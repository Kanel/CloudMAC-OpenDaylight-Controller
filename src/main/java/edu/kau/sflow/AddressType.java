package edu.kau.sflow;

public enum AddressType
{
	UNKNOWN(0),
	IPv4(1),
	IPv6(2);
	
	@SuppressWarnings("unused")
	private int value;

    private AddressType(int value) {
        this.value = value;
    }
    
	public static AddressType lookup(int lookupValue)
	{
		switch (lookupValue)
        {
        	case 1:
        		return  IPv4;
			
        	case 2:
        		return  IPv6;

			default:
				return UNKNOWN;
		}
	}
}
