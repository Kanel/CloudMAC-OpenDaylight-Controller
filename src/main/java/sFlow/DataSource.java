package sFlow;

public class DataSource
{
	private DataSourceTypes type;
	private int index;
	
	public DataSource(int value)
	{
		type = DataSourceTypes.lookup(value >> 24);
		index = value & 0x00FFFFFF;
	}
	
	public DataSourceTypes getType()
	{
		return type;
	}
	
	public int getIndex()
	{
		return index;
	}
}
