package sFlow.SampledHeaders;

import sFlow.HeaderProtocols;

public class SampledHeader
{
	private HeaderProtocols protocol;
	private long frameLength;
	private long stripped;
	private byte header[];
	
	public SampledHeader(HeaderProtocols protocol, long frameLength, long stripped, byte header[])
	{
		this.protocol = protocol;
		this.frameLength = frameLength;
		this.stripped = stripped;
		this.header = header;
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
