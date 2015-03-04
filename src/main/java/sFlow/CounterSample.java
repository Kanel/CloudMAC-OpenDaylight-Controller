package sFlow;

import java.nio.ByteBuffer;

public class CounterSample extends SampleRecord
{
	private long sequenceNumber;
	private DataSource sourceId;
	private CounterRecord counters[];
	
	private CounterSample() { }
	
	public static CounterSample parse(ByteBuffer buffer)
	{
		CounterSample sample = new CounterSample();
		int numberOfCounters;		
		int numberOfBytes;
		boolean expanded;
		
		if (buffer.remaining() >= 8)
		{
			sample.type = DataFormat.parse(buffer);
			numberOfBytes = buffer.getInt();
		}
		else
		{
			return null;
		}
		
		if (sample.type.getEnterPriseCode() == 0 && sample.type.getFormatNumber() == 2)
		{
			expanded = false;
		}
		else if (sample.type.getEnterPriseCode() == 0 && sample.type.getFormatNumber() == 4)
		{
			expanded = true;
		}
		else
		{
			return null;
		}
		
		if (buffer.remaining() >= numberOfBytes)
		{
			if ((expanded && buffer.remaining() >= 12) || (!expanded && buffer.remaining() >= 16))
			{
				sample.sequenceNumber = (long)buffer.getInt();
				sample.sourceId = (expanded) ? DataSource.parseExpanded(buffer) : DataSource.parse(buffer);
				numberOfCounters = buffer.getInt();
			}
			else
			{
				return null;
			}
			
			if (numberOfCounters >= 0)
			{
				sample.counters = new CounterRecord[numberOfCounters];
			}
			else
			{
				return null;
			}
			
			for (int i = 0; i < numberOfCounters; i++)
			{
				sample.counters[i] = CounterRecord.parse(buffer);
				
				if (sample.counters[i] == null)
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

	public long getSequenceNumber()
	{
		return sequenceNumber;
	}	
	
	public DataSource getSourceId()
	{
		return sourceId;
	}	
	
	public CounterRecord[] getCounters()
	{
		return counters;
	}
	
}
