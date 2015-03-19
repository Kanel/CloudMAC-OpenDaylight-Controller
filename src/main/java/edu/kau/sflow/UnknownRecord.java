package edu.kau.sflow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UnknownRecord extends FlowDataRecord
{
	byte[] data;
	
	private UnknownRecord() { }
	
	public static UnknownRecord parse(ByteBuffer buffer)
	{
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		if (buffer.remaining() >= 8)
		{
			UnknownRecord record = new UnknownRecord();
			int dataLength;
			
			record.type = DataFormat.parse(buffer);
			dataLength = buffer.getInt();
			
			if (buffer.remaining() >= dataLength)
			{
				record.data = new byte[dataLength];
				
				buffer.get(record.data);				
			}
			else
			{
				return null;
			}
			
			if (dataLength % 4 != 0)
			{
				buffer.position(buffer.position() + (4 - buffer.position() % 4));
			}
			
			return record;
		}
		else
		{
			return null;
		}
	}
	
	public byte[] getData()
	{
		return data;
	}
}
