package edu.kau.cloudmac.controller;

public class CloudMacConfig {
	private short flowDuration;
	private short blockFlowduration;
	private short graceDuration;
	private long accessPointExpiration;
	private long tunnelExpiration;
	private long terminationPointExpiration;
	private long beaconFlowDuration;
	private short[] ethernetTypes;
	private short[] queueIndices;
	private short blockPriority;
	private short tunnelPriority;
	private short beaconPriority;
	private short termiantionPointConfigPort;
	private short handoverThreshold;
	
	public CloudMacConfig()
	{
		// Todo: comments!
	}
	
	public short getFlowDuration()
	{
		return flowDuration;
	}
	
	public void setFlowDuration(short value)
	{
		this.flowDuration = value;
	}
	
	public short getBlockFlowDuration()
	{
		return blockFlowduration;
	}
	
	public void setBlockFlowduration(short value)
	{
		this.blockFlowduration = value;
	}
	
	public short getGraceDuration()
	{
		return graceDuration;
	}
	
	public void setGraceDuration(short value)
	{
		this.graceDuration = value;
	}	
	
	public long getAccessPointExpiration()
	{
		return accessPointExpiration;
	}
	
	public void setAccessPointExpiration(long value)
	{
		this.accessPointExpiration = value;
	}	
	
	public long getTunnelExpiration()
	{
		return tunnelExpiration;
	}
	
	public void setTunnelExpiration(long value)
	{
		this.tunnelExpiration = value;
	}
	
	public long getTerminationPointExpiration()
	{
		return terminationPointExpiration;
	}
	
	public void setTerminationPointExpiration(long value)
	{
		this.terminationPointExpiration = value;
	}
	
	public long getBeaconFlowDuration()
	{
		return beaconFlowDuration;
	}
	
	public void setBeaconFlowDuration(long value)
	{
		this.beaconFlowDuration = value;
	}
	
	public short[] getEthernetTypes()
	{
		return ethernetTypes;
	}
	
	public void setEthernetTypes(short[] value)
	{
		this.ethernetTypes = value;
	}
	
	public short[] getQueueIndices()
	{
		return queueIndices;
	}
	
	public void setQueueIndices(short[] value)
	{
		this.queueIndices = value;
	}
	
	public short getBlockPriority()
	{
		return blockPriority;
	}
	
	public void setBlockPriority(short value)
	{
		this.blockPriority = value;
	}
	
	public short getTunnelPriority()
	{
		return tunnelPriority;
	}
	
	public void settTunnelPriority(short value)
	{
		this.tunnelPriority = value;
	}
	
	public short getBeaconPriority()
	{
		return beaconPriority;
	}
	
	public void setBeaconPriority(short value)
	{
		this.beaconPriority = value;
	}
	
	public short getTerminationPointConfigPort()
	{
		return termiantionPointConfigPort;
	}
	
	public void setTerminationPointConfigPort(short value)
	{
		this.termiantionPointConfigPort = value;
	}
	
	public short getHandoverThreshold()
	{
		return handoverThreshold;
	}
	
	public void setHandoverThreshold(short value)
	{
		this.handoverThreshold = value;
	}
	
	
	
}