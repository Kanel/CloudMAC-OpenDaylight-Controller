package edu.kau.cloudmac.controller;

public class CloudMACPacket
{
	public static short getFrameType(byte[] data)
	{
		final short MAC_LENGTH = 6;
		final short ETHER_TYPE_LENGTH = 2;
		final short HEADER_802_1Q_LENGTH = 4;
		final int HEADER_802_1Q_ID = 0x8100;
		short type = 0;
		short offset = (short)(MAC_LENGTH + MAC_LENGTH + ETHER_TYPE_LENGTH);
		int etherType = (data[MAC_LENGTH + MAC_LENGTH] << 8) | data[MAC_LENGTH + MAC_LENGTH + 1];

		// Check if there is a 802.1Q header.
		if (etherType == HEADER_802_1Q_ID)
		{
			offset += HEADER_802_1Q_LENGTH;
		}
		offset += (int)((data[offset + 1] << 8) | data[offset + 2]); // Add radiotap header length.

		// We don't need the first 2 bits (protocol version).
		type += data[offset] & 0b11111111;

		return type;
	}

	public static boolean isCloudMAC(byte[] data)
	{
		short CLOUDMAC_QOS_BACKGROUND = 0x1336; // CloudMAC ethernet type.
		short CLOUDMAC_QOS_BEST_EFFORT = 0x1337; // CloudMAC ethernet type.
		short CLOUDMAC_QOS_VIDEO = 0x1338; // CloudMAC ethernet type.
		short CLOUDMAC_QOS_VOICE = 0x1339; // CloudMAC ethernet type.
		final short MAC_LENGTH = 6;
		final short HEADER_802_1Q_LENGTH = 4;
		final int HEADER_802_1Q_ID = 0x8100;
		int offset = MAC_LENGTH + MAC_LENGTH;
		int etherType = (data[offset] << 8) | data[offset + 1];
		
		// Check if there is a 802.1Q header.
		if (etherType == HEADER_802_1Q_ID) // <- Why do I even check?
		{
			offset += HEADER_802_1Q_LENGTH;
			etherType = (data[offset] << 8) | data[offset + 1];
		}
		
		return etherType == CLOUDMAC_QOS_BACKGROUND 
			|| etherType == CLOUDMAC_QOS_BEST_EFFORT
			|| etherType == CLOUDMAC_QOS_VIDEO
			|| etherType == CLOUDMAC_QOS_VOICE;
	}
	
	public static byte[] getAddress3(byte[] data)
	{
		byte[] mac = new byte[6];
		
		final short MAC_LENGTH = 6;
		final short ETHER_TYPE_LENGTH = 2;
		final short HEADER_802_1Q_LENGTH = 4;
		final int HEADER_802_1Q_ID = 0x8100;
		short offset = (short)(MAC_LENGTH + MAC_LENGTH + ETHER_TYPE_LENGTH);
		int etherType = (data[MAC_LENGTH + MAC_LENGTH] << 8) | data[MAC_LENGTH + MAC_LENGTH + 1]; // lots of assumptions here

		// Check if there is a 802.1Q header.
		if (etherType == HEADER_802_1Q_ID)
		{
			offset += HEADER_802_1Q_LENGTH;
		}
		offset += data[offset + 2];
		offset += 16;
		
		mac[0] = data[offset + 0];
		mac[1] = data[offset + 1];
		mac[2] = data[offset + 2];
		mac[3] = data[offset + 3];
		mac[4] = data[offset + 4];
		mac[5] = data[offset + 5];
		
		return mac;
	}
}
