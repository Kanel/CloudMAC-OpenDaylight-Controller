package sFlow.SampledHeaders;

import java.net.Inet4Address;

public class SampledIPv4
{
	private long length;
	private long protocol;
	private Inet4Address source;
	private Inet4Address destination;
	private long sourcePort;
	private long destinationPort;
	private long tcpFlags;
	private long tos;
	
	public SampledIPv4(long length, long protocol, Inet4Address source, Inet4Address destination, long sourcePort, long destinationPort, long tcpFlags, long tos)
	{
		this.length = length;
		this.protocol = protocol;
		this.source = source;
		this.destination = destination;
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.tcpFlags = tcpFlags;
		this.tos = tos;
	}
	
	public long getLength()
	{
		return length;
	}
	
	public long getProtocol()
	{
		return protocol;
	}
	
	public Inet4Address getSource()
	{
		return source;
	}
	
	public Inet4Address getDestination()
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
		return tos;
	}
}
