package edu.kau.cloudmac;

import java.nio.ByteBuffer;

public class CloudMACHeader
{
	private byte[] source;
	private byte[] destination;
	private byte[] vapId;
	private int type; // unsigned short
	
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
	
	public int getType()
	{
		return type;
	}
	
	public byte getSignalStrength()
	{
		return destination[0];
	}
	
	public byte getDataRate()
	{
		return destination[1];
	}
	
	public byte[] getVapId()
	{
		return vapId;
	}
}
