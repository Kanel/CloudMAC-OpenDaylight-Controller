package sFlow;

public class FlowSample
{
	private long sequenceNumber;
	private DataSource sourceId;
	private long samplingRate;
	private long samplePool;
	private long drops;
	private Interface input;
	private Interface output;
	private FlowRecord flowRecords[];
	
	public FlowSample(long sequenceNumber, DataSource dataSource, long samplingRate, long samplePool, long drops, Interface input, Interface output, FlowRecord flowRecords[])
	{
		this.sequenceNumber = sequenceNumber;
		this.sourceId = dataSource;
		this.samplingRate = samplingRate;
		this.samplePool = samplePool;
		this.drops = drops;
		this.input = input;
		this.output = output;
		this.flowRecords = flowRecords;		
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
	
	public FlowRecord[] getFlowRecords()
	{
		return flowRecords;
	}
}
