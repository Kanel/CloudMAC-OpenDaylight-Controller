package sFlow;

import java.net.InetAddress;

public class SampleDatagramV5
{
	private InetAddress agentAddress;
	private long subAgentId;
	private long sequenceNumber;
	private long upTime;
	private SampleRecord[] samples;
	
	public SampleDatagramV5(InetAddress agentAddress, long subAgentId, long sequenceNumber, long upTime, SampleRecord[] samples)
	{
		this.agentAddress = agentAddress;
		this.subAgentId = subAgentId;
		this.sequenceNumber = sequenceNumber;
		this.upTime = upTime;
		this.samples = samples;
	}
	
	public InetAddress getAgentAddress()
	{
		return agentAddress;
	}
	
	public long getSubAgentAddress()
	{
		return subAgentId;
	}
	
	public long getSequenceNumber()
	{
		return sequenceNumber;
	}
	
	public long getUpTime()
	{
		return upTime;
	}
	
	public SampleRecord[] getSamples()
	{
		return samples;
	}
}
