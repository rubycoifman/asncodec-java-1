package softnet.asn;

class BooleanEncoder implements ElementEncoder
{
	private boolean value;
	
	public BooleanEncoder(boolean value)
	{
		this.value = value;
	}
	
    public boolean isConstructed() {
    	return false;
    }
	
	public int estimateSize()
	{
		return 3;		
	}
	
	public int encodeTLV(BinaryStack binStack)
	{		
        if (value)
        {
            binStack.stack((byte)0xFF);
            binStack.stack((byte)1);
            binStack.stack(UniversalTag.Boolean);
        }
        else
        {
        	binStack.stack((byte)0);
            binStack.stack((byte)1);
            binStack.stack(UniversalTag.Boolean);            
        }
        return 3;
	}
	
	public int encodeLV(BinaryStack binStack)
	{		
        if (value)
        {
            binStack.stack((byte)0xFF);
            binStack.stack((byte)1);
        }
        else
        {
        	binStack.stack((byte)0);
            binStack.stack((byte)1);
        }
        return 2;
	}
}
