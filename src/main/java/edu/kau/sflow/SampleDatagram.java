package edu.kau.sflow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SampleDatagram
{
	private int version;
	private Address agentAddress;
	private long subAgentId;
	private long sequenceNumber;
	private long upTime;
	private SampleRecord[] samples;
	
	private SampleDatagram() { }
	
	public static SampleDatagram parse(ByteBuffer buffer)
	{
		SampleDatagram sample = new SampleDatagram();
		int numberOfSamples;
		
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		if (buffer.remaining() >= 8)
		{
			sample.version = buffer.getInt();
			sample.agentAddress = Address.parse(buffer);
			
			if (sample.agentAddress == null)
			{
				return null;
			}
			
			if (buffer.remaining() >= 16)
			{
				sample.subAgentId = (long)buffer.getInt();
				sample.sequenceNumber = (long)buffer.getInt();
				sample.upTime = (long)buffer.getInt();				
				numberOfSamples = buffer.getInt();
			}
			else
			{
				return null;
			}
			
			if (numberOfSamples < 0)
			{
				return null;
			}
			
			sample.samples = new SampleRecord[numberOfSamples];
			
			for (int i = 0; i < numberOfSamples; i++)
			{
				if (buffer.remaining() >= 4)
				{
					DataFormat format = DataFormat.parse(buffer); // Peek!
					
					buffer.position(buffer.position() - 4); // Rewind!
					
					if (format.getEnterPriseCode() == 0 && format.getFormatNumber() == 1)
					{
						sample.samples[i] = FlowSample.parse(buffer);
					}
					else if (format.getEnterPriseCode() == 0 && format.getFormatNumber() == 2)
					{
						sample.samples[i] = CounterSample.parse(buffer);
					}
					else if (format.getEnterPriseCode() == 0 && format.getFormatNumber() == 3)
					{
						sample.samples[i] = FlowSample.parse(buffer);
					}
					else if (format.getEnterPriseCode() == 0 && format.getFormatNumber() == 4)
					{
						sample.samples[i] = CounterSample.parse(buffer);
					}
					else
					{
						return null;
					}
					
					if (sample.samples[i] == null)
					{
						return null;
					}
				}
				else
				{
					return null;
				}				
			}
			
			return sample;
		}
		else
		{
			return null;
		}
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public Address getAgentAddress()
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
