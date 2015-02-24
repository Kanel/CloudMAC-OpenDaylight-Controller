package sFlow;

public class Interface
{
	private InterfaceFormatTypes formatType;
	private int value;
	
	public Interface(int value)
	{
		formatType = InterfaceFormatTypes.lookup(value >> 30);
		this.value = value & 0x3FFFFFFF;
	}
	
	public InterfaceFormatTypes getFormatType()
	{
		return formatType;
	}
	
	public int getValue()
	{
		return value;
	}
}