package sFlow;

import java.nio.ByteBuffer;

public class Interface
{
	private InterfaceFormatTypes formatType;
	private long value;
	
	private Interface() { }
	
	public static Interface parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 4)
		{
			Interface interface_ = new Interface();
			int value = buffer.getInt();
			
			interface_.formatType =  InterfaceFormatTypes.lookup(value >> 30);
			interface_.value = value & 0x3FFFFFFF;
			
			return interface_;
		}
		else
		{
			return null;
		}
	}
	
	public static Interface parseExpanded(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 8)
		{
			Interface interface_ = new Interface();
			
			interface_.formatType = InterfaceFormatTypes.lookup(buffer.getInt());
			interface_.value = (long)buffer.getInt();
			
			return interface_;
		}
		else
		{
			return null;
		}
	}
	
	public InterfaceFormatTypes getFormatType()
	{
		return formatType;
	}
	
	public long getValue()
	{
		return value;
	}
}