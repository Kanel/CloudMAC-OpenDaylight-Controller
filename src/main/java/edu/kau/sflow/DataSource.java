package edu.kau.sflow;

import java.nio.ByteBuffer;

public class DataSource
{
	private DataSourceTypes type;
	private long index;
	
	private DataSource() { }
	
	public static DataSource parse(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 4)
		{
			DataSource source = new DataSource();
			int value = buffer.getInt();
			
			source.type = DataSourceTypes.lookup(value >> 24);
			source.index = value & 0x00FFFFFF;
			
			return source;
		}
		else
		{
			return null;
		}
	}
	
	public static DataSource parseExpanded(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 8)
		{
			DataSource source = new DataSource();
			
			source.type = DataSourceTypes.lookup(buffer.getInt());
			source.index = (long)buffer.getInt();
			
			return source;
		}
		else
		{
			return null;
		}
	}
	
	public DataSourceTypes getType()
	{
		return type;
	}
	
	public long getIndex()
	{
		return index;
	}
}
