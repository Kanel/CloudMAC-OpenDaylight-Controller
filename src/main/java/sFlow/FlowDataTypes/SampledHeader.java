package sFlow.FlowDataTypes;

import java.nio.ByteBuffer;

import sFlow.DataFormat;

public class SampledHeader
{
	private DataFormat type;
	private HeaderProtocols protocol;
	private long frameLength;
	private long stripped;
	private byte header[];
	
	private SampledHeader() { }
	
	public static SampledHeader parse(ByteBuffer buffer)
	{
		SampledHeader sample = new SampledHeader();
		int numberOfBytes;
		int headerLength;
		
		if (buffer.remaining() >= 8)
		{
			sample.type = DataFormat.parse(buffer);
			numberOfBytes = buffer.getInt();
		}		
		else
		{
			return null;
		}
		
		if (sample.type.getEnterPriseCode() == 0 && sample.type.getFormatNumber() == 1)
		{
			return null;
		}
		
		if (buffer.remaining() >= numberOfBytes)
		{
			if (buffer.remaining() >= 12)
			{
				sample.frameLength = (long)buffer.getInt();
				sample.stripped = (long)buffer.getInt();
				headerLength = buffer.getInt();
			}
			else
			{
				return null;
			}
			
			if (headerLength >= 0)
			{
				sample.header = new byte[headerLength];
				
				buffer.get(sample.header);
			}
			else
			{
				return null;
			}
			
			return sample;
		}
		else
		{
			return null;
		}
	}
	
	public DataFormat getType()
	{
		return type;
	}
	
	public HeaderProtocols getProtocol()
	{
		return protocol;
	}
	
	public long getFrameLength()
	{
		return frameLength;
	}
	
	public long getStripped()
	{
		return stripped;
	}
	
	public byte[] getHeader()
	{
		return header;
	}
}
