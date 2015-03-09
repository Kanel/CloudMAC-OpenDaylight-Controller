package sFlow;

import java.nio.ByteBuffer;

public class FlowSample extends SampleRecord
{
	private long sequenceNumber;
	private DataSource sourceId;
	private long samplingRate;
	private long samplePool;
	private long drops;
	private Interface input;
	private Interface output;
	private GenericRecord flowRecords[];
	
	private FlowSample() { }
	
	public static FlowSample parse(ByteBuffer buffer)
	{
		FlowSample sample = new FlowSample();
		int numberOfRecords;
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
		
		if (sample.type.getEnterPriseCode() == 0 && sample.type.getFormatNumber() == 1)
		{
			expanded = false;
		}
		else if (sample.type.getEnterPriseCode() == 0 && sample.type.getFormatNumber() == 3)
		{
			expanded = true;
		}
		else
		{
			return null;
		}
		
		if (buffer.remaining() >= numberOfBytes)
		{
			if ((!expanded && buffer.remaining() >= 32) || (expanded && buffer.remaining() >= 44))
			{
				sample.sequenceNumber = (long)buffer.getInt();
				sample.sourceId = (expanded) ? DataSource.parseExpanded(buffer) : DataSource.parse(buffer);
				sample.samplingRate = (long)buffer.getInt();
				sample.samplePool = (long)buffer.getInt();
				sample.drops = (long)buffer.getInt();
				sample.input = (expanded) ? Interface.parseExpanded(buffer) : Interface.parse(buffer);
				sample.output = (expanded) ? Interface.parseExpanded(buffer) : Interface.parse(buffer);
				numberOfRecords = buffer.getInt();
			}
			else
			{
				return null;
			}
			
			if (numberOfRecords >= 0)
			{
				sample.flowRecords = new GenericRecord[numberOfRecords];
			}
			else
			{
				return null;
			}
			
			for (int i = 0; i < numberOfRecords; i++)
			{
				DataFormat format = DataFormat.parse(buffer); // Peek!
				
				buffer.position(buffer.position() - 4); // Rewind!
				
				if (format.getEnterPriseCode() == 0 && format.getFormatNumber() == 1)
				{
					sample.flowRecords[i] = SampledHeader.parse(buffer);
				}
				else
				{
					sample.flowRecords[i] = UnknownRecord.parse(buffer);
				}
				
				if (sample.flowRecords[i] == null)
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
	
	public long getSamplingRate()
	{
		return samplingRate;
	}
	
	public long getSamplePool()
	{
		return samplePool;
	}
	
	public long getDrops()
	{
		return drops;
	}
	
	public Interface getInput()
	{
		return input;
	}
	
	public Interface getOutput()
	{
		return output;
	}
	
	public GenericRecord[] getFlowRecords()
	{
		return flowRecords;
	}
}
