package softnet.asn;

public class NullEncoder implements ElementEncoder
{
	public int estimateSize() {
		return 2;		
	}
	
    public boolean isConstructed() {
    	return false;
    }

	public int encodeTLV(BinaryStack binStack) {
        binStack.stack((byte)0);
        binStack.stack(UniversalTag.Null);
        return 2;
	}
	
	public int encodeLV(BinaryStack binStack) {
        binStack.stack((byte)0);
        return 1;
	}
}