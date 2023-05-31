package softnet.asn;

class TimpEncoder implements ElementEncoder
{
	private TagClass tagClass;
	private int tag;
	private ElementEncoder elementEncoder;

	private static int C_ContextSpecific_Constructed = 0xA0;
	private static int C_Application_Constructed = 0x60;
	private static int C_Private_Constructed = 0xE0;
	private static int C_ContextSpecific_Primitive = 0x80;
	private static int C_Application_Primitive = 0x40;
	private static int C_Private_Primitive = 0xC0;

	public TimpEncoder(int tag, ElementEncoder elementEncoder)
    {
		if(tag < 0 || tag > 30)
			throw new IllegalArgumentException("The tag value must be in the range between 0 and 30.");

        this.tag = tag;
        this.tagClass = TagClass.ContextSpecific;
        this.elementEncoder = elementEncoder;
    }
	
    public TimpEncoder(int tag, TagClass tc, ElementEncoder elementEncoder)
    {
		if(tag < 0 || tag > 30)
			throw new IllegalArgumentException("The tag value must be in the range between 0 and 30.");

		if(tc == null)
			throw new IllegalArgumentException("The tag class is not specified.");

        this.tag = tag;
        this.tagClass = tc;
        this.elementEncoder = elementEncoder;
    }
    
    public int estimateSize() {
        return elementEncoder.estimateSize();
    }

    public boolean isConstructed() {
    	return elementEncoder.isConstructed();
    }

    public int encodeTLV(BinaryStack binStack)
    {
        int LV_length = elementEncoder.encodeLV(binStack);
        
        if(elementEncoder.isConstructed() == false) {
        	if(tagClass == TagClass.ContextSpecific)
        		binStack.stack(C_ContextSpecific_Primitive | tag);
        	else if(tagClass == TagClass.Application)
        		binStack.stack(C_Application_Primitive | tag);
        	else // tagClass == TagClass.Private
        		binStack.stack(C_Private_Primitive | tag);        	
        }
        else {
        	if(tagClass == TagClass.ContextSpecific)
            	binStack.stack(C_ContextSpecific_Constructed | tag);
        	else if(tagClass == TagClass.Application)
        		binStack.stack(C_Application_Constructed | tag);
        	else // tagClass == TagClass.Private
        		binStack.stack(C_Private_Constructed | tag);        	
        }
        return 1 + LV_length;
    }

    public int encodeLV(BinaryStack binStack) {
        return elementEncoder.encodeLV(binStack);
    }
}
