package edu.kau.sflow;

import java.nio.ByteBuffer;

public class UnknownRecord extends GenericRecord
{
	byte[] data;
	
	private UnknownRecord() { }
	
	public static GenericRecord parse(ByteBuffer buffer)
	{
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
