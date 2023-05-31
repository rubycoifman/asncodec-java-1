package softnet.asn;

import java.util.ArrayList;
import java.util.GregorianCalendar;

class SequenceEncoderImp implements SequenceEncoder, ElementEncoder
{
	public SequenceEncoderImp()
	{
		childNodes = new ArrayList<ElementEncoder>();
	}
	
	private static byte C_Constructed_Flag = 0x20;
	private static byte C_Universal_Constructed_Sequence = (byte)(C_Constructed_Flag | UniversalTag.Sequence);

	private ArrayList<ElementEncoder> childNodes;
	
	public int count() {
		return childNodes.size();
	}
	
	public int getSize() {
		return estimateSize();
	}
	
    public boolean isConstructed() {
    	return true;
    }
	
	public int estimateSize()
	{
		int V_Length = 0;
        for(ElementEncoder encoder: childNodes) {
            V_Length += encoder.estimateSize();
        }
        return 1 + LengthEncoder.estimateSize(V_Length) + V_Length;		
	}
	
	public int encodeTLV(BinaryStack binStack)
	{
		int v_length = 0;
        for (int i = childNodes.size() - 1; i >= 0; i--) {
        	v_length += childNodes.get(i).encodeTLV(binStack);
        }
        int l_length = LengthEncoder.encode(v_length, binStack);
        binStack.stack(C_Universal_Constructed_Sequence);
        return 1 + l_length + v_length;
	}

	public int encodeLV(BinaryStack binStack)
	{
		int v_length = 0;
        for (int i = childNodes.size() - 1; i >= 0; i--) {
        	v_length += childNodes.get(i).encodeTLV(binStack);
        }
        int l_length = LengthEncoder.encode(v_length, binStack);
        return l_length + v_length;
	}

	public SequenceEncoder Sequence()
	{
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		childNodes.add(encoder);
		return encoder;
	}
	
	public SequenceOfEncoder SequenceOf(UType uType)
	{
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");
		SequenceOfEncoderImp encoder = new SequenceOfEncoderImp(uType);
		childNodes.add(encoder);
		return encoder;
	}
	
	public void Int32(int value)
	{
		childNodes.add(new Int32Encoder(value));
	}
	
	public void Int64(long value)
	{
		childNodes.add(new Int64Encoder(value));
	}

	public void Boolean(boolean value)
	{
		childNodes.add(new BooleanEncoder(value));
	}

	public void Real32(float value) 
	{
		childNodes.add(Real32Encoder.create(value));
	}

	public void Real64(double value) 
	{
		childNodes.add(Real64Encoder.create(value));
	}

	public void UTF8String(String value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
		childNodes.add(UTF8StringEncoder.create(value));
	}
	
	public void BMPString(String value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
		childNodes.add(BMPStringEncoder.create(value));		
	}

	public void IA5String(String value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
		childNodes.add(IA5StringEncoder.create(value));	
	}
	
	public void PrintableString(String value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
		childNodes.add(PrintableStringEncoder.create(value));	
	}

	public void GndTime(java.util.GregorianCalendar value) 
	{
		if(value == null)
			throw new NullPointerException("The parameter 'value' must not be null.");		
		GndTimeGCEncoder encoder = GndTimeGCEncoder.create((GregorianCalendar)value.clone());
		childNodes.add(encoder);
	}

	public void OctetString(byte[] buffer)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		childNodes.add(new OctetStringEncoder(buffer, 0, buffer.length));
	}
	
	public void OctetString(byte[] buffer, int offset, int length)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");

		childNodes.add(new OctetStringEncoder(buffer, offset, length));
	}
	
	public void OctetString(java.util.UUID value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		childNodes.add(new OctetStringEncoder(nioBuffer.array(), 0, 16));
	}
	
	public void Null()
	{
		childNodes.add(new NullEncoder());
	}

	public TexpEncoder Texp() {
		TexpEncoderImp encoder = new TexpEncoderImp();
		childNodes.add(encoder);		
		return encoder;		
	}
			
	public SequenceEncoder Sequence(int tag)
	{
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		childNodes.add(new TimpEncoder(tag, encoder));		
		return encoder;
	}
	
	public SequenceEncoder Sequence(int tag, TagClass tc)
	{
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		childNodes.add(new TimpEncoder(tag, tc, encoder));		
		return encoder;		
	}	
	
	public SequenceOfEncoder SequenceOf(int tag, UType uType)
	{
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");
		SequenceOfEncoderImp encoder = new SequenceOfEncoderImp(uType);
		childNodes.add(new TimpEncoder(tag, encoder));		
		return encoder;		
	}

	public SequenceOfEncoder SequenceOf(int tag, UType uType, TagClass tc)
	{
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");
		SequenceOfEncoderImp encoder = new SequenceOfEncoderImp(uType);
		childNodes.add(new TimpEncoder(tag, tc, encoder));		
		return encoder;		
	}

	public void Int32(int tag, int value)
	{
		Int32Encoder encoder = new Int32Encoder(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void Int32(int tag, int value, TagClass tc)
	{
		Int32Encoder encoder = new Int32Encoder(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void Int64(int tag, long value)
	{
		Int64Encoder encoder = new Int64Encoder(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void Int64(int tag, long value, TagClass tc)
	{
		Int64Encoder encoder = new Int64Encoder(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void Boolean(int tag, boolean value)
	{
		BooleanEncoder encoder = new BooleanEncoder(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void Boolean(int tag, boolean value, TagClass tc)
	{
		BooleanEncoder encoder = new BooleanEncoder(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void Real32(int tag, float value) 
	{
		Real32Encoder encoder = Real32Encoder.create(value);
        childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void Real32(int tag, float value, TagClass tc) 
	{
		Real32Encoder encoder = Real32Encoder.create(value);
        childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void Real64(int tag, double value) 
	{
		Real64Encoder encoder = Real64Encoder.create(value);
        childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void Real64(int tag, double value, TagClass tc) 
	{
		Real64Encoder encoder = Real64Encoder.create(value);
        childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void UTF8String(int tag, String value)
	{
		validateValue(value);
		UTF8StringEncoder encoder = UTF8StringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void UTF8String(int tag, String value, TagClass tc)
	{
		validateValue(value);
		UTF8StringEncoder encoder = UTF8StringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void BMPString(int tag, String value)
	{
		validateValue(value);
		BMPStringEncoder encoder = BMPStringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, encoder));	
	}

	public void BMPString(int tag, String value, TagClass tc)
	{
		validateValue(value);
		BMPStringEncoder encoder = BMPStringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));	
	}

	public void IA5String(int tag, String value)
	{
		validateValue(value);
		IA5StringEncoder encoder = IA5StringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void IA5String(int tag, String value, TagClass tc)
	{
		validateValue(value);
		IA5StringEncoder encoder = IA5StringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void PrintableString(int tag, String value)
	{
		validateValue(value);
		PrintableStringEncoder encoder = PrintableStringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, encoder));
	}		

	public void PrintableString(int tag, String value, TagClass tc)
	{
		validateValue(value);
		PrintableStringEncoder encoder = PrintableStringEncoder.create(value);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}		

	public void GndTime(int tag, java.util.GregorianCalendar value)
	{
		validateValue(value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		GndTimeGCEncoder encoder = GndTimeGCEncoder.create(valueClone);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void GndTime(int tag, java.util.GregorianCalendar value, TagClass tc)
	{
		validateValue(value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		GndTimeGCEncoder encoder = GndTimeGCEncoder.create(valueClone);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void OctetString(int tag, byte[] buffer)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		OctetStringEncoder encoder = new OctetStringEncoder(buffer, 0, buffer.length);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void OctetString(int tag, byte[] buffer, TagClass tc)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		OctetStringEncoder encoder = new OctetStringEncoder(buffer, 0, buffer.length);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void OctetString(int tag, byte[] buffer, int offset, int length)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");

		OctetStringEncoder encoder = new OctetStringEncoder(buffer, offset, length);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void OctetString(int tag, byte[] buffer, int offset, int length, TagClass tc)
	{
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");

		OctetStringEncoder encoder = new OctetStringEncoder(buffer, offset, length);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	public void OctetString(int tag, java.util.UUID value)
	{
		validateValue(value);
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		OctetStringEncoder encoder = new OctetStringEncoder(nioBuffer.array(), 0, 16);
		childNodes.add(new TimpEncoder(tag, encoder));
	}

	public void OctetString(int tag, java.util.UUID value, TagClass tc)
	{
		validateValue(value);
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		OctetStringEncoder encoder = new OctetStringEncoder(nioBuffer.array(), 0, 16);
		childNodes.add(new TimpEncoder(tag, tc, encoder));
	}

	private void validateValue(Object value)
	{
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
	}
}
