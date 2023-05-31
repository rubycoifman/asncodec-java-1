package softnet.asn;

class UTF8StringEncoder implements ElementEncoder
{
	private byte[] V_bytes;

    private UTF8StringEncoder(byte[] valueBytes) {
    	V_bytes = valueBytes;
    }

    public static UTF8StringEncoder create(String value) {
        byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return new UTF8StringEncoder(valueBytes);
    }

    public int estimateSize() {
        return 1 + LengthEncoder.estimateSize(V_bytes.length) + V_bytes.length;
    }

    public boolean isConstructed() {
    	return false;
    }

    public int encodeTLV(BinaryStack binStack) {
        binStack.stack(V_bytes);
        int L_length = LengthEncoder.encode(V_bytes.length, binStack);
        binStack.stack(UniversalTag.UTF8String);
        return 1 + L_length + V_bytes.length;
    }

    public int encodeLV(BinaryStack binStack) {
        binStack.stack(V_bytes);
        int L_length = LengthEncoder.encode(V_bytes.length, binStack);
        return L_length + V_bytes.length;
    }
}
