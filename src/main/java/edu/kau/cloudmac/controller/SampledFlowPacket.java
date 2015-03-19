package edu.kau.cloudmac.controller;

import java.nio.ByteBuffer;

import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.IPv4;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.UDP;

import edu.kau.cloudmac.CloudMACRecord;
import edu.kau.sflow.FlowDataRecord;
import edu.kau.sflow.FlowSample;
import edu.kau.sflow.SampleDatagram;
import edu.kau.sflow.SampleRecord;
import edu.kau.sflow.SampledHeader;

public class SampledFlowPacket
{
	public static boolean isSampledFlowPacket(Packet l2pkt)
	{
		Packet packet = l2pkt;
		
		if (packet instanceof Ethernet)
		{
			packet = packet.getPayload();
			
			if (packet instanceof IPv4)
			{
				packet = packet.getPayload();
				
				if (packet instanceof UDP)
				{
					return ((UDP)packet).getDestinationPort() == 6343;
				}
			}
		}
		return false;
	}
	
	private static int getNumberOfRecords(SampleRecord records[])
	{
		int amount = 0;
		
		for (SampleRecord i : records)
		{
			if (i instanceof FlowSample)
			{
				for (FlowDataRecord j : ((FlowSample)i).getFlowRecords())
				{
					if (j instanceof SampledHeader)
					{
						if (CloudMACPacket.isCloudMAC(((SampledHeader) j).getHeader()))
						{
							amount++;
						}
					}
				}
			}
		}
		
		return amount;
	}
	
	public static CloudMACRecord[] getCloudMACRecords(Packet l2pkt)
	{		
		UDP udpPacket = (UDP)l2pkt.getPayload().getPayload();
		ByteBuffer buffer = ByteBuffer.wrap(udpPacket.getRawPayload());
		SampleDatagram datagram = SampleDatagram.parse(buffer);
		SampleRecord records[] = datagram.getSamples();
		CloudMACRecord cloudmacRecords[] = new CloudMACRecord[getNumberOfRecords(records)];
		int index = 0;
		
		for (SampleRecord i : records)
		{
			if (i instanceof FlowSample)
			{
				for (FlowDataRecord j : ((FlowSample)i).getFlowRecords())
				{
					if (j instanceof SampledHeader)
					{
						if (CloudMACPacket.isCloudMAC(((SampledHeader) j).getHeader()))
						{
							SampledHeader header = (SampledHeader)j;
							
							buffer = ByteBuffer.wrap(header.getHeader());
							cloudmacRecords[index++] = CloudMACRecord.parse(buffer);
						}
					}
				}
			}
		}
		return cloudmacRecords;
	}
}
