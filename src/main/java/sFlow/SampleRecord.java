package sFlow;

public class SampleRecord
{
	private DataFormat sampleType;
	private byte sampleData[];
	
	public SampleRecord(DataFormat sampleType, byte sampleData[])
	{
		this.sampleType = sampleType;
		this.sampleData = sampleData;
	}
	
	public DataFormat getSampletype()
	{
		return sampleType;
	}
	
	public byte[] getSampleData()
	{
		return sampleData;
	}
}
