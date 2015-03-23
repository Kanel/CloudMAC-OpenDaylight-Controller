package edu.kau.cloudmac.controller;

import java.util.Arrays;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;

public class Endpoint
{
	protected byte[] mac;
	protected NodeConnector connector;

	public Endpoint(NodeConnector connector, byte[] mac)
	{
		this.connector = connector;
		this.mac = mac;
	}

	public Node getNode()
	{
		return connector.getNode();
	}

	public NodeConnector getConnector()
	{
		return connector;
	}
	
	public void setConnector(NodeConnector connector)
	{
		this.connector = connector;
	}

	public byte[] getMacAdress()
	{
		return mac;
	}

	@Override
	public boolean equals(Object other)
	{
		Endpoint otherEndpoint;

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
	    otherEndpoint = (Endpoint)other;

	    return Arrays.equals(otherEndpoint.getMacAdress(), mac);
	}
}
