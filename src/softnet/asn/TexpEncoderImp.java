package softnet.asn;

import java.util.GregorianCalendar;

public class TexpEncoderImp implements TexpEncoder, ElementEncoder
{
	private static int C_ContextSpecific_Constructed = 0xA0;
	private static int C_Application_Constructed = 0x60;
	private static int C_Private_Constructed = 0xE0;

	private int tag = -1;
	private TagClass tagClass = null;
	private ElementEncoder encoder = null;
	
	public void setThis(int tag)
	{
		if(tag < 0 || tag > 30)
			throw new IllegalArgumentException("The tag value must be in the range between 0 and 30.");
		this.tag = tag;
        this.tagClass = TagClass.ContextSpecific;		
	}
	
	public void setThis(int tag, TagClass tc)
	{
		if(tag < 0 || tag > 30)
			throw new IllegalArgumentException("The tag value must be in the range between 0 and 30.");
		if(tc == null)
			throw new IllegalArgumentException("The tag class is not specified.");
		this.tag = tag;
        this.tagClass = tc;		
	}
	
	public boolean isConstructed() {
		return true;
	}
	
	public int estimateSize() {		
		if(tag == -1)
			throw new IllegalStateException("The tag was not specified.");
		if(encoder == null)
			throw new IllegalStateException("An element to be tagged was not specified.");
		
		int V_Length = encoder.estimateSize();
        return 1 + LengthEncoder.estimateSize(V_Length) + V_Length;
	}
	
	public int encodeTLV(BinaryStack binStack) {
		if(tag == -1)
			throw new IllegalStateException("The tag was not specified.");
		if(encoder == null)
			throw new IllegalStateException("An element to be tagged was not specified.");
		
		int v_length = encoder.encodeTLV(binStack);
        int l_length = LengthEncoder.encode(v_length, binStack);
    	if(tagClass == TagClass.ContextSpecific)
        	binStack.stack(C_ContextSpecific_Constructed | tag);
    	else if(tagClass == TagClass.Application)
    		binStack.stack(C_Application_Constructed | tag);
    	else // tagClass == TagClass.Private
    		binStack.stack(C_Private_Constructed | tag);        	
        return 1 + l_length + v_length;		
	}
	
	public int encodeLV(BinaryStack binStack) {
		if(tag == -1)
			throw new IllegalStateException("The tag was not specified.");
		if(encoder == null)
			throw new IllegalStateException("An element to be tagged was not specified.");
		
		int v_length = encoder.encodeTLV(binStack);
        int l_length = LengthEncoder.encode(v_length, binStack);
        return l_length + v_length;
	}
	
	public SequenceEncoder Sequence()
	{
		validateState();
		encoder = new SequenceEncoderImp();
		return (SequenceEncoder)encoder;
	}
	
	public SequenceOfEncoder SequenceOf(UType uType)
	{
		validateState();
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");
		
		encoder = new SequenceOfEncoderImp(uType);		
		return (SequenceOfEncoder)encoder;
	}
	
	public void Int32(int value)
	{
		validateState();
		encoder = new Int32Encoder(value);
	}
	
	public void Int64(long value)
	{
		validateState();
		encoder = new Int64Encoder(value);
	}

	public void Boolean(boolean value)
	{
		validateState();
		encoder = new BooleanEncoder(value);
	}

	public void Real32(float value) 
	{
		validateState();
		encoder = Real32Encoder.create(value);
	}

	public void Real64(double value) 
	{
		validateState();
		encoder = Real64Encoder.create(value);
	}

	public void UTF8String(String value)
	{
		validateStateAndValue(value);
		encoder = UTF8StringEncoder.create(value);
	}
	
	public void BMPString(String value)
	{
		validateStateAndValue(value);
		encoder = BMPStringEncoder.create(value);		
	}

	public void IA5String(String value)
	{
		validateStateAndValue(value);
		encoder = IA5StringEncoder.create(value);	
	}
	
	public void PrintableString(String value)
	{
		validateStateAndValue(value);
		encoder = PrintableStringEncoder.create(value);	
	}

	public void GndTime(java.util.GregorianCalendar value) 
	{
		validateStateAndValue(value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		encoder = GndTimeGCEncoder.create(valueClone);
	}

	public void OctetString(byte[] buffer)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		encoder = new OctetStringEncoder(buffer, 0, buffer.length);
	}
	
	public void OctetString(byte[] buffer, int offset, int length)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");
		
		encoder = new OctetStringEncoder(buffer, offset, length);
	}
	
	public void OctetString(java.util.UUID value)
	{
		validateStateAndValue(value);
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		encoder = new OctetStringEncoder(nioBuffer.array(), 0, 16);
	}
	
	public TexpEncoder Texp() {
		validateState();		
		encoder = new TexpEncoderImp();
		return (TexpEncoder)encoder;	
	}
	
	public SequenceEncoder Sequence(int tag)
	{
		validateState();
		SequenceEncoderImp encoder2 = new SequenceEncoderImp();
		encoder = new TimpEncoder(tag, encoder2);		
		return encoder2;
	}
	
	public SequenceEncoder Sequence(int tag, TagClass tc)
	{
		validateState();
		SequenceEncoderImp encoder2 = new SequenceEncoderImp();
		encoder = new TimpEncoder(tag, tc, encoder2);		
		return encoder2;
	}
	
	public SequenceOfEncoder SequenceOf(int tag, UType uType)
	{
		validateState();
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");

		SequenceOfEncoderImp encoder2 = new SequenceOfEncoderImp(uType);
		encoder = new TimpEncoder(tag, encoder2);		
		return encoder2;		
	}

	public SequenceOfEncoder SequenceOf(int tag, UType uType, TagClass tc)
	{
		validateState();
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		if(uType == UType.Null)
			throw new IllegalArgumentException("It is not allowed to create a collection of ASN.1 NULL elements.");

		SequenceOfEncoderImp encoder2 = new SequenceOfEncoderImp(uType);
		encoder = new TimpEncoder(tag, tc, encoder2);		
		return encoder2;		
	}

	public void Int32(int tag, int value)
	{
		validateState();
		encoder = new TimpEncoder(tag, new Int32Encoder(value));
	}

	public void Int32(int tag, int value, TagClass tc)
	{
		validateState();
		encoder = new TimpEncoder(tag, tc, new Int32Encoder(value));
	}
	
	public void Int64(int tag, long value)
	{
		validateState();
		encoder = new TimpEncoder(tag, new Int64Encoder(value));
	}

	public void Int64(int tag, long value, TagClass tc)
	{
		validateState();
		encoder = new TimpEncoder(tag, tc, new Int64Encoder(value));
	}

	public void Boolean(int tag, boolean value)
	{
		validateState();
		encoder = new TimpEncoder(tag, new BooleanEncoder(value));
	}

	public void Boolean(int tag, boolean value, TagClass tc)
	{
		validateState();
		encoder = new TimpEncoder(tag, tc, new BooleanEncoder(value));
	}

	public void Real32(int tag, float value) 
	{
		validateState();
		encoder = new TimpEncoder(tag, Real32Encoder.create(value));
	}

	public void Real32(int tag, float value, TagClass tc) 
	{
		validateState();
		encoder = new TimpEncoder(tag, tc, Real32Encoder.create(value));
	}

	public void Real64(int tag, double value) 
	{
		validateState();
		encoder = new TimpEncoder(tag, Real64Encoder.create(value));
	}

	public void Real64(int tag, double value, TagClass tc) 
	{
		validateState();
		encoder = new TimpEncoder(tag, tc, Real64Encoder.create(value));
	}

	public void UTF8String(int tag, String value)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, UTF8StringEncoder.create(value));
	}

	public void UTF8String(int tag, String value, TagClass tc)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, tc, UTF8StringEncoder.create(value));
	}
	
	public void BMPString(int tag, String value)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, BMPStringEncoder.create(value));	
	}

	public void BMPString(int tag, String value, TagClass tc)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, tc, BMPStringEncoder.create(value));	
	}

	public void IA5String(int tag, String value)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, IA5StringEncoder.create(value));
	}

	public void IA5String(int tag, String value, TagClass tc)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, tc, IA5StringEncoder.create(value));
	}

	public void PrintableString(int tag, String value)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, PrintableStringEncoder.create(value));
	}		

	public void PrintableString(int tag, String value, TagClass tc)
	{
		validateStateAndValue(value);
		encoder = new TimpEncoder(tag, tc, PrintableStringEncoder.create(value));
	}		

	public void GndTime(int tag, java.util.GregorianCalendar value)
	{
		validateStateAndValue(value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		encoder = new TimpEncoder(tag, GndTimeGCEncoder.create(valueClone));
	}

	public void GndTime(int tag, java.util.GregorianCalendar value, TagClass tc)
	{
		validateStateAndValue(value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		encoder = new TimpEncoder(tag, tc, GndTimeGCEncoder.create(valueClone));
	}

	public void OctetString(int tag, byte[] buffer)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		
		encoder = new TimpEncoder(tag, new OctetStringEncoder(buffer, 0, buffer.length));
	}

	public void OctetString(int tag, byte[] buffer, TagClass tc)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		
		encoder = new TimpEncoder(tag, tc, new OctetStringEncoder(buffer, 0, buffer.length));
	}

	public void OctetString(int tag, byte[] buffer, int offset, int length)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");

		encoder = new TimpEncoder(tag, new OctetStringEncoder(buffer, offset, length));
	}

	public void OctetString(int tag, byte[] buffer, int offset, int length, TagClass tc)
	{
		validateState();
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		if(offset < 0)
			throw new IllegalArgumentException("The parameter 'offset' must not be negative.");
		if(length < 0)
			throw new IllegalArgumentException("The parameter 'length' must not be negative.");
		if(offset + length > buffer.length)
			throw new IllegalArgumentException("The limits specified by 'offset' and 'length' are outside the byte buffer.");

		encoder = new TimpEncoder(tag, tc, new OctetStringEncoder(buffer, offset, length));
	}

	public void OctetString(int tag, java.util.UUID value)
	{
		validateStateAndValue(value);
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		encoder = new TimpEncoder(tag,new OctetStringEncoder(nioBuffer.array(), 0, 16));
	}

	public void OctetString(int tag, java.util.UUID value, TagClass tc)
	{
		validateStateAndValue(value);
		java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(new byte[16]);
		nioBuffer.putLong(value.getMostSignificantBits());
		nioBuffer.putLong(value.getLeastSignificantBits());
		encoder = new TimpEncoder(tag, tc, new OctetStringEncoder(nioBuffer.array(), 0, 16));
	}

	private void validateState()
	{
		if(encoder != null)
			throw new IllegalStateException("The element to be explicitly tagged has already been specified.");
	}

	private void validateStateAndValue(Object value)
	{
		if(encoder != null)
			throw new IllegalStateException("The element to be explicitly tagged has already been specified.");
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");
	}
}
