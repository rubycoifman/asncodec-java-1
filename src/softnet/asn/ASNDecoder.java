package softnet.asn;

public class ASNDecoder 
{
    private ASNDecoder() { }

    public static SequenceDecoder Sequence(byte[] encoding) throws AsnException
    {
        return Sequence(encoding, 0);
    }    

    public static SequenceDecoder Sequence(byte[] encoding, int offset) throws AsnException
    {
        try
        {
            int T = encoding[offset];

            if ((T & C_Mask_Class) != C_Universal_Class)
                throw new TypeMismatchAsnException();

            if ((T & C_Constructed_Flag) == 0)
                throw new TypeMismatchAsnException();

            if ((T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new TypeMismatchAsnException();

            offset++;
            PairInt32 lengthPair = LengthDecoder.decode(encoding, offset);
            offset += lengthPair.second;

            if (offset + lengthPair.first > encoding.length)
                throw new FormatAsnException("The size of the input buffer is not enough to contain all the ASN.1 data.");

            return new SequenceDecoderImp(encoding, offset, lengthPair.first, false); 
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e)
        {
            throw new FormatAsnException("The size of the input buffer is not enough to contain all the ASN.1 data.");
        }
    }

	private static int C_Mask_Class = 0xC0;        
	private static int C_Mask_Tag = 0x1F;
	private static int C_Universal_Class = 0;
	private static int C_Constructed_Flag = 0x20;
}
