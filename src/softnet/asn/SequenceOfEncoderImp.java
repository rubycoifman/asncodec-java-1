package softnet.asn;

import java.util.ArrayList;
import java.util.GregorianCalendar;

class SequenceOfEncoderImp implements SequenceOfEncoder, ElementEncoder
{
	public SequenceOfEncoderImp(UType uType)
	{
		this.universalType = uType;
		childNodes = new ArrayList<ElementEncoder>();
	}
		
	private static byte C_Constructed_Flag = 0x20;
	private static byte C_Universal_Constructed_Sequence = (byte)(C_Constructed_Flag | UniversalTag.Sequence);
	
	private ArrayList<ElementEncoder> childNodes;
	private final UType universalType;	
	
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
		validateState(UType.Sequence);
		SequenceEncoderImp encoder = new SequenceEncoderImp();
		childNodes.add(encoder);
		return encoder;
	}
	
	public SequenceOfEncoder SequenceOf(UType uType)
	{		
		validateState(UType.Sequence);
		if(uType == null)
			throw new IllegalArgumentException("The parameter 'uType' must not be null.");
		SequenceOfEncoderImp encoder = new SequenceOfEncoderImp(uType);
		childNodes.add(encoder);
		return encoder;
	}

	public void Int32(int value)
	{
		validateState(UType.Integer);
		childNodes.add(new Int32Encoder(value));
	}
	
	public void Int64(long value)
	{
		validateState(UType.Integer);
		childNodes.add(new Int64Encoder(value));
	}
	
	public void Boolean(boolean value)
	{
		validateState(UType.Boolean);
		childNodes.add(new BooleanEncoder(value));
	}

	public void Real32(float value) 
	{
		validateState(UType.Real);
		childNodes.add(Real32Encoder.create(value));
	}

	public void Real64(double value) 
	{
		validateState(UType.Real);
		childNodes.add(Real64Encoder.create(value));
	}

	public void UTF8String(String value)
	{
		validateStateAndValue(UType.UTF8String, value);
		childNodes.add(UTF8StringEncoder.create(value));
	}
	
	public void BMPString(String value)
	{
		validateStateAndValue(UType.BMPString, value);
		childNodes.add(BMPStringEncoder.create(value));		
	}

	public void IA5String(String value)
	{
		validateStateAndValue(UType.IA5String, value);
		childNodes.add(IA5StringEncoder.create(value));	
	}
	
	public void PrintableString(String value)
	{
		validateStateAndValue(UType.PrintableString, value);
		childNodes.add(PrintableStringEncoder.create(value));	
	}

	public void GndTime(java.util.GregorianCalendar value) 
	{
		validateStateAndValue(UType.GeneralizedTime, value);
		GregorianCalendar valueClone = (GregorianCalendar)value.clone();
		GndTimeGCEncoder encoder = GndTimeGCEncoder.create(valueClone);
		childNodes.add(encoder);
	}

	public void OctetString(byte[] buffer)
	{
		validateState(UType.OctetString);
		if(buffer == null)
			throw new IllegalArgumentException("The parameter 'buffer' must not be null.");
		childNodes.add(new OctetStringEncoder(buffer, 0, buffer.length));
	}
	
	public void OctetString(byte[] buffer, int offset, int length)
	{
		validateState(UType.OctetString);
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
	
	public void Null() {
		childNodes.add(new NullEncoder());
	}
	
	private void validateState(UType uType)
	{
		if(universalType != uType)
			throw new IllegalStateException(String.format("Only '%s' type elements are allowed in the 'SequenceOf(%s)' container.", universalType, universalType));		
	}

	private void validateStateAndValue(UType uType, Object value)
	{
		if(universalType != uType)
			throw new IllegalStateException(String.format("Only '%s' type elements are allowed in the 'SequenceOf(%s)' container.", universalType, universalType));
		if(value == null)
			throw new IllegalArgumentException("The parameter 'value' must not be null.");		
	}
}
