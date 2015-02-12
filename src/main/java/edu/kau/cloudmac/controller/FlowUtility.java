package edu.kau.cloudmac.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Controller;
import org.opendaylight.controller.sal.action.Drop;
import org.opendaylight.controller.sal.action.Enqueue;
import org.opendaylight.controller.sal.core.Edge;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Path;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.routing.IRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowUtility
{
	IFlowProgrammerService flowProgrammer;
	IRouting routing;
	private static final Logger log = LoggerFactory.getLogger(FlowUtility.class);

	public FlowUtility(IFlowProgrammerService flowProgrammer, IRouting routing)
	{
		this.flowProgrammer = flowProgrammer;
		this.routing = routing;
	}

	void setRouting(IRouting routing)
    {
        this.routing = routing;
    }

	void unsetRouting()
    {
        this.routing = null;
    }

	void setIFlowProgrammerService(IFlowProgrammerService flowProgrammer)
    {
        this.flowProgrammer = flowProgrammer;
    }

	void unsetIFlowProgrammerService()
    {
        this.flowProgrammer = null;
    }

	// Creates a list of NodeConenctors that form a path from a to b.
	private ArrayList<NodeConnector> getRoute(NodeConnector a, NodeConnector b)
	{
		ArrayList<NodeConnector> list = new ArrayList<NodeConnector>();
		List<Edge> edges;

		if (a.getNode().equals(b.getNode()))
		{
			edges = new ArrayList<Edge>();
		}
		else
		{
			Path path = routing.getRoute(a.getNode(), b.getNode());

			if (path != null)
			{
				edges = path.getEdges();
			}
			else
			{
				return null;
			}
		}
		list.add(a);

		for (Edge edge : edges)
		{
			list.add(edge.getTailNodeConnector());
			list.add(edge.getHeadNodeConnector());
		}
		list.add(b);

		return list;
	}
	
	// Creates a flow that matches the parameters.
	private Flow createFlow(NodeConnector inPort, short etherType, byte[] source, byte[] sourceMask, byte[] destination, byte[] destinationMask, short priority, short timeout)
	{
		Match match = new Match();
		Flow flow = new Flow();
		
		match = new Match();
		match.setField(MatchType.IN_PORT, inPort);
		match.setField(MatchType.DL_TYPE, etherType);
		
		if (source!= null)
		{
			if (sourceMask != null)
			{
				match.setField(MatchType.DL_SRC, source, sourceMask);
			}
			else
			{
				match.setField(MatchType.DL_SRC, source);
			}
		}
		
		if (destination != null)
		{
			if (destinationMask != null)
			{
				match.setField(MatchType.DL_DST, destination, destinationMask);
			}
			else 
			{
				match.setField(MatchType.DL_DST, destination);
			}
		}

		flow.setActions(new ArrayList<Action>());
		flow.setMatch(match);
		flow.setPriority(priority);
		flow.setHardTimeout(timeout);

		return flow;
	}
	
	public FlowUtilityResult createTunnel(NodeConnector a, NodeConnector b, byte[] mtMac, byte[] apMac, short[] ethernetTypes, short[] queueIndices, short priority, short timeout, short graceTime)
	{
		if (ethernetTypes.length != queueIndices.length)
			return FlowUtilityResult.ARGUMENT_MISSMATCH;
		
		for (int i = 0; i < ethernetTypes.length; i++)
		{
			FlowUtilityResult result = createTunnel(a, b, mtMac, apMac, ethernetTypes[i], queueIndices[i], priority, timeout, graceTime);
			
			if (result != FlowUtilityResult.OK)
			{
				return result;
			}
		}
		return FlowUtilityResult.OK;
	}

	// Add all necessary flows between a mobile terminal and a access point.
	// 
	private FlowUtilityResult createTunnel(NodeConnector a, NodeConnector b, byte[] mtMac, byte[] apMac, short ethernetType, short queueIndex, short priority, short timeout, short graceTime)
	{
		if (routing == null)
			return FlowUtilityResult.NO_I_ROUTING;
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;

		ArrayList<NodeConnector> connectors = getRoute(a, b);
		Flow forward;
		Flow backward;
		Flow beacon;
		byte[] mask = new byte[]{ (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF }; // The first 2 bytes can dynamically change.
		byte[] broadcast = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
		byte[] apMac2 = apMac.clone();
		
		// Todo: hope that opendaylight gets fixed.
		apMac[0] = 0x00;
		apMac[1] = 0x6c;
		apMac2[0] = 0x0a;
		apMac2[1] = 0x0b;

		if (connectors == null)
		{
			log.warn("No path connecting {} and {}", a.getNode(), b.getNode());

			return FlowUtilityResult.NO_PATH;
		}

		// Initial node keeps track if the connection is active.
		// Forward flow.
		forward = createFlow(connectors.get(0), ethernetType, mtMac, null, apMac2, null, (short)(priority + 1), (short)(timeout - graceTime));
		forward.addAction(new Enqueue(connectors.get(1), 1));

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// Forward flow.
		forward = createFlow(connectors.get(0), ethernetType, mtMac, null, broadcast, null, priority, timeout);
		forward.addAction(new Enqueue(connectors.get(1), queueIndex));

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// This should cause the flows to be recreated before they timeout.
		forward = createFlow(connectors.get(0), ethernetType, mtMac, null, apMac, null, priority, timeout);		
		forward.addAction(new Enqueue(connectors.get(1), queueIndex));
		forward.addAction(new Controller());
		

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// Backward flow.
		backward = createFlow(connectors.get(1), ethernetType, apMac, null, mtMac, null, priority, timeout);
		backward.addAction(new Enqueue(connectors.get(0), queueIndex));

		flowProgrammer.addFlow(connectors.get(0).getNode(), backward);

		// Backward flow.
		backward = createFlow(connectors.get(1), ethernetType, apMac, null, broadcast, null, priority, timeout);
		backward.addAction(new Enqueue(connectors.get(0), queueIndex));

		flowProgrammer.addFlow(connectors.get(0).getNode(), backward);

		// The rest of the flows.
		for (int i = 2; i < connectors.size(); i += 2)
		{
			// Forward flow.
			forward = createFlow(connectors.get(i), ethernetType, mtMac, null, apMac2, null, priority, timeout);
			forward.addAction(new Enqueue(connectors.get(i + 1), 1));

			flowProgrammer.addFlow(connectors.get(i).getNode(), forward);

			// Forward flow.
			forward = createFlow(connectors.get(i), ethernetType, mtMac, null, broadcast, null, priority, timeout);
			forward.addAction(new Enqueue(connectors.get(i + 1), queueIndex));

			flowProgrammer.addFlow(connectors.get(i).getNode(), forward);

			// Backward flow.
			backward = createFlow(connectors.get(i + 1), ethernetType, apMac, null, mtMac, null, priority, timeout);
			backward.addAction(new Enqueue(connectors.get(i), queueIndex));

			flowProgrammer.addFlow(connectors.get(i).getNode(), backward);

			// Backward flow.
			backward = createFlow(connectors.get(i + 1), ethernetType, apMac, null, broadcast, null, priority, timeout);
			backward.addAction(new Enqueue(connectors.get(i), queueIndex));

			flowProgrammer.addFlow(connectors.get(i).getNode(), backward);
		}

		// Beacon frames have to reach the controller periodically.
		beacon = createFlow(connectors.get(connectors.size() - 1), ethernetType, apMac, null, broadcast, null, (short)(priority + 1), (short)(timeout - graceTime));
		beacon.addAction(new Enqueue(connectors.get(connectors.size() - 2), queueIndex));

		flowProgrammer.addFlow(connectors.get(connectors.size() - 1).getNode(), beacon);

		// Beacon frames have to reach the controller periodically.
		beacon = createFlow(connectors.get(connectors.size() - 1), ethernetType, apMac, null, broadcast, null, (short)(priority + 1), timeout);
		beacon.addAction(new Enqueue(connectors.get(connectors.size() - 2), queueIndex));
		beacon.addAction(new Controller());

		flowProgrammer.addFlow(connectors.get(connectors.size() - 1).getNode(), beacon);

		return FlowUtilityResult.OK;
	}

	// Add all necessary flows between a mobile terminal and a access point.
	public FlowUtilityResult createBeaconTunnel(NodeConnector a, NodeConnector b, byte[] accessPointMac, short ethernetType, short priority, short queueIndex, short timeout)
	{
		if (routing == null)
			return FlowUtilityResult.NO_I_ROUTING;
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;

		ArrayList<NodeConnector> connectors = getRoute(a, b);
		Flow flow;
		byte[] mask = new byte[]{ 0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }; // The first 2 bytes can dynamically change.

		if (connectors == null)
		{
			log.warn("No path connecting {} and {}", a.getNode(), b.getNode());

			return FlowUtilityResult.NO_PATH;
		}

		for (int i = 0; i < connectors.size(); i += 2)
		{
			flow = createFlow(connectors.get(i), ethernetType, accessPointMac, mask, null, null, priority, timeout);
			flow.addAction(new Enqueue(connectors.get(i + 1), queueIndex));

			flowProgrammer.addFlow(connectors.get(i).getNode(), flow);
		}
		return FlowUtilityResult.OK;
	}

	private FlowUtilityResult block(NodeConnector connector, byte[] sourceMac, byte[] destiantionMac, short ethernetType, short priority, short timeout)
	{
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;
		
		Flow flow = createFlow(connector, ethernetType, sourceMac, null, destiantionMac, null, priority, timeout);
		
		flow.addAction(new Drop());

		flowProgrammer.addFlow(connector.getNode(), flow);

		return FlowUtilityResult.OK;
	}
	
	public FlowUtilityResult block(NodeConnector connector, byte[] sourceMac, byte[] destiantionMac, short[] ethernetType, short priority, short timeout)
	{
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;
		
		for (int i = 0; i < ethernetType.length; i++)
		{
			FlowUtilityResult result = block(connector, sourceMac, destiantionMac, ethernetType[i], priority, timeout);
			
			if (result != FlowUtilityResult.OK)
			{
				return result;
			}
		}
		return FlowUtilityResult.OK;
	}

	public void ActivateAckingAsync(String hostname, int port, byte[] mac, int timeout)
	{
			AckingActivatorCallable activator = new AckingActivatorCallable(hostname, port, mac, timeout);
			FutureTask<String> futureTask = new FutureTask<String>(activator);
			ExecutorService executor = Executors.newSingleThreadExecutor();

			executor.execute(futureTask);
	}
}