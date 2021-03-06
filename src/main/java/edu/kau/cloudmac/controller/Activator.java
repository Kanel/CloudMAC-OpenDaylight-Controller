package edu.kau.cloudmac.controller;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.routing.IRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator extends ComponentActivatorAbstractBase
{
	private static final Logger log = LoggerFactory.getLogger(Activator.class);

	@Override
	public Object[] getImplementations()
	{
		log.trace("Getting Implementations");

		Object[] res = { PacketHandler.class };
		return res;
	}

	@Override
	public void configureInstance(Component c, Object imp, String containerName)
	{
		log.trace("Configuring instance");

		if (imp.equals(PacketHandler.class))
		{
			// Define exported and used services for PacketHandler component.

			Dictionary<String, Object> props = new Hashtable<String, Object>();
			props.put("salListenerName", "mypackethandler");

			// Export IListenDataPacket interface to receive packet-in events.
			c.setInterface(new String[] {IListenDataPacket.class.getName()}, props);

			// Need the DataPacketService for encoding, decoding, sending data packets
			c.add(createContainerServiceDependency(containerName).setService(IDataPacketService.class).setCallbacks("setDataPacketService", "unsetDataPacketService").setRequired(true));

			// Need IRouting to find path between nodes.
			c.add(createContainerServiceDependency(containerName).setService(IRouting.class).setCallbacks("setRouting", "unsetRouting").setRequired(true));

			// Need IFlowProgrammerService for programming paths between node connectors.
			c.add(createContainerServiceDependency(containerName).setService(IFlowProgrammerService.class).setCallbacks("setIFlowProgrammerService", "unsetIFlowProgrammerService").setRequired(true));

			// Need OvsdbConfigurationService for querying OVSDB.
			//c.add(createContainerServiceDependency(containerName).setService(OVSDBConfigService.class).setCallbacks("setOVSDBConfigService", "unsetOVSDBConfigService").setRequired(true));
		}
	}
}
