package edu.kau.cloudmac.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MobileTerminalTunnel
{
	protected Endpoint source;
	protected AccessPoint ap;
	protected long expiration;	

	public MobileTerminalTunnel(Endpoint source, AccessPoint ap, long expiration)
	{
		this.source = source;
		this.ap = ap;
		this.expiration = expiration;
		this.signals = new LinkedList<Triple<Endpoint, Short, Long>>();
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
	
	public void reportSignal(WirelessTerminationPoint wtp, short signal, long time)
	{
		
	}
	
	public Triple<Endpoint, Short, Long> getBestSignal()
	{
		
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