package edu.kau.cloudmac.controller;

import java.util.Arrays;

import org.opendaylight.controller.sal.core.NodeConnector;

public class MobileTerminalTunnel
{
	protected Endpoint source;
	protected AccessPoint ap;
	protected long expiration;
	protected SignalAnalyser analyser;

	public MobileTerminalTunnel(Endpoint source, AccessPoint ap, long expiration)
	{
		this.source = source;
		this.ap = ap;
		this.expiration = expiration;
		this.analyser = new SignalAnalyser();
	}

	public Endpoint getSource()
	{
		return source;
	}

	public void setSource(Endpoint source)
	{
		this.source = source;
	}

	public AccessPoint getAccessPoint()
	{
		return ap;
	}

	public long getExpiration()
	{
		return expiration;
	}

	public void setExpiration(long expiration)
	{
		this.expiration = expiration;
	}

	public boolean expired()
	{
		return expiration < System.currentTimeMillis();
	}
	
	public void reportSignal(NodeConnector connector, short signal, long timestamp)
	{
		analyser.report(connector, signal, timestamp);
	}
	
	public NodeConnector getBestSignalSource(long timestamp)
	{
		return analyser.getBest(source.getConnector(), timestamp);		
	}

	@Override
	public boolean equals(Object other)
	{
		MobileTerminalTunnel otherMobileTerminal;

	    if (other == null)
    	{
    		return false;
    	}
	    if (other == this)
	    {
	    	return true;
	    }
	    if (!(other instanceof MobileTerminalTunnel))
    	{
    		return false;
    	}
	    otherMobileTerminal = (MobileTerminalTunnel)other;

	    return Arrays.equals(otherMobileTerminal.source.getMacAdress(), source.getMacAdress());
	}
}