package softnet.asn;

import java.util.regex.Pattern;

class PrintableStringEncoder implements ElementEncoder
{
	private byte[] V_bytes;

    private PrintableStringEncoder(byte[] valueBytes)
    {
    	V_bytes = valueBytes;
    }

    public static PrintableStringEncoder create(String value)
    {
    	if (Pattern.matches("^[\\u0020-\\u007F]+$", value) == false)
    		throw new IllegalArgumentException(String.format("The string '%s' contains characters that are not allowed in 'ASN1 PrintableString'.", value));
    		
		if (Pattern.matches("^[A-Za-z0-9\\s'()+,\\-./:=?]*$", value) == false)    	
    		throw new IllegalArgumentException(String.format("The string '%s' contains characters that are not allowed in 'ASN1 PrintableString'.", value));

		byte[] valueBytes = value.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return new PrintableStringEncoder(valueBytes);
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
        binStack.stack(UniversalTag.PrintableString);
        return 1 + L_length + V_bytes.length;
    }

    public int encodeLV(BinaryStack binStack)
    {
        binStack.stack(V_bytes);
        int L_length = LengthEncoder.encode(V_bytes.length, binStack);
        return L_length + V_bytes.length;
    }
}
