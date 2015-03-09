package edu.kau.sflow;

public enum HeaderProtocols
{
	ETHERNET_ISO88023(1),
	ISO88024_TOKENBUS(2),
	ISO88025_TOKENRING(3),
	FDDI(4),
	FRAME_RELAY(5),
	X25(6),
   	PPP(7),
   	SMDS(8),
   	AAL5(9),
   	AAL5_IP(10),
   	IPv4(11),
   	IPv6(12),
   	MPLS(13),
   	POS(14),
   	UNKNOWN(0);
	
	@SuppressWarnings("unused")
	private int value;

    private HeaderProtocols(int value) {
        this.value = value;
    }
    
	public static HeaderProtocols lookup(int lookupValue)
	{
		switch (lookupValue)
        {
        	case 1:
        		return  ETHERNET_ISO88023;
			
        	case 2:
        		return  ISO88024_TOKENBUS;
        		
        	case 3:
        		return  ISO88025_TOKENRING;
        		
        	case 4:
        		return  FDDI;
        		
        	case 5:
        		return  FRAME_RELAY;
        		
        	case 6:
        		return X25;
        		
        	case 7:
        		return  PPP;
        		
        	case 8:
        		return  SMDS;
        		
        	case 9:        		
        		return  AAL5;
        		
        	case 10:
        		return  AAL5_IP;
        		
        	case 11:
        		return  IPv4;
        		
        	case 12:
        		return  IPv6;
        		
        	case 13:
        		return  MPLS;  
        		
        	case 14:
        		return  POS;  

			default:
				return UNKNOWN;
		}
	}
}
