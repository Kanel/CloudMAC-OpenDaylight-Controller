package edu.kau.cloudmac.controller;

import edu.kau.ini.Parser;
import java.util.Arrays;
import java.util.Map;

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
	private CloudMacConfig config;

	// TODO: keep track of the number of acking interfaces in use by each WTP.
	public PacketHandler()
	{
		accessPoints = new AccessPointManager();
		tunnels = new TunnelManager();
		wtps = new WirelessTerminationPointManager();
		flowUtil = new FlowUtility(null, null);
		config = new CloudMacConfig();
		
		loadConfig();
	}
	
	void loadConfig()
	{		
		Map<String, Map<String, String>> settingsaaaaa = Parser.parse("./configuration/cloudmac.ini");
		Map<String, String> settings = settingsaaaaa.get("cloudmac");
		
		log.info("Configuring:");
		
		// ------------------------------------------------
		// Flow Duration
		// ------------------------------------------------
		if (settings.containsKey("flowDuration"))
		{
			config.setFlowDuration((short) Integer.parseInt(settings.get("flowDuration")));
		}
		else
		{
			config.setFlowDuration((short) 120);
			
			log.warn("Missing config: flowDuration");
		}	
		log.info("flowDuration: {}", config.getFlowDuration());
		
		// ------------------------------------------------
		// Block Flow Duration
		// ------------------------------------------------
		if (settings.containsKey("blockFlowDuration"))
		{
			config.setBlockFlowduration((short) Integer.parseInt(settings.get("blockFlowDuration")));
		}
		else
		{
			config.setBlockFlowduration((short) 2);
			
			log.warn("Missing config: blockFlowDuration");
		}
		log.info("blockFlowDuration: {}", config.getBlockFlowDuration());
		
		// ------------------------------------------------
		// Grace Duration
		// ------------------------------------------------
		if (settings.containsKey("graceDuration"))
		{
			config.setGraceDuration((short) Integer.parseInt(settings.get("graceDuration")));
		}
		else
		{
			config.setGraceDuration((short) 10);
			
			log.warn("Missing config: graceDuration");
		}
		log.info("graceDuration: {}", config.getGraceDuration());
		
		// ------------------------------------------------
		// Access Point Expiration
		// ------------------------------------------------
		if (settings.containsKey("accessPointExpiration"))
		{
			config.setAccessPointExpiration(Integer.parseInt(settings.get("accessPointExpiration")));
		}
		else
		{
			config.setAccessPointExpiration(160000);
			
			log.warn("Missing config: accessPointExpiration");
		}
		log.info("accessPointExpiration: {}", config.getAccessPointExpiration());
		
		// ------------------------------------------------
		// Tunnel Expiration
		// ------------------------------------------------
		if (settings.containsKey("tunnelExpiration"))
		{
			config.setTunnelExpiration(Integer.parseInt(settings.get("tunnelExpiration")));
		}
		else
		{
			config.setTunnelExpiration(120000);
			
			log.warn("Missing config: tunnelExpiration");
		}
		log.info("tunnelExpiration: {}", config.getTunnelExpiration());
		
		// ------------------------------------------------
		// Termination Point Expiration
		// ------------------------------------------------
		if (settings.containsKey("terminationPointExpiration"))
		{
			config.setTerminationPointExpiration(Integer.parseInt(settings.get("terminationPointExpiration")));
		}
		else
		{
			config.setTerminationPointExpiration(120000);
			
			log.warn("Missing config: terminationPointExpiration");
		}
		log.info("terminationPointExpiration: {}", config.getTerminationPointExpiration());
		
		// ------------------------------------------------
		// Beacon Flow Duration
		// ------------------------------------------------
		if (settings.containsKey("beaconFlowDuration"))
		{
			config.setBeaconFlowDuration(Integer.parseInt(settings.get("beaconFlowDuration")));
		}
		else
		{
			config.setBeaconFlowDuration(10000);
			
			log.warn("Missing config: beaconFlowDuration");
		}
		log.info("beaconFlowDuration: {}", config.getBeaconFlowDuration());
		
		// ------------------------------------------------
		// Ethernet Types
		// ------------------------------------------------
		if (settings.containsKey("ethernetTypes"))
		{
			String[] types = settings.get("ethernetTypes").split(":");
			short[] ethernetTypes = new short[4];
			
			ethernetTypes[0] = Short.parseShort(types[0]);
			ethernetTypes[1] = Short.parseShort(types[1]);
			ethernetTypes[2] = Short.parseShort(types[2]);
			ethernetTypes[3] = Short.parseShort(types[3]);
			
			config.setEthernetTypes(ethernetTypes);
		}
		else
		{
			config.setEthernetTypes(new short[] { 0x1336, 0x1337, 0x1338, 0x1339 });
			
			log.warn("Missing config: ethernetTypes");
		}
		log.info("ethernetTypes: {}", config.getEthernetTypes());
		
		// ------------------------------------------------
		// Queue Indices
		// ------------------------------------------------
		if (settings.containsKey("queueIndices"))
		{
			String[] indices = settings.get("queueIndices").split(":");
			short[] queueIndices = new short[4];
			
			queueIndices[0] = Short.parseShort(indices[0]);
			queueIndices[1] = Short.parseShort(indices[1]);
			queueIndices[2] = Short.parseShort(indices[2]);
			queueIndices[3] = Short.parseShort(indices[3]);
			
			config.setQueueIndices(queueIndices);
		}
		else
		{
			config.setQueueIndices(new short[] { 1, 2, 3, 4 });
			
			log.warn("Missing config: queueIndices");
		}
		log.info("queueIndices: {}", config.getQueueIndices());
		
		// ------------------------------------------------
		// Block Priority
		// ------------------------------------------------
		if (settings.containsKey("blockPriority"))
		{
			config.setBlockPriority(Short.parseShort(settings.get("blockPriority")));
		}
		else
		{
			config.setBlockPriority((short) 1200);
			
			log.warn("Missing config: blockPriority");
		}
		log.info("blockPriority: {}", config.getBlockPriority());
		
		// ------------------------------------------------
		// Tunnel Priority
		// ------------------------------------------------		
		if (settings.containsKey("tunnelPriority"))
		{
			config.settTunnelPriority(Short.parseShort(settings.get("tunnelPriority")));
		}
		else
		{
			config.settTunnelPriority((short) 1100);
			
			log.warn("Missing config: tunnelPriority");
		}
		log.info("tunnelPriority: {}", config.getTunnelPriority());
		
		// ------------------------------------------------
		// Beacon Priority
		// ------------------------------------------------
		if (settings.containsKey("beaconPriority"))
		{
			config.setBeaconPriority(Short.parseShort(settings.get("beaconPriority")));
		}
		else
		{
			config.setBeaconPriority((short) 1000);
			
			log.warn("Missing config: beaconPriority");
		}
		log.info("beaconPriority: {}", config.getBeaconPriority());
		
		// ------------------------------------------------
		// Termination Point Configuration Port
		// ------------------------------------------------
		if (settings.containsKey("terminationPointConfigPort"))
		{
			config.setTerminationPointConfigPort(Short.parseShort(settings.get("terminationPointConfigPort")));
		}
		else
		{
			config.setTerminationPointConfigPort((short) 1999);
			
			log.warn("Missing config: terminationPointConfigPort");
		}
		log.info("terminationPointConfigPort: {}", config.getTerminationPointConfigPort());
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

	private boolean isPartOfTest(byte[] source, byte[] destination)
	{
		byte[] mobileClient1 = new byte[] { 0, 38, 90, 11, 54, 124 }; // D-Link (mine)
		byte[] mobileClient2 = new byte[] { 0x74, 0x2f, 0x68, (byte) 0xd2, 0x43, 0x17 }; // Jonathan's laptop
		byte[] vap = new byte[] { 0x0a, 0x0b };
		byte[] broadcast = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };

		return 	Arrays.equals(source, mobileClient1) 
			 || Arrays.equals(source, mobileClient2)
			 || Arrays.equals(destination, mobileClient1) 
			 || Arrays.equals(destination, mobileClient2);
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
			Ethernet ethPkt = (Ethernet)l2pkt;
			// Source and Destination should properly be retrieved from the frame rather than the Ethernet header.
			byte[] sourceMac = ethPkt.getSourceMACAddress();
			byte[] destinationMac = ethPkt.getDestinationMACAddress();
			byte[] broadcast = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };

			// Special announce packet that reveals the presence of a WTP and which IP that can be used to configure it.
			if (Arrays.equals(sourceMac, broadcast) && Arrays.equals(destinationMac, broadcast))
			{
				WirelessTerminationPoint wtp;
				String ip = new String(l2pkt.getRawPayload());

				if (wtps.contains(ingressConnector))
				{
					log.trace("Reseting WTP expiration {}, config IP {}", ingressConnector, ip);

					wtp = wtps.get(ingressConnector);
					wtp.setExpiration(System.currentTimeMillis() + config.getTerminationPointExpiration());
				}
				// New WTP discovered.
				else
				{
					log.info("Discovered new WTP {}, config IP {}", ingressConnector, ip);

					wtp = wtps.add(ingressConnector, System.currentTimeMillis() + config.getTerminationPointExpiration());
				}
				wtp.setIP(ip);
			}
			else
			{
				FrameTypes frameType = FrameTypes.lookup(CloudMACPacket.getFrameType(inPkt));

				log.trace("CloudMAC packet recieved, frame type: {}, {}", frameType, CloudMACPacket.getFrameType(inPkt));

				// Handle Routing, and WTP, and VAP discovery.
				switch (frameType)
				{
				case Management_Beacon:
					if (accessPoints.contains(CloudMACPacket.getAddress3(inPkt)))
					{
						log.trace("Reseting access point expiration {}/{}.", ingressConnector, sourceMac);

						// Refresh access point status.
						accessPoints.get(sourceMac).setExpiration(System.currentTimeMillis() + config.getAccessPointExpiration());
					}
					else
					{
						log.info("Discovered new access point {}/{}.", ingressConnector, sourceMac);

						// New access point discovered.

						accessPoints.add(ingressConnector, CloudMACPacket.getAddress3(inPkt), System.currentTimeMillis() + config.getAccessPointExpiration());
					}

					// Route beacons to WTPs to allow their presence to be easily detected, only if the access point isn't in use.
					if (!tunnels.containsAccessPoint(sourceMac))
					{
						if (wtps.hasFree())
						{
							WirelessTerminationPoint wtp =  wtps.allocate(System.currentTimeMillis() + config.getTerminationPointExpiration());

							log.trace("Adding beacon tunnel {}", sourceMac, destinationMac);

							flowUtil.createBeaconTunnel(ingressConnector, wtp.getConnector(), sourceMac, config.getEthernetTypes()[3], config.getBeaconPriority(), config.getQueueIndices()[3], config.getFlowDuration());
						}
						else
						{
							log.trace("Blocking beacon frames from access point({}).", sourceMac);

							flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 10), config.getBlockFlowDuration());
						}
					}
					break;

				case Management_Probe_Request:
					if (!isPartOfTest(sourceMac, destinationMac))
					{
						log.trace("Not part of test, blocking frames from {} to {}.", sourceMac, destinationMac);

						flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 9), config.getBlockFlowDuration());

						break;
					}

					if (!tunnels.contains(sourceMac))
					{
						if (accessPoints.hasFree())
						{
							AccessPoint ap = accessPoints.allocate(System.currentTimeMillis() + config.getTunnelExpiration());
							WirelessTerminationPoint wtp = wtps.get(ingressConnector);

							log.trace("Adding tunnel {} <-> {}", sourceMac, ap.getMacAdress());

							tunnels.add(ingressConnector, sourceMac, ap, System.currentTimeMillis() + config.getTunnelExpiration());
							flowUtil.createTunnel(ingressConnector, ap.getConnector(), sourceMac, ap.getMacAdress(), config.getEthernetTypes(), config.getQueueIndices(), config.getTunnelPriority(), config.getFlowDuration(), config.getGraceDuration());
							flowUtil.ActivateAckingAsync(wtp.getIP(), config.getTerminationPointConfigPort(), ap.getMacAdress(), config.getFlowDuration() * 1000);
						}
						else
						{
							log.warn("No available access point.");
						}
					}
					break;

				case Management_Association_request:
					if (!isPartOfTest(sourceMac, destinationMac))
					{
						log.trace("Not part of test, blocking frames from {} to {}.", sourceMac, destinationMac);

						flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 8), config.getBlockFlowDuration());

						break;
					}

					if (tunnels.contains(sourceMac))
					{
						byte[] mac = tunnels.get(sourceMac).getAccessPoint().getMacAdress();

						if (destinationMac[2] != mac[2] ||
							destinationMac[3] != mac[3] ||
							destinationMac[4] != mac[4] ||
							destinationMac[5] != mac[5])
						{
							log.trace("Blocking frames from {} to {}.", sourceMac, destinationMac);

							flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 7), config.getBlockFlowDuration());
						}
						// TODO: Handle case of client trying to connect to another VAP. For now just block it.
					}
					else
					{
						if (accessPoints.contains(destinationMac))
						{
							AccessPoint accessPoint = accessPoints.get(destinationMac);
							WirelessTerminationPoint wtp = wtps.get(ingressConnector);

							if (!accessPoint.isAllocated())
							{
								log.trace("Creating tunnel {} <-> {}", sourceMac, accessPoint.getMacAdress());

								accessPoint.allocate(System.currentTimeMillis() + config.getTunnelExpiration());
								tunnels.add(ingressConnector, sourceMac, accessPoint, System.currentTimeMillis() + config.getTunnelExpiration());
								flowUtil.createTunnel(ingressConnector, accessPoint.getConnector(), sourceMac, accessPoint.getMacAdress(), config.getEthernetTypes(), config.getQueueIndices(), config.getTunnelPriority(), config.getFlowDuration(), config.getGraceDuration());
								flowUtil.ActivateAckingAsync(wtp.getIP(), config.getTerminationPointConfigPort(), destinationMac, config.getFlowDuration() * 1000);
							}
							else
							{
								log.trace("Blocking frames from {} to {}.", sourceMac, destinationMac);

								// Access point is in use, nothing we can do other than ignore the packets.
								flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 1), config.getBlockFlowDuration());
							}
						}
						else
						{
							log.trace("Blocking frames from {} to {}.", sourceMac, destinationMac);

							// This is not our problem, simply block these packets.
							flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 2), config.getBlockFlowDuration());
						}
					}
					break;

				default:
					// Does the traffic belong to a known tunnel?
					if (tunnels.contains(sourceMac) && accessPoints.contains(destinationMac))
					{
						MobileTerminalTunnel tunnel = tunnels.get(sourceMac);
						AccessPoint accessPoint = accessPoints.get(destinationMac);
						WirelessTerminationPoint wtp = wtps.get(ingressConnector);

						if (tunnel.getAccessPoint().equals(accessPoint))
						{
							if (tunnel.getSource().getConnector().equals(ingressConnector)) // Is the frame coming from the same WTP as before?
							{
								long expiration = System.currentTimeMillis();

								log.trace("Extedning tunnel lease {} <-> {}", sourceMac, destinationMac);

								// Extend tunnel "lease".
								accessPoint.setAllocationExpiration(expiration + config.getTunnelExpiration());
								tunnel.setExpiration(expiration + config.getTunnelExpiration());
								flowUtil.createTunnel(ingressConnector, accessPoint.getConnector(), sourceMac, accessPoint.getMacAdress(), config.getEthernetTypes(), config.getQueueIndices(), config.getTunnelPriority(), config.getFlowDuration(), config.getGraceDuration());
								flowUtil.ActivateAckingAsync(wtp.getIP(), config.getTerminationPointConfigPort(), destinationMac, config.getFlowDuration() * 1000);
							}
							else
							{
								// TODO: Handle handover between known WTPs.
							}
						}
						else
						{
							log.trace("Blocking frames from {} to {}.", sourceMac, destinationMac);

							// Client tries to send data to a VAP it's not associated with, not allowed, block it.
							flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 4), config.getBlockFlowDuration());
						}
					}
					else
					{
						log.trace("Blocking frames from {} to {}.", sourceMac, destinationMac);

						// Unknown traffic possibly from another network, you can end up here if a tunnel times out but the client or access point still sends traffic.
						flowUtil.block(ingressConnector, sourceMac, destinationMac, config.getEthernetTypes(), (short)(config.getBlockPriority() + 5), config.getBlockFlowDuration());
					}
					break;
				}
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
