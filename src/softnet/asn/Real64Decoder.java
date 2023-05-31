package softnet.asn;

class Real64Decoder
{
	public static double decode(byte[] buffer, int offset, int V_length, boolean checkForUnderflow) throws FormatAsnException, UnderflowAsnException, OverflowAsnException
	{
        if (V_length == 0)
            return 0.0;

        int headerByte = buffer[offset];
        offset++;

        if ((headerByte & 0xC0) == 0x40)
        {
        	if(V_length != 1)
        		throw new FormatAsnException();
        	
        	if(headerByte == 0x40)
        		return Double.POSITIVE_INFINITY;

        	if(headerByte == 0x41)
        		return Double.NEGATIVE_INFINITY;
        	
        	if(headerByte == 0x42)
        		return Double.NaN;

        	if(headerByte == 0x43)
        		return -0.0;

    		throw new FormatAsnException();
        }
        
        if ((headerByte & 0x80) == 0)
            throw new FormatAsnException("The ASN1 codec only supports base 2 binary format for encoding floating point numbers.");

        if ((headerByte & 0x30) != 0)
            throw new FormatAsnException("The ASN1 codec only supports base 2 binary format for encoding floating point numbers.");

        if ((headerByte & 0x0C) != 0)  // scaling factor must be zero for base 2 binary format
            throw new FormatAsnException();
        
        long exponent;
        int exponent_flags = headerByte & 0x03;
        int exponent_bytes_number;

        if (exponent_flags == 0)
        {
            if (V_length < 3)
                throw new FormatAsnException();

            exponent = buffer[offset];
            offset++;
            exponent_bytes_number = 1;
        }
        else if (exponent_flags == 1)
        {
            if (V_length < 4)
                throw new FormatAsnException();

            int byte_a = buffer[offset] & 0xFF;
            int byte_b = buffer[offset + 1] & 0xFF;
            offset += 2;
            exponent_bytes_number = 2;

            if (byte_a >= 128)
            {
                if (byte_a == 255 && byte_b >= 128)
                    throw new FormatAsnException();

                exponent = /* 0xFFFF0000 */ -65536 | (byte_a << 8) | byte_b;
            }
            else
            {
                if (byte_a == 0 && byte_b <= 127)
                    throw new FormatAsnException();

                exponent = (byte_a << 8) | byte_b;
            }
        }
        else if (exponent_flags == 2)
        {
            if (V_length < 5)
                throw new FormatAsnException();

            int byte_a = buffer[offset] & 0xFF;
            if (byte_a >= 128)
            {
                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");
                return 0.0;
            }
            else
                throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");
        }
        else // exponent_flags == 3
        {
            if (V_length < 7)
                throw new FormatAsnException();

            int byte_a = buffer[offset + 1] & 0xFF;
            if (byte_a >= 128)
            {
                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");
                return 0.0;
            }
            else
                throw new OverflowAsnException("The value of the input real is outside of the scope of 64-bit IEEE-754 real.");
        }

        int mantissa_bytes_number = V_length - (1 + exponent_bytes_number);

        int lsb_index = offset + mantissa_bytes_number - 1;
        if ((buffer[lsb_index] & 0x01) == 0)
            throw new FormatAsnException();

        int ms_byte = buffer[offset] & 0xFF;
        if (ms_byte == 0)
            throw new FormatAsnException();
        
        int ms_bits_number = 0;
        if (ms_byte >= 128) ms_bits_number = 8;
        else if (ms_byte >= 64) ms_bits_number = 7;
        else if (ms_byte >= 32) ms_bits_number = 6;
        else if (ms_byte >= 16) ms_bits_number = 5;
        else if (ms_byte >= 8) ms_bits_number = 4;
        else if (ms_byte >= 4) ms_bits_number = 3;
        else if (ms_byte >= 2) ms_bits_number = 2;
        else ms_bits_number = 1;

        int mantissa_width = ms_bits_number + (mantissa_bytes_number - 1) * 8;        
        long mantissa = 0;
        
        if (mantissa_width <= 53)
        {
	        for (int i = lsb_index, j = 0; i >= offset; i--, j++)
	            mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
        }
        else if(mantissa_width <= 56)
        {
            if (checkForUnderflow)
                throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

	        for (int i = lsb_index, j = 0; i >= offset; i--, j++)
	            mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
	        mantissa = mantissa >> (mantissa_width - 53);
        }
        else // mantissa_width > 56 && mantissa_bytes_number >= 8  ==>  pick out the most significant 53 bits
        {
            if (checkForUnderflow)
                throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");

	        for (int i = offset + 7, j = 0; i >= offset; i--, j++)
	            mantissa = mantissa | ((buffer[i] & 0xFFL) << (8 * j));
	        mantissa = mantissa >> (ms_bits_number + 3);
        }
        
        exponent = exponent + mantissa_width + 1022; // exponent + (mantissa_width - 1) + 1023
        
        if (exponent >= 1) 
        {
            if (exponent > 2046)
                throw new OverflowAsnException("The value of an input real is outside of the scope of 64-bit IEEE-754 real.");

            long doubleBits = exponent << 52;

            if ((headerByte & 0x40) != 0) // check for sign
                doubleBits = doubleBits | -9223372036854775808L; /* 0x8000000000000000 */

            if(mantissa_width < 53)
            	mantissa = (mantissa << (53 - mantissa_width)) & 0x000FFFFFFFFFFFFFL;
            else
            	mantissa = mantissa & 0x000FFFFFFFFFFFFFL;           	

            doubleBits = doubleBits | mantissa;
            
            return Double.longBitsToDouble(doubleBits); 
        }
        else // exponent <= 0 
        {
        	long doubleBits = 0;

            if ((headerByte & 0x40) != 0) // check for sign
                doubleBits = doubleBits | -9223372036854775808L; /* 0x8000000000000000 */
            
            if(mantissa_width < 53)
            {
            	int shift = 52 - mantissa_width + (int)exponent;
            	if(shift > 0)
            		mantissa = mantissa << shift;
            	else if (shift < 0)
            	{
                    if (checkForUnderflow)
                        throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");
            		mantissa = mantissa >> shift;
            	}
            }
            else // mantissa_width >= 53
            {
                if (checkForUnderflow)
                    throw new UnderflowAsnException("The precision of the input real is outside of the scope of 64-bit IEEE-754 real.");
            	mantissa = mantissa >> (-(int)exponent + 1);  
            }
            
            doubleBits = doubleBits | mantissa;
            
            return Double.longBitsToDouble(doubleBits); 
        }        
	}
}
