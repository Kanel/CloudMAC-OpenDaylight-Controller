package sFlow;

public enum InterfaceFormatTypes
{
	SINGLE_INTERFACE(0),
	PACKET_DISCARDED(1),
	MULTIPLE_DESTINATION_INTERFACES(2);
	
	@SuppressWarnings("unused")
	private int value;

    private InterfaceFormatTypes(int value) {
        this.value = value;
    }
    
	public static InterfaceFormatTypes lookup(int lookupValue)
	{
		switch (lookupValue)
        {
        	case 1:
        		return  PACKET_DISCARDED;
			
        	case 2:
        		return  MULTIPLE_DESTINATION_INTERFACES;

			default:
				return SINGLE_INTERFACE;
		}
	}
}
