package softnet.asn;

class LengthDecoder
{
    public static PairInt32 decode(byte[] buffer, int offset) throws FormatAsnException
    {
    	try
    	{    	
	        int L1_Byte = buffer[offset] & 0xFF;
	
	        if (L1_Byte <= 127)
	        {
	            return new PairInt32(L1_Byte, 1);
	        }
	
	        if (L1_Byte == 128)
	            throw new FormatAsnException("ASN1 codec does not support the indefinite length form.");
	
	        int L_Size = L1_Byte & 0x7F;
	        offset++;
	
	        if (L_Size == 1)
	        {
	            int length = buffer[offset] & 0xFF;
	            return new PairInt32(length, 2);
	        }
	        else if (L_Size == 2)
	        {
	            int b1 = buffer[offset] & 0xFF;
	            int b0 = buffer[offset + 1] & 0xFF;
	            int length = (b1 << 8) | b0;
	            return new PairInt32(length, 3);
	        }
	        else if (L_Size == 3)
	        {
	            int b2 = buffer[offset] & 0xFF;
	            int b1 = buffer[offset + 1] & 0xFF;
	            int b0 = buffer[offset + 2] & 0xFF;
	            int length = (b2 << 16) | (b1 << 8) | b0;
	            return new PairInt32(length, 4);
	        }
	        else if (L_Size == 4)
	        {
	            int b3 = buffer[offset] & 0xFF;
	            if (b3 >= 128)
	                throw new FormatAsnException("ASN1 codec does not support the length of content more than 2GB.");
	
	            int b2 = buffer[offset + 1] & 0xFF;
	            int b1 = buffer[offset + 2] & 0xFF;
	            int b0 = buffer[offset + 3] & 0xFF;
	            int length = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	            return new PairInt32(length, 5);
	        }
	
	        throw new FormatAsnException("ASN1 codec does not support the length of content more than 2GB.");
    	}
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new FormatAsnException("The size of the input buffer is not enough to contain all the Asn1 data.");
        }
    }
}
