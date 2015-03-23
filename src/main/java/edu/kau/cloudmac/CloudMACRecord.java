package edu.kau.cloudmac;

import java.nio.ByteBuffer;

public class CloudMACRecord
{
	private CloudMACHeader cloudmacHeader;
	private RadioTapHeader radiotapHeader;
	private E802_11Header e80211Header;
	
	private CloudMACRecord() { }
	
	// 69 bytes
	public static CloudMACRecord parse(ByteBuffer buffer)
	{
		CloudMACRecord cloudMac = new CloudMACRecord();
		
		cloudMac.cloudmacHeader = CloudMACHeader.parse(buffer);
		cloudMac.radiotapHeader = RadioTapHeader.parse(buffer);
		cloudMac.e80211Header = E802_11Header.parse(buffer);
		
		if (cloudMac.cloudmacHeader == null)
		{
			return null;
		}
		
		if (cloudMac.radiotapHeader == null)
		{
			return null;
		}
		
		if (cloudMac.e80211Header == null)
		{
			return null;
		}
		
		return cloudMac;
	}
	
	public CloudMACHeader getCloudMacHeader()
	{
		return cloudmacHeader;
	}
	
	public RadioTapHeader getRadiotap()
	{
		return radiotapHeader;
	}
	
	public E802_11Header getE80211Header()
	{
		return e80211Header;
	}
}
