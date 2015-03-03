package sFlow;

public class CounterRecord
{
	DataFormat counterFormat;
	byte[] counterData;
	
	public CounterRecord(DataFormat format, byte[] data)
	{
		counterFormat = format;
		counterData = data;
	}
	
	public DataFormat getDataFormat()
	{
		return counterFormat;
	}
	
	public byte[] getData()
	{
		return counterData;
	}
}
