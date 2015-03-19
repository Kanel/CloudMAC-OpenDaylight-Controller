package edu.kau.cloudmac;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RadioTapHeader
{
	private short version; // unsigned byte
	private short pad; // unsigned byte
	private int len; // unsigned short
	private int present; // unsigned short
	private short rate; // unsigned byte
	private int channelFrequency; // unsigned short
	private int channelFlags; // unsigned short
	private short antennaSignal; // unsigned byte
	private short antennaNoise; // unsigned byte
	
	private RadioTapHeader() { }
	
	private static void align(ByteBuffer buffer, int alignment)
	{
		if (buffer.position() % alignment != 0)
		{
			buffer.position((buffer.position() % alignment) + alignment);
		}
	}
	
	private static void skip(ByteBuffer buffer, int skip)
	{
		buffer.position(buffer.position() + skip);
	}
	
	public static RadioTapHeader parse(ByteBuffer buffer)
	{
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		if (buffer.remaining() >= 8)
		{
			RadioTapHeader header = new RadioTapHeader();
			int start = buffer.position();			
			
			header.version = (short)buffer.get();
			header.pad = (short)buffer.get();
			header.len = (int)buffer.getShort();
			header.present = buffer.getInt();

			// Only Rate, Channel, Antenna Signal, Antenna noise are supported.
			// TSFT - Skip this.
			if ((header.present & 0b1) == 0b1)
			{
				align(buffer, 8);
				skip(buffer, 8);
			}
			// Flags - Skip this.
			if ((header.present & 0b10) == 0b10)
			{
				skip(buffer, 1);
			}
			
			// Rate - We want this.
			if ((header.present & 0b100) == 0b100)
			{
				header.rate = (short)buffer.get();
			}
			
			// Channel - We want this.
			if ((header.present & 0b1000) == 0b1000)
			{
				align(buffer, 2);
				
				header.channelFrequency = (int)buffer.getShort();
				header.channelFlags = (int)buffer.getShort();
			}

			// FHSS - I don't even know what this is so skip!
			if ((header.present & 0b10000) == 0b10000)
			{
				skip(buffer, 2);
			}

			// Antenna Signal - We want this.
			if ((header.present & 0b100000) == 0b100000)
			{
				header.antennaSignal = (short)buffer.get();
			}
			
			// Antenna Noise - We want this.
			if ((header.present & 0b1000000) == 0b1000000)
			{
				header.antennaNoise = (short)buffer.get();
			}
			
			// Skip all possible other fields.
			buffer.position(start + header.len);
						
			return header;
		}
		else
		{
			return null;
		}
	}
	
	public short getVersion()
	{
		return version;
	}
	
	public short getPadding()
	{
		return pad;
	}
	
	public int getLength()
	{
		return len;
	}
	
	public int getPresent()
	{
		return present;
	}
	
	public int getRate()
	{
		return rate;
	}
	
	public int getChannelFrequency()
	{
		return channelFrequency;
	}
	
	public int getChannelFlags()
	{
		return channelFlags;
	}
	
	public short getAntennaSignal()
	{
		return antennaSignal;
	}
	
	public short getantennaNoise()
	{
		return antennaNoise;
	}
}