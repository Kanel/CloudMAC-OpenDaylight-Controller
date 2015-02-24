package sFlow;

public class DataFormat
{
	private int enterpriseCode;
	private int formatNumber;
	
	public DataFormat(int value)
	{
		enterpriseCode = value >> 12;
		formatNumber = value & 0x00000FFF;
	}
	
	public int getEnterPriseCode()
	{
		return enterpriseCode;
	}
	
	public int getFormatNumber()
	{
		return formatNumber;
	}
}
