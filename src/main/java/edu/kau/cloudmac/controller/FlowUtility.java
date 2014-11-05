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
	private final short CLOUDMAC_ETHERNET_TYPE;
	private static final Logger log = LoggerFactory.getLogger(FlowUtility.class);

	public FlowUtility(IFlowProgrammerService flowProgrammer, IRouting routing)
	{
		this.flowProgrammer = flowProgrammer;
		this.routing = routing;
		this.CLOUDMAC_ETHERNET_TYPE = 0x1337;
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

	// This makes the code much cleaner than when working with edges directly.
	private ArrayList<NodeConnector> getRoute(NodeConnector a, NodeConnector b)
	{
		ArrayList<NodeConnector> list = new ArrayList<NodeConnector>();
		List<Edge> edges;

		if (a.equals(b))
		{
			edges = new ArrayList<Edge>();
		}
		else
		{
			Path path = routing.getRoute(a.getNode(), b.getNode());

			edges = path.getEdges();
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

	// Add all necessary flows between a mobile terminal and a access point.
	public FlowUtilityResult createTunnel(NodeConnector a, NodeConnector b, byte[] aMac, byte[] bMac, byte[] cMac, short timeout, short graceTime)
	{
		if (routing == null)
			return FlowUtilityResult.NO_I_ROUTING;
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;

		ArrayList<NodeConnector> connectors = getRoute(a, b);
		Match matchForward;
		Match matchReverse;
		Match matchBeacon;
		Flow forward;
		Flow backward;
		Flow beacon;
		byte[] mask = new byte[]{ 1, 0, 0, 0, 0, 0 }; // The first 2 bytes can dynamically change.
		byte[] broadcast = new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
		short basePriortity = 1000;

		matchForward = new Match();
		matchReverse = new Match();

		// Initial node keeps track if the connection is active.
		// Forward flow.
		matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchForward.setField(MatchType.DL_SRC, aMac);
		matchForward.setField(MatchType.DL_DST, bMac);
		matchForward.setField(MatchType.IN_PORT, connectors.get(0));

		forward = new Flow(matchForward, new ArrayList<Action>());
		forward.addAction(new Enqueue(connectors.get(1), 1));
		forward.setPriority((short)(basePriortity + 1));
		forward.setHardTimeout((short)(timeout - graceTime));

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// Forward flow.
		matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchForward.setField(MatchType.DL_SRC, aMac);
		matchForward.setField(MatchType.DL_DST, broadcast);
		matchForward.setField(MatchType.IN_PORT, connectors.get(0));

		forward = new Flow(matchForward, new ArrayList<Action>());
		forward.addAction(new Enqueue(connectors.get(1), 1));
		forward.setPriority((short)(basePriortity + 1));
		forward.setHardTimeout((short)(timeout - graceTime));

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// This should cause the flows to be recreated before they timeout.
		// TODO: find out if both directions need to be checked!
		matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchForward.setField(MatchType.DL_SRC, aMac);
		matchForward.setField(MatchType.DL_DST, bMac);
		matchForward.setField(MatchType.IN_PORT, connectors.get(0));

		forward = new Flow(matchForward, new ArrayList<Action>());
		forward.addAction(new Enqueue(connectors.get(1), 1));
		forward.addAction(new Controller());
		forward.setPriority(basePriortity);
		forward.setHardTimeout(timeout);

		flowProgrammer.addFlow(connectors.get(0).getNode(), forward);

		// Backward flow.
		matchReverse.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchReverse.setField(MatchType.DL_SRC, cMac);
		matchReverse.setField(MatchType.DL_DST, aMac);
		matchReverse.setField(MatchType.IN_PORT, connectors.get(1));

		backward = new Flow(matchForward, new ArrayList<Action>());
		backward.setMatch(matchReverse);
		backward.addAction(new Enqueue(connectors.get(0), 1));
		backward.setPriority(basePriortity);
		backward.setHardTimeout(timeout);

		flowProgrammer.addFlow(connectors.get(0).getNode(), backward);

		// Backward flow.
		matchReverse.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchReverse.setField(MatchType.DL_SRC, cMac);
		matchReverse.setField(MatchType.DL_DST, broadcast);
		matchReverse.setField(MatchType.IN_PORT, connectors.get(1));

		backward = new Flow(matchForward, new ArrayList<Action>());
		backward.setMatch(matchReverse);
		backward.addAction(new Enqueue(connectors.get(0), 1));
		backward.setPriority(basePriortity);
		backward.setHardTimeout(timeout);

		flowProgrammer.addFlow(connectors.get(0).getNode(), backward);

		// The rest of the flows.
		for (int i = 2; i < connectors.size(); i += 2)
		{
			// Forward flow.
			matchForward = new Match();
			matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchForward.setField(MatchType.DL_SRC, aMac);
			matchForward.setField(MatchType.DL_DST, bMac);
			matchForward.setField(MatchType.IN_PORT, connectors.get(i + 0));

			forward = new Flow(matchForward, new ArrayList<Action>());
			forward.addAction(new Enqueue(connectors.get(i + 1), 1));
			forward.setPriority(basePriortity);
			forward.setHardTimeout(timeout);

			flowProgrammer.addFlow(connectors.get(i).getNode(), forward);


			// Forward flow.
			matchForward = new Match();
			matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchForward.setField(MatchType.DL_SRC, aMac);
			matchForward.setField(MatchType.DL_DST, broadcast);
			matchForward.setField(MatchType.IN_PORT, connectors.get(i + 0));

			forward = new Flow(matchForward, new ArrayList<Action>());
			forward.addAction(new Enqueue(connectors.get(i + 1), 1));
			forward.setPriority(basePriortity);
			forward.setHardTimeout(timeout);

			flowProgrammer.addFlow(connectors.get(i).getNode(), forward);

			// Backward flow.
			matchReverse = new Match();
			matchReverse.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchReverse.setField(MatchType.DL_SRC, cMac);
			matchReverse.setField(MatchType.DL_DST, aMac);
			matchReverse.setField(MatchType.IN_PORT, connectors.get(i + 1));

			backward = new Flow(matchReverse, new ArrayList<Action>());
			backward.addAction(new Enqueue(connectors.get(i + 0), 1));
			backward.setPriority(basePriortity);
			backward.setHardTimeout(timeout);

			flowProgrammer.addFlow(connectors.get(i).getNode(), backward);

			// Backward flow.
			matchReverse = new Match();
			matchReverse.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchReverse.setField(MatchType.DL_SRC, cMac);
			matchReverse.setField(MatchType.DL_DST, broadcast);
			matchReverse.setField(MatchType.IN_PORT, connectors.get(i + 1));

			backward = new Flow(matchReverse, new ArrayList<Action>());
			backward.addAction(new Enqueue(connectors.get(i + 0), 1));
			backward.setPriority(basePriortity);
			backward.setHardTimeout(timeout);

			flowProgrammer.addFlow(connectors.get(i).getNode(), backward);
		}

		// Beacon frames have to reach the controller periodically.
		matchBeacon = new Match();
		matchBeacon.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchBeacon.setField(MatchType.DL_SRC, cMac);
		matchBeacon.setField(MatchType.DL_DST, broadcast);
		matchBeacon.setField(MatchType.IN_PORT, connectors.get(connectors.size() - 1));

		beacon = new Flow(matchBeacon, new ArrayList<Action>());
		beacon.addAction(new Enqueue(connectors.get(connectors.size() - 2), 1));
		beacon.setPriority((short)(basePriortity + 1));
		beacon.setHardTimeout((short)(timeout - graceTime - 1));

		flowProgrammer.addFlow(connectors.get(connectors.size() - 1).getNode(), beacon);

		// Beacon frames have to reach the controller periodically.
		matchBeacon = new Match();
		matchBeacon.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
		matchBeacon.setField(MatchType.DL_SRC, cMac);
		matchBeacon.setField(MatchType.DL_DST, broadcast);
		matchBeacon.setField(MatchType.IN_PORT, connectors.get(connectors.size() - 1));

		beacon = new Flow(matchBeacon, new ArrayList<Action>());
		beacon.addAction(new Enqueue(connectors.get(connectors.size() - 2), 1));
		beacon.addAction(new Controller());
		beacon.setPriority((short)(basePriortity + 1));
		beacon.setHardTimeout(timeout);

		flowProgrammer.addFlow(connectors.get(connectors.size() - 1).getNode(), beacon);

		return FlowUtilityResult.OK;
	}

	// Add all necessary flows between a mobile terminal and a access point.
	public FlowUtilityResult createBeaconTunnel(NodeConnector a, NodeConnector b, byte[] accessPointMac, short timeout)
	{
		if (routing == null)
			return FlowUtilityResult.NO_I_ROUTING;
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;

		ArrayList<NodeConnector> connectors = getRoute(a, b);
		Match matchForward;
		Match matchReverse;
		Flow forward;
		Flow backward;
		byte[] mask = new byte[]{ 0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }; // The first 2 bytes can dynamically change.

		// The rest of the flows.
		for (int i = 0; i < connectors.size(); i += 2)
		{
			// Forward flow.
			matchForward = new Match();
			matchForward.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchForward.setField(MatchType.DL_SRC, accessPointMac, mask);
			matchForward.setField(MatchType.IN_PORT, connectors.get(i + 0));

			forward = new Flow(matchForward, new ArrayList<Action>());
			forward.addAction(new Enqueue(connectors.get(i + 1), 1));
			forward.setHardTimeout(timeout);
			forward.setPriority((short)(1000 + 1));

			flowProgrammer.addFlow(connectors.get(i).getNode(), forward);

			/*// Backward flow.
			matchReverse = new Match();
			matchReverse.setField(MatchType.DL_TYPE, CLOUDMAC_ETHERNET_TYPE);
			matchReverse.setField(MatchType.DL_DST, accessPointMac);
			matchReverse.setField(MatchType.IN_PORT, connectors.get(i + 1));

			backward = new Flow(matchReverse, new ArrayList<Action>());
			backward.addAction(new Enqueue(connectors.get(i + 0), 1));
			backward.setHardTimeout(timeout);

			flowProgrammer.addFlow(connectors.get(i).getNode(), backward);*/
		}
		return FlowUtilityResult.OK;
	}

	public FlowUtilityResult block(NodeConnector connector, byte[] sourceMac, byte[] destiantionMac, short timeout)
	{
		if (flowProgrammer == null)
			return FlowUtilityResult.NO_I_FLOW_PROGRAMMER;

		Match match = new Match();
		Flow flow = new Flow();
		byte[] mask = new byte[]{ 0, 0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }; // The first 2 bytes can dynamically change.

		match.setField(MatchType.IN_PORT, connector);
		match.setField(MatchType.DL_SRC, sourceMac, mask);
		match.setField(MatchType.DL_DST, destiantionMac, mask);

		flow.setActions(new ArrayList<Action>());
		flow.addAction(new Drop());
		flow.setMatch(match);
		flow.setHardTimeout(timeout);
		flow.setPriority((short)2000);

		flowProgrammer.addFlow(connector.getNode(), flow);

		return FlowUtilityResult.OK;
	}

	public void ActivateAckingAsync(String hostname, int port, byte[] mac, int timeout)
	{
			AckingActivatorCallable activator = new AckingActivatorCallable(hostname, port, mac, timeout);
			FutureTask<String> futureTask = new FutureTask<String>(activator);
			ExecutorService executor =Executors.newSingleThreadExecutor();

			executor.execute(futureTask);
	}
}