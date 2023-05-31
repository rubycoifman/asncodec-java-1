package softnet.asn;

class OctetStringEncoder implements ElementEncoder
{
	private byte[] buffer;
	private int offset;
	private int length;
	
	public OctetStringEncoder(byte[] buffer, int offset, int length)
	{
		this.buffer = buffer;
		this.offset = offset;
		this.length = length;
	}
	
    public boolean isConstructed() {
    	return false;
    }

	public int estimateSize() {
		return 1 + LengthEncoder.estimateSize(length) + length;
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		binStack.stack(buffer, offset, length);
        int L_length = LengthEncoder.encode(length, binStack);
	    binStack.stack(UniversalTag.OctetString);
	    return 1 + L_length + length;
	}	

	public int encodeLV(BinaryStack binStack)
	{
		binStack.stack(buffer, offset, length);
        int L_length = LengthEncoder.encode(length, binStack);
	    return L_length + length;
	}	
}
