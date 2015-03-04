package sFlow;

import java.nio.ByteBuffer;

public class CounterRecord
{
	DataFormat counterFormat;
	byte[] counterData;
	
	private CounterRecord() { }
	
	public static CounterRecord parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 8)
		{
			CounterRecord record = new CounterRecord();
			int dataLength;
			
			record.counterFormat = DataFormat.parse(buffer);
			dataLength = buffer.getInt();
			
			if (buffer.remaining() >= dataLength)
			{
				record.counterData = new byte[dataLength];
				
				buffer.get(record.counterData);
			}
			else
			{
				return null;
			}
			
			return record;
		}
		else
		{
			return null;
		}
	}
	
	public DataFormat getDataFormat()
	{
		return counterFormat;
	}
	
	public byte[] getData()
	{
		return counterData;
	}
}
