package edu.kau.cloudmac.controller;

import java.util.LinkedList;
import java.util.List;

import org.opendaylight.controller.sal.core.NodeConnector;

public class SignalAnalyser
{
	protected List<Triple<NodeConnector, Short, Long>> signals;
	
	public SignalAnalyser()
	{
		signals = new LinkedList<>();
	}
	
	private short decay(short value, long deltaTime)
	{
		return (short)(value / (2 * deltaTime) / 10000);
	}
	
	private void update(long timestamp)
	{
		for (int i = 0; i < signals.size(); i++)
		{
			long deltaTime = timestamp - signals.get(i).getC();
			
			if (deltaTime != 0)
			{
				signals.get(i).setB(decay(signals.get(i).getB(), deltaTime));
				signals.get(i).setC(timestamp);
			}
			
			// TODO: Remove old ones.
		}
	}
	
	public void report(NodeConnector connector, short signal, long timestamp)
	{		
		for (int i = 0; i < signals.size(); i++)
		{
			if (signals.get(i).getA().equals(connector))
			{
				long deltaTime = timestamp - signals.get(i).getC();	
				
				signals.get(i).setB(decay(signals.get(i).getB(), deltaTime));
				signals.get(i).setC(timestamp);
				
				return;
			}
		}
		signals.add(new Triple<NodeConnector, Short, Long>(connector, signal, timestamp));
	}
	
	public NodeConnector getBest(NodeConnector connector, long timestamp)
	{
		Triple<NodeConnector, Short, Long> current = null;
		Triple<NodeConnector, Short, Long> best = signals.get(0);
		
		update(timestamp);
		
		for (int i = 0; i < signals.size(); i++)
		{
			if (signals.get(i).getA().equals(connector))
			{
				current = signals.get(i);
			}
			
			if (signals.get(i).getB() > best.getB())
			{
				best = signals.get(i);
			}
		}
		
		if (current.getB() * 1.05 < best.getB())
		{
			return best.getA();
		}
		else
		{
			return current.getA();
		}
	}
}
