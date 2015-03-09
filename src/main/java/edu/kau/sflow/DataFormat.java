package edu.kau.sflow;

import java.nio.ByteBuffer;

public class DataFormat
{
	private int enterpriseCode;
	private int formatNumber;

	private DataFormat() { }
	
	public static DataFormat parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 4)
		{
			DataFormat format = new DataFormat();
			int value = buffer.getInt();
			
			format.enterpriseCode = value >> 12;
			format.formatNumber = value & 0x00000FFF;
			
			return format;
		}
		else
		{
			return null;
		}
	}
	
	public int getEnterPriseCode()
	{
		return enterpriseCode;
	}
	
	public int getFormatNumber()
	{
		return formatNumber;
	}
}
