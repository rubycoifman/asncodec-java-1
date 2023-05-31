package softnet.asn;

class BMPStringEncoder implements ElementEncoder
{	
	private byte[] V_bytes;
	
    private BMPStringEncoder(byte[] valueBytes) {
    	V_bytes = valueBytes;
    }
    
    public static BMPStringEncoder create(String value) {
        byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_16BE);
        return new BMPStringEncoder(valueBytes);
    }    

    public boolean isConstructed() {
    	return false;
    }
    
    public int estimateSize() {
        return 1 + LengthEncoder.estimateSize(V_bytes.length) + V_bytes.length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
        binStack.stack(V_bytes);
        int L_length = LengthEncoder.encode(V_bytes.length, binStack);
        binStack.stack(UniversalTag.BMPString);
        return 1 + L_length + V_bytes.length;
    }
    
    public int encodeLV(BinaryStack binStack)
    {
        binStack.stack(V_bytes);
        int L_length = LengthEncoder.encode(V_bytes.length, binStack);
        return L_length + V_bytes.length;
    }
}
