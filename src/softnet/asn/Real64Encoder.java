package softnet.asn;

class Real64Encoder implements ElementEncoder
{
	private byte[] V_bytes;
	private int V_length;
	
    private Real64Encoder()
    {
    	V_bytes = new byte[10];
    	V_length = 0;
    }

    public static Real64Encoder create(double value)
    {
    	Real64Encoder encoder = new Real64Encoder();
        encoder.encodeV(value);
        return encoder;
    }

    public boolean isConstructed() {
    	return false;
    }

    public int estimateSize() {
    	return 2 + V_length;
    }

    public int encodeTLV(BinaryStack binStack)
    {
    	if(V_length > 0)
    		binStack.stack(V_bytes, 0, V_length);
    	binStack.stack((byte)V_length);    	
        binStack.stack(UniversalTag.Real);
        return 2 + V_length;
    }

    public int encodeLV(BinaryStack binStack)
    {
    	if(V_length > 0)
    		binStack.stack(V_bytes, 0, V_length);
    	binStack.stack((byte)V_length);    	
        return 1 + V_length;
    }

    private void encodeV(double value)
    {
    	long doubleBits = Double.doubleToLongBits(value);
    	    	
        int exp_byte_a = ((int)(doubleBits >> 56)) & 0x0000007F;
        int exp_byte_b = ((int)(doubleBits >> 48)) & 0x000000F0;
        int exponent = ((exp_byte_a << 4) | (exp_byte_b >> 4));
    	
        if (exponent >= 1) // normal double / +infinity / -infinity / NaN
        {
        	if(exponent < 2047) // normal double
        	{
        		exponent -= 1023;
        		long mantissa = (doubleBits & 0x000FFFFFFFFFFFFFL) | 0x0010000000000000L;
        		
        		int insignificant_bits_number = countInsignificantBits(mantissa);
       			exponent -= (52 - insignificant_bits_number);
       			mantissa = mantissa >> insignificant_bits_number;
        	
        		encodeExponent(exponent);
        		encodeHeader(doubleBits);
        		encodeMantissa(mantissa);
        	}
        	else // exponent == 2047 /// +infinity / -infinity / NaN
        	{
                long mantissa = doubleBits & 0x000FFFFFFFFFFFFFL;
        		if(mantissa == 0) // +infinity / -infinity
        		{
        			if(doubleBits >= 0L) //  +infinity
        			{
        				V_bytes[0] = 0x40; 
        				V_length = 1;
        			}
        			else // -infinity
        			{
        				V_bytes[0] = 0x41; 
        				V_length = 1;        				
        			}
        		}
        		else // NaN
        		{
    				V_bytes[0] = 0x42; 
    				V_length = 1;        			
        		}
        	}
        }
        else // exponent == 0 /// subnormal double / +0 / -0
        {
            long mantissa = doubleBits & 0x000FFFFFFFFFFFFFL;
            if(mantissa > 0) // subnormal double
            {
                exponent = -1022;
                int insignificant_bits_number = countInsignificantBits(mantissa);
       			exponent -= (52 - insignificant_bits_number);
       			mantissa = mantissa >> insignificant_bits_number;
        	
        		encodeExponent(exponent);
        		encodeHeader(doubleBits);
        		encodeMantissa(mantissa);        		
            }
            else // +0 / -0
            {
            	if(doubleBits >= 0L) // +0 
            	{
            		V_length = 0;
            	}
            	else // -0
            	{
            		V_bytes[0] = 0x43; 
            		V_length = 1;
            	}
            }
        }
    }    
    
    private void encodeMantissa(long mantissa)
    {
        byte b0 =  (byte)(mantissa & 0x00000000000000ffL);
        byte b1 = (byte)((mantissa & 0x000000000000ff00L) >> 8);
        byte b2 = (byte)((mantissa & 0x0000000000ff0000L) >> 16);
        byte b3 = (byte)((mantissa & 0x00000000ff000000L) >> 24);
        byte b4 = (byte)((mantissa & 0x000000ff00000000L) >> 32);
        byte b5 = (byte)((mantissa & 0x0000ff0000000000L) >> 40);
        byte b6 = (byte)((mantissa & 0x001f000000000000L) >> 48);

        if (b6 == 0)
        {
            if (b5 == 0)
            {
                if (b4 == 0)
                {
                    if (b3 == 0)
                    {
                        if (b2 == 0)
                        {
                            if (b1 == 0)
                            {
                               	V_bytes[V_length] = (byte)b0;
                                V_length += 1;
                            }
                            else
                            {
                               	V_bytes[V_length] = (byte)b1;
                               	V_bytes[V_length + 1] = (byte)b0;
                                V_length += 2;
                            }
                        }
                        else
                        {
                           	V_bytes[V_length] = (byte)b2;
                           	V_bytes[V_length + 1] = (byte)b1;
                           	V_bytes[V_length + 2] = (byte)b0;
                            V_length += 3;
                        }
                    }
                    else
                    {
                       	V_bytes[V_length] = (byte)b3;
                       	V_bytes[V_length + 1] = (byte)b2;
                       	V_bytes[V_length + 2] = (byte)b1;
                       	V_bytes[V_length + 3] = (byte)b0;
                        V_length += 4;
                    }
                }
                else
                {
                   	V_bytes[V_length] = (byte)b4;
                   	V_bytes[V_length + 1] = (byte)b3;
                   	V_bytes[V_length + 2] = (byte)b2;
                   	V_bytes[V_length + 3] = (byte)b1;
                   	V_bytes[V_length + 4] = (byte)b0;
                    V_length += 5;
                }
            }
            else
            {
               	V_bytes[V_length] = (byte)b5;
               	V_bytes[V_length + 1] = (byte)b4;
               	V_bytes[V_length + 2] = (byte)b3;
               	V_bytes[V_length + 3] = (byte)b2;
               	V_bytes[V_length + 4] = (byte)b1;
               	V_bytes[V_length + 5] = (byte)b0;
                V_length += 6;
            }
        }
        else
        {
           	V_bytes[V_length] = (byte)b6;
           	V_bytes[V_length + 1] = (byte)b5;
           	V_bytes[V_length + 2] = (byte)b4;
           	V_bytes[V_length + 3] = (byte)b3;
           	V_bytes[V_length + 4] = (byte)b2;
           	V_bytes[V_length + 5] = (byte)b1;
           	V_bytes[V_length + 6] = (byte)b0;
            V_length += 7;
        }        
    }
    
    private void encodeExponent(int exponent)
    {
        int b0 = exponent & 0x000000FF;
        int b1 = (exponent & 0x0000FF00) >> 8;
        
        if(b1 == 255)
        {
        	if(b0 >= 128)
        	{
        		V_bytes[1] = (byte)(b0 - 256);
        		V_length = 1;
        	}
        	else
        	{
        		V_bytes[1] = (byte)(-1);
        		V_bytes[2] = (byte)b0;
        		V_length = 2;
        	}
        }
        else if (b1 == 0)
        {
        	if (b0 <= 127)
            {
        		V_bytes[1] = (byte)b0;
        		V_length = 1;
            }
            else
            {
        		V_bytes[1] = (byte)0;
        		V_bytes[2] = (byte)(b0 - 256);
        		V_length = 2;
            }
        }
        else if (b1 >= 128)
        {
    		V_bytes[1] = (byte)(b1 - 256);
        	if(b0 >= 128)
        		V_bytes[2] = (byte)(b0 - 256);
        	else
        		V_bytes[2] = (byte)b0;
    		V_length = 2;
        }
        else // b1 <= 127
        {
    		V_bytes[1] = (byte)b1;
        	if(b0 >= 128)
        		V_bytes[2] = (byte)(b0 - 256);
        	else
        		V_bytes[2] = (byte)b0;
    		V_length = 2;
        }
    }
    
    private void encodeHeader(long doubleBits)
    {
        if (doubleBits >= 0L) // positive value
        {
            if (V_length == 1)
            	V_bytes[0] = (byte)0x80;                
            else
            	V_bytes[0] = (byte)0x81;
        }
        else // negative value
        {
            if (V_length == 1)
            	V_bytes[0] = (byte)0xC0;
            else
            	V_bytes[0] = (byte)0xC1;
        }
        V_length++;
    }
    
    private int countInsignificantBits(long mantissa)
    {
    	long masked_mantissa = mantissa & 0x001FFFFFFC000000L;
    	if(masked_mantissa == mantissa) 
    	{
    		masked_mantissa = mantissa & 0x001FFF8000000000L;
    		if(masked_mantissa == mantissa) 
    		{
        		masked_mantissa = mantissa & 0x001FC00000000000L;
        		if(masked_mantissa == mantissa) 
        		{
            		masked_mantissa = mantissa & 0x001E000000000000L;
            		if(masked_mantissa == mantissa) 
            		{
                		masked_mantissa = mantissa & 0x001C000000000000L;
                		if(masked_mantissa != mantissa)
                			return 49;
                		
                		masked_mantissa = mantissa & 0x0018000000000000L;
                		if(masked_mantissa != mantissa)
                			return 50;
                			
                		masked_mantissa = mantissa & 0x0010000000000000L;
                		if(masked_mantissa != mantissa)
                			return 51;
                		
                		return 52;
            		}
            		else 
            		{
            			masked_mantissa = mantissa & 0x001F800000000000L;
            			if(masked_mantissa != mantissa)
                			return 46;

            			masked_mantissa = mantissa & 0x001F000000000000L;
            			if(masked_mantissa != mantissa)
                			return 47;
            			
            			return 48;
            		}
        		}   
        		else
        		{
        			masked_mantissa = mantissa & 0x001FF80000000000L;
        			if(masked_mantissa == mantissa) 
            		{
                		masked_mantissa = mantissa & 0x001FF00000000000L;
                		if(masked_mantissa != mantissa)
                			return 43;

                		masked_mantissa = mantissa & 0x001FE00000000000L;
                		if(masked_mantissa != mantissa)
                			return 44;
                		
                		return 45;
            		}
        			else
        			{
        				masked_mantissa = mantissa & 0x001FFF0000000000L;
                		if(masked_mantissa != mantissa)
                			return 39;

        				masked_mantissa = mantissa & 0x001FFE0000000000L;
                		if(masked_mantissa != mantissa)
                			return 40;

        				masked_mantissa = mantissa & 0x001FFC0000000000L;
                		if(masked_mantissa != mantissa)
                			return 41;

            			return 42;
        			}
        		}
    		}
    		else    			
    		{
    			masked_mantissa = mantissa & 0x001FFFFE00000000L;
    			if(masked_mantissa == mantissa) 
        		{
    				masked_mantissa = mantissa & 0x001FFFF000000000L;
        			if(masked_mantissa == mantissa) 
        			{
        				masked_mantissa = mantissa & 0x001FFFE000000000L;
                		if(masked_mantissa != mantissa)
                			return 36;

        				masked_mantissa = mantissa & 0x001FFFC000000000L;
                		if(masked_mantissa != mantissa)
                			return 37;
                		
                		return 38;
        			}
        			else
        			{
        				masked_mantissa = mantissa & 0x001FFFFC00000000L;
                		if(masked_mantissa != mantissa)
                			return 33;

        				masked_mantissa = mantissa & 0x001FFFF800000000L;
                		if(masked_mantissa != mantissa)
                			return 34;

                		return 35;
        			}
        		}
    			else
    			{
    				masked_mantissa = mantissa & 0x001FFFFFC0000000L;
    				if(masked_mantissa == mantissa) 
    				{
    					masked_mantissa = mantissa & 0x001FFFFF80000000L;
                		if(masked_mantissa != mantissa)
                			return 30;
                		
    					masked_mantissa = mantissa & 0x001FFFFF00000000L;
                		if(masked_mantissa != mantissa)
                			return 31;

            			return 32;
    				}
    				else
    				{
    					masked_mantissa = mantissa & 0x001FFFFFF8000000L;
                		if(masked_mantissa != mantissa)
                			return 26;
                		
                		masked_mantissa = mantissa & 0x001FFFFFF0000000L;
                		if(masked_mantissa != mantissa)
                			return 27;

                		masked_mantissa = mantissa & 0x001FFFFFE0000000L;
                		if(masked_mantissa != mantissa)
                			return 28;

            			return 29;
    				}
    			}
    		}
    	}
    	else
    	{
    		masked_mantissa = mantissa & 0x001FFFFFFFFFE000L;
    		if(masked_mantissa == mantissa)
    		{
    			masked_mantissa = mantissa & 0x001FFFFFFFF00000L;
        		if(masked_mantissa == mantissa) 
        		{
        			masked_mantissa = mantissa & 0x001FFFFFFF800000L;
            		if(masked_mantissa == mantissa) 
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFF000000L;
                		if(masked_mantissa != mantissa)
                			return 23;

            			masked_mantissa = mantissa & 0x001FFFFFFE000000L;
                		if(masked_mantissa != mantissa)
                			return 24;

            			return 25;                		
            		}
            		else
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFFE00000L;
                		if(masked_mantissa != mantissa)
                			return 20;
            			
            			masked_mantissa = mantissa & 0x001FFFFFFFC00000L;
                		if(masked_mantissa != mantissa)
                			return 21;

                		return 22;
            		}
        		}
        		else
        		{
        			masked_mantissa = mantissa & 0x001FFFFFFFFE0000L;
        			if(masked_mantissa == mantissa) 
        			{
            			masked_mantissa = mantissa & 0x001FFFFFFFFC0000L;
                		if(masked_mantissa != mantissa)
                			return 17;
        				
            			masked_mantissa = mantissa & 0x001FFFFFFFF80000L;
                		if(masked_mantissa != mantissa)
                			return 18;

            			return 19;
        			}
        			else
        			{
            			masked_mantissa = mantissa & 0x001FFFFFFFFFC000L;
                		if(masked_mantissa != mantissa)
                			return 13;

            			masked_mantissa = mantissa & 0x001FFFFFFFFF8000L;
                		if(masked_mantissa != mantissa)
                			return 14;

            			masked_mantissa = mantissa & 0x001FFFFFFFFF0000L;
                		if(masked_mantissa != mantissa)
                			return 15;
                		
            			return 16;
        			}
        		}
    		}
    		else
    		{
    			masked_mantissa = mantissa & 0x001FFFFFFFFFFF80L;
        		if(masked_mantissa == mantissa)
        		{
        			masked_mantissa = mantissa & 0x001FFFFFFFFFFC00L;
            		if(masked_mantissa == mantissa) 
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFFFFF800L;
                		if(masked_mantissa != mantissa)
                			return 10;

            			masked_mantissa = mantissa & 0x001FFFFFFFFFF000L;
                		if(masked_mantissa != mantissa)
                			return 11;
                		
                		return 12;
            		}
            		else
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFFFFFF00L;
                		if(masked_mantissa != mantissa)
                			return 7;

            			masked_mantissa = mantissa & 0x001FFFFFFFFFFE00L;
                		if(masked_mantissa != mantissa)
                			return 8;

            			return 9;
            		}
        		}
        		else
        		{
        			masked_mantissa = mantissa & 0x001FFFFFFFFFFFF0L;
            		if(masked_mantissa == mantissa)
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFFFFFFE0L;
                		if(masked_mantissa != mantissa)
                			return 4;

            			masked_mantissa = mantissa & 0x001FFFFFFFFFFFC0L;
                		if(masked_mantissa != mantissa)
                			return 5;
                		
            			return 6;
            		}
            		else
            		{
            			masked_mantissa = mantissa & 0x001FFFFFFFFFFFFEL;
                		if(masked_mantissa != mantissa)
                			return 0;

                		masked_mantissa = mantissa & 0x001FFFFFFFFFFFFCL;
                		if(masked_mantissa != mantissa)
                			return 1;

                		masked_mantissa = mantissa & 0x001FFFFFFFFFFFF8L;
                		if(masked_mantissa != mantissa)
                			return 2;

            			return 3;
            		}
        		}
    		}
    	}
    }
}












