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
	
	/**
	 * The value returned's meaning is dependent on what type of interface this object represents.
	 * 
	 * @return 
	 * SINGLE_INTERFACE: The ifindex of the interface the packet was received on or the interface it was sent on.
	 *
	 * PACKET_DISCARDED: The reason code explaining why the packet was dropped.
	 * 		0 - 255 use ICMP Destination Unreachable codes
     *               See www.iana.org for authoritative list.
     *              RFC 1812, section 5.2.7.1 describes the
     *               current codes.  Note that the use of
     *               these codes does not imply that the
     *               packet to which they refer is an IP
     *               packet, or if it is, that an ICMP message
     *               of any kind was generated for it.
     *               Current value are:
     *                 0  Net Unreachable
     *                 1  Host Unreachable
     *                 2  Protocol Unreachable
     *                 3  Port Unreachable
     *                 4  Fragmentation Needed and 
     *                    Don't Fragment was Set
     *                 5  Source Route Failed
     *                 6  Destination Network Unknown
     *                 7  Destination Host Unknown
     *                 8  Source Host Isolated
     *                 9  Communication with Destination 
     *                    Network is Administratively 
     *                    Prohibited
     *                10  Communication with Destination Host 
     *                    is Administratively Prohibited
     *                11  Destination Network Unreachable for 
     *                    Type of Service
     *                12  Destination Host Unreachable for 
     *                    Type of Service
     *                13  Communication Administratively 
     *                    Prohibited
     *                14  Host Precedence Violation
     *                15  Precedence cutoff in effect
     *       256 = unknown
     *       257 = ttl exceeded
     *       258 = ACL
     *       259 = no buffer space
     *       260 = RED
     *       261 = traffic shaping/rate limiting
     *       262 = packet too big (for protocols that don't 
     *             support fragmentation)
     *     
	 * MULTIPLE_DESTINATION_INTERFACES: The number of destination interfaces, a value of zero means that there was an unknown number greater than one.
	 */
	public int getValue()
	{
		return value;
	}
}