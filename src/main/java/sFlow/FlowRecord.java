package sFlow;

public class FlowRecord
{
	DataFormat flowFormat;
	byte[] flowData;
	
	public FlowRecord(DataFormat format, byte[] data)
	{
		flowFormat = format;
		flowData = data;
	}
	
	public DataFormat getDataFormat()
	{
		return flowFormat;
	}
	
	public byte[] getData()
	{
		return flowData;
	}
}