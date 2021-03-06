package edu.kau.cloudmac.controller;

import org.opendaylight.controller.sal.core.NodeConnector;

public class AccessPoint extends Endpoint
{
	protected boolean allocated;
	protected long allocationExpiration;
	protected long expiration;

	public AccessPoint(NodeConnector connector, byte[] mac, long expiration)
	{
		super(connector, mac);

		this.allocated = false;
		this.expiration = expiration;
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
	
	public boolean equals(AccessPoint accessPoint)
	{
		return equals(accessPoint.getMacAdress());
	}
	
	public boolean equals(byte[] other)
	{
		return mac[2] == other[2] &&
			   mac[3] == other[3] &&
			   mac[4] == other[4] &&
			   mac[5] == other[5];
	}
}
