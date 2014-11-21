package edu.kau.cloudmac.controller;

import java.util.LinkedList;
import java.util.ListIterator;

import org.opendaylight.controller.sal.core.NodeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WirelessTerminationPointManager
{
	private static final Logger log = LoggerFactory.getLogger(AccessPointManager.class);
	LinkedList<WirelessTerminationPoint> wtps;

	public WirelessTerminationPointManager()
	{
		wtps = new LinkedList<WirelessTerminationPoint>();
	}

	public WirelessTerminationPoint add(NodeConnector connector, long expiration)
	{
		WirelessTerminationPoint wtp = new WirelessTerminationPoint(connector, expiration);

		wtps.add(wtp);

		return wtp;
	}

	public void remove(NodeConnector connector)
	{
		wtps.remove(new WirelessTerminationPoint(connector, 0));
	}

	public boolean contains(NodeConnector connector)
	{
		return get(connector) != null;
	}

	public WirelessTerminationPoint get(NodeConnector connector)
	{
		cleanup();

		for (WirelessTerminationPoint element : wtps)
		{
			if (element.getConnector().equals(connector))
			{
				return element;
			}
		}
		return null;
	}

	public boolean hasFree()
	{
		cleanup();

		for (WirelessTerminationPoint element : wtps)
		{
			if (!element.isAllocated())
			{
				return true;
			}
		}
		return false;
	}

	public WirelessTerminationPoint allocate(long expiration)
	{
		cleanup();

		for (WirelessTerminationPoint element : wtps)
		{
			if (!element.isAllocated())
			{
				element.allocate(expiration);

				return element;
			}
		}
		return null;
	}

	private void cleanup()
	{
		long now = System.currentTimeMillis();

		for (ListIterator<WirelessTerminationPoint> iter = wtps.listIterator(); iter.hasNext(); ) {
			WirelessTerminationPoint element = iter.next();

		    if (element.isAllocated() && element.getAllocationExpiration() < now)
			{
		    	log.trace("WTP beacon allocation has expired {}/{}. ", element.getConnector());

		    	element.deallocate();
			}
			if (element.getExpiration() < now)
			{
				log.info("WTP has expired {}/{}. ", element.getConnector());

				iter.remove();
			}
		}
	}
}