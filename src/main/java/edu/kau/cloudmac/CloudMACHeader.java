package edu.kau.cloudmac;

import java.nio.ByteBuffer;

public class CloudMACHeader
{
	private byte[] source;
	private byte[] destination;
	private byte[] vapId;
	private int type; // unsigned short

	private E802_1QHeader e802_1qHeader;
	
	private CloudMACHeader() { }
	
	public static CloudMACHeader parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 16)
		{
			CloudMACHeader header = new CloudMACHeader();
			
			header.source = new byte[6];
			header.destination = new byte[6];
			header.vapId = new byte[4];
			
			buffer.get(header.source);
			buffer.get(header.destination);
			
			header.vapId[0] = header.destination[2];
			header.vapId[1] = header.destination[3];
			header.vapId[2] = header.destination[4];
			header.vapId[3] = header.destination[5];
			
			header.type = (int)buffer.getShort();
			
			if (header.type == E802_1QHeader.TAG_PROTOCOL_IDENTIFIER)
			{
				buffer.position(buffer.position() - 2);
				
				header.e802_1qHeader = E802_1QHeader.parse(buffer);
			}
			
			return header;
		}
		else
		{
			return null;
		}
	}
	
	public byte[] getSource()
	{
		return source;
	}
	
	public byte[] getDestination()
	{
		return destination;
	}
	
	public byte[] getVapId()
	{
		return vapId;
	}
	
	public byte getSignalStrength()
	{
		return destination[0];
	}
	
	public byte getDataRate()
	{
		return destination[1];
	}
	
	public int getType()
	{
		return type;
	}	

	public E802_1QHeader getE802_1qHeader()
	{
		return e802_1qHeader;
	}
}
