package sFlow.FlowDataTypes;

import java.net.Inet6Address;

public class SampledIPv6
{
	private long length;
	private long protocol;
	private Inet6Address source;
	private Inet6Address destination;
	private long sourcePort;
	private long destinationPort;
	private long tcpFlags;
	private long priority;
	
	public SampledIPv6(long length, long protocol, Inet6Address source, Inet6Address destination, long sourcePort, long destinationPort, long tcpFlags, long priority)
	{
		this.length = length;
		this.protocol = protocol;
		this.source = source;
		this.destination = destination;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.tcpFlags = tcpFlags;
		this.priority = priority;
	}
	
	public long getLength()
	{
		return length;
	}
	
	public long getProtocol()
	{
		return protocol;
	}
	
	public Inet6Address getSource()
	{
		return source;
	}
	
	public Inet6Address getDestination()
	{
		return destination;
	}
	
	public long getSourcePort()
	{
		return sourcePort;
	}
	
	public long getDestinationPort()
	{
		return destinationPort;
	}
	
	public long getTcpFlags()
	{
		return tcpFlags;
	}
	
	public long getTos()
	{
		return priority;
	}
}
