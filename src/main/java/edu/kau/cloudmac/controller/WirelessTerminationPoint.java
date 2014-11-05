package edu.kau.cloudmac.controller;

import org.opendaylight.controller.sal.core.NodeConnector;

public class WirelessTerminationPoint
{
	protected boolean allocated;
	protected long allocationExpiration;
	protected long expiration;
	protected NodeConnector connector;
	protected String ip;

	public WirelessTerminationPoint(NodeConnector connector, long expiration)
	{
		this.connector = connector;
		this.allocated = false;
		this.expiration = expiration;
	}

	public NodeConnector getConnector()
	{
		return connector;
	}

	public boolean isAllocated()
	{
		return allocated;
	}

	public void allocate(long expiration)
	{
		allocated = true;
		allocationExpiration = expiration;
	}

	public void deallocate()
	{
		allocated = false;
	}

	public long getAllocationExpiration()
	{
		return allocationExpiration;
	}

	public void setAllocationExpiration(long expiration)
	{
		allocationExpiration = expiration;
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

	public void setIP(String ip)
	{
		this.ip = ip;
	}

	public String getIP()
	{
		return ip;
	}

	@Override
	public boolean equals(Object other)
	{
		WirelessTerminationPoint otherWtp;

	    if (other == null)
    	{
    		return false;
    	}
	    if (other == this)
	    {
	    	return true;
	    }
	    if (!(other instanceof Endpoint))
    	{
    		return false;
    	}
	    otherWtp = (WirelessTerminationPoint)other;

	    return connector.equals(otherWtp.connector);
	}
}
