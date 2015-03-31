package edu.kau.cloudmac.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.opendaylight.controller.sal.core.NodeConnector;

public class SignalAnalyser
{
	private short handoverThreshold;
	protected List<Triple<NodeConnector, Double, Long>> signals;
	
	public SignalAnalyser(short handoverThreshold)
	{
		this.handoverThreshold = handoverThreshold;
		this.signals = new LinkedList<>();
	}
	
	public void report(NodeConnector connector, short signal, long timestamp)
	{		
		for (int i = 0; i < signals.size(); i++)
		{
			if (signals.get(i).getA().equals(connector))
			{				
				double rssi = signals.get(i).getB() * 0.2 + signal * 0.8;
				
				signals.get(i).setB(rssi);
				signals.get(i).setC(timestamp);
				
				return;
			}
		}
		signals.add(new Triple<NodeConnector, Double, Long>(connector, signal * 0.8, timestamp));
	}
	
	public NodeConnector getCandidate(NodeConnector connector)
	{
		Triple<NodeConnector, Double, Long> current = null;
		Triple<NodeConnector, Double, Long> candidate = signals.get(0);
		
		cleanup();
		
		for (int i = 0; i < signals.size(); i++)
		{
			if (signals.get(i).getA().equals(connector))
			{
				current = signals.get(i);
			}
			
			if (signals.get(i).getB() > candidate.getB())
			{
				candidate = signals.get(i);
			}
		}
		
		if (current != null && current.getB() + handoverThreshold < candidate.getB())
		{
			return candidate.getA();
		}
		else
		{
			return current.getA();
		}
	}
	
	private void cleanup()
	{
		long now = System.currentTimeMillis();

		for (ListIterator<Triple<NodeConnector, Double, Long>> iter = signals.listIterator(); iter.hasNext(); ) {
			Triple<NodeConnector, Double, Long> element = iter.next();

		    if (element.getC() < now)
			{
				iter.remove();
			}
		}
	}
}
