package edu.kau.sflow;

public enum DataSourceTypes
{
	IF_INDEX(0),
	SMON_VLAN_DATA_SOURCE(1),
	ENT_PHYSICAL_ENTRY(2);
	
	@SuppressWarnings("unused")
	private int value;

    private DataSourceTypes(int value) {
        this.value = value;
    }
    
	public static DataSourceTypes lookup(int lookupValue)
	{
		switch (lookupValue)
        {
        	case 1:
        		return  SMON_VLAN_DATA_SOURCE;
			
        	case 2:
        		return  ENT_PHYSICAL_ENTRY;

			default:
				return IF_INDEX;
		}
	}
}
