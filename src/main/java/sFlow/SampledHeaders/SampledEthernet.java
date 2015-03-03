package sFlow.SampledHeaders;

public class SampledEthernet
{
	private long length;
	private byte[] sourceMac;
	private byte[] destinationMac;
	private long type;
	
	public SampledEthernet(long length, byte[] sourceMac, byte[] destinationMac, long type)
	{
		this.length = length;
		this.sourceMac = sourceMac;
		this.destinationMac = destinationMac;
		this.type = type;
	}
	
	public long getLength()
	{
		return length;
	}
	
	public byte[] getSourceMac()
	{
		return sourceMac;
	}
	
	public byte[] getDestiantionMac()
	{
		return destinationMac;
	}
	
	public long getType()
	{
		return type;
	}
}
