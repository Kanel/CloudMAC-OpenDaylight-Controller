package edu.kau.ini;

public class Token
{
	String value;
	Tags tag;
	
	public Token(String value, Tags tag)
	{
		this.value = value;
		this.tag = tag;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public Tags getTag()
	{
		return tag;
	}
	
	public void setTag(Tags tag)
	{
		this.tag = tag;
	}
}
