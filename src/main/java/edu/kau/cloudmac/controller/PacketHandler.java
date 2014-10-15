package edu.kau.cloudmac.controller;

import java.util.Arrays;

import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.sal.routing.IRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler implements IListenDataPacket
{
	private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);
	private IDataPacketService dataPacketService;
	private IRouting routing;
	private IFlowProgrammerService flowProgrammer;
	private AccessPointManager accessPoints;
	private TunnelManager tunnels;
	private WirelessTerminationPointManager wtps;
	private FlowUtility flowUtil;
	private final short CLOUDMAC_ETHERNET_TYPE;
	private final short CLOUDMAC_FLOW_TIME;
	private final short CLOUDMAC_FLOW_BLOCK_TIME;
	private final short CLOUDMAC_FLOW_GRACETIME;
	private final long CLOUDMAC_ACCESS_POINT_EXPIRATION;
	private final long CLOUDMAC_ACCESS_POINT_EXPIRATION_GRACE;
	private final long CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION;
	private final long CLOUDMAC_WIRELESS_TERMINATION_POINT_EXPIRATION;
	private final long CLOUDMAC_WIRELESS_TERMINATION_POINT_BEACON_EXPIRATION;

	public PacketHandler()
	{
		accessPoints = new AccessPointManager();
		tunnels = new TunnelManager();
		wtps = new WirelessTerminationPointManager();
		flowUtil = new FlowUtility(null, null);
		CLOUDMAC_ETHERNET_TYPE = 0x1337;
		CLOUDMAC_FLOW_TIME = 10;
		CLOUDMAC_FLOW_BLOCK_TIME = 2;
		CLOUDMAC_FLOW_GRACETIME = 4;
		CLOUDMAC_ACCESS_POINT_EXPIRATION = 16000;
		CLOUDMAC_ACCESS_POINT_EXPIRATION_GRACE = 4000;
		CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION = 15000;
		CLOUDMAC_WIRELESS_TERMINATION_POINT_EXPIRATION = 10000;
		CLOUDMAC_WIRELESS_TERMINATION_POINT_BEACON_EXPIRATION = 10000;
	}

	void setDataPacketService(IDataPacketService s)
	{
		log.trace("Set DataPacketService.");

		dataPacketService = s;
	}

	void unsetDataPacketService(IDataPacketService s)
	{
		log.trace("Removed DataPacketService.");

		if (dataPacketService == s)
		{
			dataPacketService = null;
		}
	}

	void setRouting(IRouting r)
    {
        log.trace("Set IRouting.");

        routing = r;

        flowUtil.setRouting(r);
    }

	void unsetRouting(IRouting r)
    {
        log.trace("Removed IRouting.");

        if (routing == r)
        {
        	routing = null;

        	flowUtil.unsetRouting();
        }
    }

	void setIFlowProgrammerService(IFlowProgrammerService fp)
    {
        log.trace("Set IFlowProgrammerService.");

        flowProgrammer = fp;

        flowUtil.setIFlowProgrammerService(fp);
    }

	void unsetIFlowProgrammerService(IFlowProgrammerService fp)
    {
        log.trace("Removed IFlowProgrammerService.");

        if (flowProgrammer == fp)
        {
			flowProgrammer = null;

			flowUtil.unsetIFlowProgrammerService();
        }
    }

	private boolean isPartOfTest(byte[] mac)
	{
		byte[] mobileClient1 = new byte[] { 0, 38, 90, 11, 54, 124 };

		return Arrays.equals(mac, mobileClient1);
	}

	@Override
	public PacketResult receiveDataPacket(RawPacket inPkt)
	{
		// We need these three interfaces.
		if (dataPacketService == null)
			return PacketResult.IGNORED;
		if (routing == null)
			return PacketResult.IGNORED;
		if (flowProgrammer == null)
			return PacketResult.IGNORED;

		Packet l2pkt = dataPacketService.decodeDataPacket(inPkt);

		if (CloudMACPacket.isCloudMAC(l2pkt))
		{
       		NodeConnector ingressConnector = inPkt.getIncomingNodeConnector();
			FrameTypes frameType = FrameTypes.lookup(CloudMACPacket.getFrameType(inPkt));
			Ethernet ethPkt = (Ethernet)l2pkt;
			byte[] sourceMac = ethPkt.getSourceMACAddress();
			byte[] destinationMac = ethPkt.getDestinationMACAddress();

			log.trace("CloudMAC packet recieved, frame type: {}, {}", frameType, CloudMACPacket.getFrameType(inPkt));

			// Handle Routing, and WTP, and VAP discovery.
			switch (frameType)
			{
			case Management_Beacon:
				if (accessPoints.contains(sourceMac))
				{
					log.info("CloudMAC: Reseting access point expiration {}/{}.", ingressConnector, sourceMac);

					// Refresh access point status.
					accessPoints.get(sourceMac).setExpiration(System.currentTimeMillis() + CLOUDMAC_ACCESS_POINT_EXPIRATION);
				}
				else
				{
					log.info("CloudMAC: Discovered new access point {}/{}.", ingressConnector, sourceMac);

					// New access point discovered.
					accessPoints.add(ingressConnector, sourceMac, System.currentTimeMillis() + CLOUDMAC_ACCESS_POINT_EXPIRATION);
				}

				// Route beacons to WTPs to allow their presence to be easily detected.
				if (wtps.hasFree())
				{
					WirelessTerminationPoint wtp =  wtps.allocate(System.currentTimeMillis() + CLOUDMAC_WIRELESS_TERMINATION_POINT_BEACON_EXPIRATION);

					log.trace("CloudMAC: Adding beacon tunnel {}", sourceMac, destinationMac);

					flowUtil.createBeaconTunnel(ingressConnector, wtp.getConnector(), sourceMac, CLOUDMAC_FLOW_TIME);
				}
				else
				{
					log.trace("CloudMAC: Blocking beacon frames from access point({}).", sourceMac);

					flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
				}
				break;

			case Management_Probe_Request:
				if (!tunnels.contains(sourceMac))
				{
					if (accessPoints.hasFree())
					{
						AccessPoint ap = accessPoints.allocate(System.currentTimeMillis() + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);

						log.trace("CloudMAC: Adding tunnel {} <-> {}", sourceMac, ap.getMacAdress());

						tunnels.add(ingressConnector, sourceMac, ap, System.currentTimeMillis() + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);
						flowUtil.createTunnel(ingressConnector, ap.getConnector(), sourceMac, ap.getMacAdress(), CLOUDMAC_FLOW_TIME, CLOUDMAC_FLOW_GRACETIME);
					}
					else
					{
						log.trace("CloudMAC: No available access point.");
					}
				}
				break;

			case Management_Association_request:
				if (tunnels.contains(sourceMac))
				{
					byte[] mac = tunnels.get(sourceMac).getAccessPoint().getMacAdress();

					if (destinationMac[2] != mac[2] ||
						destinationMac[3] != mac[3] ||
						destinationMac[4] != mac[4] ||
						destinationMac[5] != mac[5])
					{
						log.trace("CloudMAC: Blocking frames from {} to {}.", sourceMac, destinationMac);

						flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
					}
					// TODO: Handle case of client trying to connect to another VAP. For now just block it.
				}
				else
				{
					if (accessPoints.contains(destinationMac))
					{
						AccessPoint accessPoint = accessPoints.get(destinationMac);

						if (!accessPoint.isAllocated())
						{
							log.trace("CloudMAC: Creating tunnel {} <-> {}", sourceMac, accessPoint.getMacAdress());

							accessPoint.allocate(System.currentTimeMillis() + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);
							tunnels.add(ingressConnector, sourceMac, accessPoint, System.currentTimeMillis() + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);
							flowUtil.createTunnel(ingressConnector, accessPoint.getConnector(), sourceMac, accessPoint.getMacAdress(), CLOUDMAC_FLOW_TIME, CLOUDMAC_FLOW_GRACETIME);
						}
						else
						{
							log.trace("CloudMAC: Blocking frames from {} to {}.", sourceMac, destinationMac);

							// Access point is in use, nothing we can do other than ignore the packets.
							flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
						}
					}
					else
					{
						log.trace("CloudMAC: Blocking frames from {} to {}.", sourceMac, destinationMac);

						// This is not our problem, simply block these packets.
						flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
					}
				}
				break;

			default:
				// Keep track of WTPs in the network.
				if (frameType != FrameTypes.Management_Beacon && !accessPoints.contains(sourceMac)) // Keep track of active WTPs.
				{
					if (wtps.contains(ingressConnector))
					{
						log.info("CloudMAC: Reseting WTP expiration {}.", ingressConnector);

						wtps.get(ingressConnector).setExpiration(System.currentTimeMillis() + CLOUDMAC_WIRELESS_TERMINATION_POINT_EXPIRATION);
					}
					// New WTP discovered.
					else
					{
						log.info("CloudMAC: Discovered new WTP {}.", ingressConnector);

						wtps.add(ingressConnector, System.currentTimeMillis() + CLOUDMAC_WIRELESS_TERMINATION_POINT_EXPIRATION);
					}
				}

				// Does the traffic belong to a known tunnel?
				if (tunnels.contains(sourceMac) && accessPoints.contains(destinationMac))
				{
					MobileTerminalTunnel tunnel = tunnels.get(sourceMac);
					AccessPoint accessPoint = accessPoints.get(destinationMac);

					if (tunnel.getAccessPoint().equals(accessPoint))
					{
						if (tunnel.getSource().getConnector().equals(ingressConnector)) // Is the frame coming from the same WTP as before?
						{
							long expiration = System.currentTimeMillis();

							log.trace("CloudMAC: Extedning tunnel lease {} <-> {}", sourceMac, destinationMac);

							// Extend tunnel "lease".
							accessPoint.setAllocationExpiration(expiration + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);
							tunnel.setExpiration(expiration + CLOUDMAC_MOBILE_TERMINAL_TUNNEL_EXPIRATION);
							flowUtil.createTunnel(ingressConnector, accessPoint.getConnector(), sourceMac, accessPoint.getMacAdress(), CLOUDMAC_FLOW_TIME, CLOUDMAC_FLOW_GRACETIME);
						}
						else
						{
							// TODO: Handle handover between known WTPs.
						}
					}
					else
					{
						log.trace("CloudMAC: Blocking frames from {} to {}.", sourceMac, destinationMac);

						// Client tries to send data to a VAP it's not associated with, not allowed, block it.
						flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
					}
				}
				else
				{
					log.trace("CloudMAC:3 Blocking frames from {} to {}.", sourceMac, destinationMac);

					// Unknown traffic possibly from another network, you can end up here if a tunnel times out but the client or access point still sends traffic.
					flowUtil.block(ingressConnector, sourceMac, destinationMac, CLOUDMAC_FLOW_BLOCK_TIME);
				}
				break;
			}

			// TODO: Handle CloudMAC controller crash.
			return PacketResult.CONSUME;
		}
		else
		{
			// Not our problem!
			return PacketResult.IGNORED;
		}
	}
}
