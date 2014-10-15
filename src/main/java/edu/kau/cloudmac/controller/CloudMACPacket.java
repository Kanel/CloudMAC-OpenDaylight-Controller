package edu.kau.cloudmac.controller;

import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.RawPacket;

public class CloudMACPacket
{
	public static short getFrameType(RawPacket packet)
	{
		final short MAC_LENGTH = 6;
		final short ETHER_TYPE_LENGTH = 2;
		final short HEADER_802_1Q_LENGTH = 4;
		final int HEADER_802_1Q_ID = 0x8100;
		byte[] data = packet.getPacketData();
		short type = 0;
		short offset = (short)(MAC_LENGTH + MAC_LENGTH + ETHER_TYPE_LENGTH);
		int etherType = (data[MAC_LENGTH + MAC_LENGTH] << 8) | data[MAC_LENGTH + MAC_LENGTH + 1];

		// Check if there is a 802.1Q header.
		if (etherType == HEADER_802_1Q_ID) // <- Why do I even check?
		{
			offset += HEADER_802_1Q_LENGTH;
		}
		offset += data[offset + 2]; // Some CloudMAC magic.

		// We don't need the first 2 bits (protocol version).
		type += data[offset] & 0b11111111;

		return type;
	}

	public static boolean isCloudMAC(Packet packet)
	{
        short CLOUD_MAC_TYPE = 0x1337; // CloudMAC ethernet type.

        if (packet instanceof Ethernet)
        {
                Ethernet ethPkt = (Ethernet)packet;

                if (ethPkt.getEtherType() == CLOUD_MAC_TYPE)
                {
                        return true;
                }
        }
		return false;
	}
}
