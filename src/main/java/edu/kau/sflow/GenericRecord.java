package edu.kau.sflow;


public class GenericRecord
{
	protected DataFormat type;
	
	protected GenericRecord() { }
	
	public DataFormat getFormat()
	{
		return type;
	}
}
