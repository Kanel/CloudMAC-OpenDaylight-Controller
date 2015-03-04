package sFlow;

import java.nio.ByteBuffer;

public class FlowRecord
{
	DataFormat flowFormat;
	byte[] flowData;
	
	private FlowRecord() { }
	
	public static FlowRecord parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 8)
		{
			FlowRecord record = new FlowRecord();
			int dataLength;
			
			record.flowFormat = DataFormat.parse(buffer);
			dataLength = buffer.getInt();
			
			if (buffer.remaining() >= dataLength)
			{
				record.flowData = new byte[dataLength];
				
				buffer.get(record.flowData);
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
		return flowFormat;
	}
	
	public byte[] getData()
	{
		return flowData;
	}
}