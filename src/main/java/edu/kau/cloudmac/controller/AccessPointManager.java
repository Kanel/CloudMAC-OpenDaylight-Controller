package edu.kau.cloudmac.controller;

import java.util.LinkedList;
import java.util.ListIterator;

import org.opendaylight.controller.sal.core.NodeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessPointManager
{
	private static final Logger log = LoggerFactory.getLogger(AccessPointManager.class);
	LinkedList<AccessPoint> accessPoints;

	public AccessPointManager()
	{
		accessPoints = new LinkedList<AccessPoint>();
	}

	public void add(NodeConnector connector, byte[] mac, long expiration)
	{
		accessPoints.add(new AccessPoint(connector, mac.clone(), expiration));
	}

	public void remove(byte[] mac)
	{
		accessPoints.remove(new AccessPoint(null, mac, 0));
	}

	public boolean contains(byte[] mac)
	{
		return get(mac) != null;
	}

	// Ignores the first two bytes.
	public AccessPoint get(byte[] mac)
	{
		cleanup();

		for (AccessPoint element : accessPoints)
		{
			byte[] a = element.getMacAdress();

			if (a[2] == mac[2] &&
				a[3] == mac[3] &&
				a[4] == mac[4] &&
				a[5] == mac[5])
			{
				return element;
			}
		}
		return null;
	}

	public boolean hasFree()
	{
		cleanup();

		for (AccessPoint element : accessPoints)
		{
			if (!element.isAllocated())
			{
				return true;
			}
		}
		return false;
	}

	public AccessPoint allocate(long expiration)
	{
		cleanup();

		for (AccessPoint element : accessPoints)
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

		for (ListIterator<AccessPoint> iter = accessPoints.listIterator(); iter.hasNext(); ) {
		    AccessPoint element = iter.next();

		    if (element.isAllocated() && element.getAllocationExpiration() < now)
			{
		    	log.trace("CloudMAC: Access point allocation has expired {}/{}. ", element.getConnector(), element.getMacAdress());

		    	element.deallocate();
			}
			if (element.getExpiration() < now)
			{
				log.info("CloudMAC: Access point has expired {}/{}. ", element.getConnector(), element.getMacAdress());

				iter.remove();
			}
		}
	}
}