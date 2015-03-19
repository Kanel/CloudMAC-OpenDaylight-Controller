package edu.kau.cloudmac;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class E802_11FrameControl
{
	private byte version;
	private byte type;
	private byte subtype;
	private boolean toDs;
	private boolean fromDs;
	private boolean moreFrag;
	private boolean retry;
	private boolean powerManagement;
	private boolean moreData;
	private boolean wep;
	private boolean order;
	
	private E802_11FrameControl() { }
	
	public static E802_11FrameControl parse(ByteBuffer buffer)
	{
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		if (buffer.remaining() >= 2)
		{
			E802_11FrameControl control = new E802_11FrameControl();
			short value = buffer.getShort();
			
			control.version =  			(byte)((value & 0b1100000000000000) >> 14);
			control.type =     			(byte)((value & 0b0011000000000000) >> 12);
			control.subtype =  			(byte)((value & 0b0000111100000000) >> 8);
			control.toDs =     			(value & 0b0000000010000000) == 0b0000000010000000;
			control.fromDs =   			(value & 0b0000000001000000) == 0b0000000001000000;
			control.moreFrag = 			(value & 0b0000000000100000) == 0b0000000000100000;
			control.retry =    			(value & 0b0000000000010000) == 0b0000000000010000;
			control.powerManagement = 	(value & 0b0000000000001000) == 0b0000000000001000;
			control.moreData = 			(value & 0b0000000000000100) == 0b0000000000000100;
			control.wep = 				(value & 0b0000000000000010) == 0b0000000000000010;
			control.order = 			(value & 0b0000000000000001) == 0b0000000000000001;
			
			return control;
		}
		else
		{
			return null;
		}
	}
	
	public byte getVersion()
	{
		return version;
	}
	
	public byte getType()
	{
		return type;
	}
	
	public byte getSubtype()
	{
		return subtype;
	}
	
	public boolean getToDs()
	{
		return toDs;
	}
	
	public boolean getFromDs()
	{
		return fromDs;
	}
	
	public boolean getMoreFrag()
	{
		return moreFrag;
	}
	
	public boolean getRetry()
	{
		return retry;
	}
	
	public boolean getPowerManagement()
	{
		return powerManagement;
	}
	
	public boolean getMoreData()
	{
		return moreData;
	}
	
	public boolean getWep()
	{
		return wep;
	}
	
	public boolean getOrder()
	{
		return order;
	}
}
