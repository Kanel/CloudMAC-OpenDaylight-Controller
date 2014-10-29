package edu.kau.cloudmac.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import org.opendaylight.controller.sal.core.NodeConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelManager
{
	private static final Logger log = LoggerFactory.getLogger(TunnelManager.class);
	LinkedList<MobileTerminalTunnel> mobileTerminals;

	public TunnelManager()
	{
		mobileTerminals = new LinkedList<MobileTerminalTunnel>();
	}

	public void add(NodeConnector connector, byte[] mac, AccessPoint ap, long expiration)
	{
		mobileTerminals.add(new MobileTerminalTunnel(new Endpoint(connector, mac.clone()), ap, expiration));
	}

	public void remove(byte[] mac)
	{
		mobileTerminals.remove(new MobileTerminalTunnel(new Endpoint(null, mac), null, 0));
	}

	public boolean contains(byte[] mac)
	{
		cleanup();

		return get(mac) != null;
	}

	public boolean containsAccessPoint(byte[] mac)
	{
		cleanup();

		return getByAccessPoint(mac) != null;
	}

	public MobileTerminalTunnel get(byte[] mac)
	{
		cleanup();

		for (MobileTerminalTunnel element : mobileTerminals)
		{
			if (Arrays.equals(element.getSource().getMacAdress(), mac))
			{
				return element;
			}
		}
		return null;
	}

	public MobileTerminalTunnel getByAccessPoint(byte[] mac)
	{
		cleanup();

		for (MobileTerminalTunnel element : mobileTerminals)
		{
			if (Arrays.equals(element.getAccessPoint().getMacAdress(), mac))
			{
				return element;
			}
		}
		return null;
	}

	private void cleanup()
	{
		for (ListIterator<MobileTerminalTunnel> iter = mobileTerminals.listIterator(); iter.hasNext(); )
		{
			MobileTerminalTunnel element = iter.next();

			if (element.expired() || element.getAccessPoint().expired())
			{
				log.trace("CloudMAC: Tunnel has expired {}. ", element.getSource().getMacAdress());

				iter.remove();
			}
		}
	}
}