package sFlow;

public class CounterSample
{
	private long sequenceNumber;
	private DataSource sourceId;
	private CounterRecord counters[];
	
	public CounterSample(long sequenceNumber, DataSource sourceId, CounterRecord counters[])
	{
		this.sequenceNumber = sequenceNumber;
		this.sourceId = sourceId;
		this.counters = counters;
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
