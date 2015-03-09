package edu.kau.sflow;

import java.nio.ByteBuffer;

public class SampledHeader extends GenericRecord
{
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
		
		if (sample.type.getEnterPriseCode() != 0 || sample.type.getFormatNumber() != 1)
		{
			return null;
		}
		
		if (buffer.remaining() >= numberOfBytes)
		{
			if (buffer.remaining() >= 12)
			{
				sample.protocol = HeaderProtocols.lookup(buffer.getInt());
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
			
			if (headerLength % 4 != 0)
			{
				buffer.position(buffer.position() + (4 - buffer.position() % 4));
			}
			
			return sample;
		}
		else
		{
			return null;
		}
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
