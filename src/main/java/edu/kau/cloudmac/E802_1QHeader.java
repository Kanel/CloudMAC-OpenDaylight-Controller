package edu.kau.cloudmac;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class E802_1QHeader
{
	private int tagProtocolIdentifier; // unsigned short
	private byte priorityCodePoint; // u3
	private boolean dropEligibleIdicator;
	private short vlanIdentifier; // u12
	
	public static int TAG_PROTOCOL_IDENTIFIER = 0x8100;
	
	protected E802_1QHeader() { }
	
	public static E802_1QHeader parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 2)
		{
			E802_1QHeader header = new E802_1QHeader();
			byte value_a;
			byte value_b;
			
			buffer.order(ByteOrder.BIG_ENDIAN);
			
			header.tagProtocolIdentifier = (int)buffer.getShort();
			value_a = buffer.get();
			value_b = buffer.get();
			header.priorityCodePoint = (byte)(value_a >> 5);
			header.dropEligibleIdicator = (value_a & 0b00010000) == 0b00010000;
			header.vlanIdentifier = (short)((((short)value_a & 0x000F) << 8) | value_b);
			
			if (header.tagProtocolIdentifier != TAG_PROTOCOL_IDENTIFIER)
			{
				return null;
			}
			
			return header;
			
		}
		else
		{
			return null;
		}
	}
	
	public int getTagProtocolIdentifier()
	{
		return tagProtocolIdentifier;
	}
	
	public byte getPriorityCodePoint()
	{
		return priorityCodePoint;
	}
	
	public boolean getDropEligibleIdicator()
	{
		return dropEligibleIdicator;
	}
	
	public short getVlanIdentifier()
	{
		return vlanIdentifier;
	}
}
