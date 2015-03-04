package sFlow;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Address
{
	private AddressType type;
	private byte[] value;
	
	private Address() { }
	
	public static Address parse(ByteBuffer buffer)
	{
		Address address = new Address();
		
		if (buffer.remaining() >= 4)
		{
			return null;
		}
		
		address.type = AddressType.lookup(buffer.getInt());
		
		if (address.type == AddressType.IPv4)
		{
			if (buffer.remaining() >= 4)
			{
				address.value = new byte[4];
				
				buffer.get(address.value);
			}
			else
			{
				return null;
			}
		}
		else if (address.type == AddressType.IPv6)
		{
			if (buffer.remaining() >= 16)
			{
				address.value = new byte[16];
	
				buffer.get(address.value);
			}
			else
			{
				return null;
			}
		}
		else
		{
			address.value = null;
		}
		
		return address;
	}
	
	public AddressType getType()
	{
		return type;
	}
	
	public byte[] getValue()
	{
		return value;
	}
	
	public InetAddress toInetAddress() throws UnknownHostException
	{
		if (type == AddressType.IPv4)
		{
			return Inet4Address.getByAddress(value);
		}
		else if (type == AddressType.IPv6)
		{
			return Inet6Address.getByAddress(value);
		}
		else
		{
			return null;
		}
	}
}
