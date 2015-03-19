package edu.kau.cloudmac.controller;

import java.util.LinkedList;
import java.util.List;

public class SignalAnalyser
{
	protected List<Triple<Endpoint, Short, Long>> signals;
	
	public SignalAnalyser()
	{
		signals = new LinkedList<>();
	}
	
	public void report(Endpoint endpoint, short signal, long timestamp)
	{
		
		for (int i = 0; i < signals.size(); i++)
		{
			if (signals.get(i).getA().equals(endpoint))
			{
				long difference = (timestamp - signals.get(i).getC()) / 10000;
				
				signals.get(i).setB((short)(signals.get(i).getB() / (2 * difference) + signal));
			}
		}
	}
}
