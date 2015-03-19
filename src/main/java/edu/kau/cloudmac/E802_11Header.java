package edu.kau.cloudmac;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class E802_11Header
{
	private E802_11FrameControl control;
	private int durationId; // unsigned short
	private byte[] address1;
	private byte[] address2;
	private byte[] address3;
	private int sequenceControl; // unsigned short
	private byte[] address4;
	
	private E802_11Header() 
	{
		address1 = new byte[6];
		address2 = new byte[6];
		address3 = new byte[6];
		address4 = new byte[6];
	}
	
	public static E802_11Header parse(ByteBuffer buffer)
	{
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		if (buffer.remaining() >= 30)
		{
			E802_11Header header = new E802_11Header();
			
			buffer.order(ByteOrder.BIG_ENDIAN);
			
			header.control = E802_11FrameControl.parse(buffer);
			header.durationId = (int)buffer.getShort();
		
			buffer.get(header.address1);
			buffer.get(header.address2);
			buffer.get(header.address3);
			
			header.sequenceControl = (int)buffer.getShort();
			
			buffer.get(header.address4);
			
			if (header.control == null)
			{
				return null;
			}
			
			return header;
		}
		else
		{
			return null;
		}
	}
	
	public byte[] getSourceAddress()
	{
		if (control.getToDs())
		{
			if (control.getFromDs())
			{
				return address4;
			}
			else
			{
				return address2;
			}
		}
		else
		{
			if (control.getFromDs())
			{
				return address3;
			}
			else
			{
				return address2;
			}
		}
	}
	
	public byte[] getDestinationAddress()
	{
		if (control.getToDs())
		{
			if (control.getFromDs())
			{
				return address3;
			}
			else
			{
				return address2;
			}
		}
		else
		{
			return address1;
		}
	}
	
	public byte[] getBssidAddress()
	{
		if (control.getToDs())
		{
			if (control.getFromDs())
			{
				return null;
			}
			else
			{
				return address1;
			}
		}
		else
		{
			if (control.getFromDs())
			{
				return address2;
			}
			else
			{
				return address3;
			}
		}
	}
	
	public E802_11FrameControl getFrameControl()
	{
		return control;
	}
	
	public int GetDurationId()
	{
		return durationId;
	}
	
	public byte[] getAddress1()
	{
		return address1;
	}
	
	public byte[] getAddress2()
	{
		return address2;
	}
	
	public byte[] getAddress3()
	{
		return address3;
	}
	
	public int getSequenceControl()
	{
		return sequenceControl;
	}
	
	public byte[] getAddress4()
	{
		return address4;
	}
}