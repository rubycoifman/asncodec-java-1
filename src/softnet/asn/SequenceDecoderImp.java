package softnet.asn;

import java.util.GregorianCalendar;
import java.util.UUID;

class SequenceDecoderImp implements SequenceDecoder
{
	private static int C_Mask_Class = 0xC0;
	private static int C_Mask_Tag = 0x1F;
	private static int C_Universal_Class = 0;	
	private static int C_ContextSpecific_Class = 0x80;
	private static int C_Application_Class = 0x40;
	private static int C_Private_Class = 0xC0;
	private static int C_Constructed_Flag = 0x20;

    private byte[] m_buffer = null;        
    private int m_offset = 0;
    private int data_begin = 0;
    private int data_end = 0;
    private boolean _validateClass; 
        
    public SequenceDecoderImp(byte[] buffer, int offset, int length, boolean validateClass)
    {
    	m_buffer = buffer;
        m_offset = offset;
        data_begin = offset;
        data_end = offset + length;
        this._validateClass = validateClass;
    }
        
	public void validateClass(boolean flag) {
		_validateClass = flag;	
	}

    private int m_count = -1;
    public int count() throws FormatAsnException
    {
        if (m_count == -1)
        {
            m_count = 0;
            int offset = data_begin;
            while (offset < data_end)
            { 
                PairInt32  lengthPair = LengthDecoder.decode(m_buffer, offset + 1);
                offset += 1 + lengthPair.second + lengthPair.first;
                m_count++;
            }

            if(offset != data_end)
                throw new FormatAsnException();
        }
        return m_count;
    }
    
	public boolean hasNext()
	{
		if (m_offset < data_end)
    		return true;
		return false;
	}
    
	public boolean exists(UType type)
	{
        if (m_offset == data_end)
    		return false;

        int T = m_buffer[m_offset];
        if ((T & C_Mask_Class) != C_Universal_Class)
    		return false;
    	
    	if((T & C_Mask_Tag) == type.tag)
    		return true;    	
    	return false;
	}

	public boolean exists(int tag)
	{
        if (m_offset == data_end)
    		return false;

        int T = m_buffer[m_offset];
        if ((T & C_Mask_Class) == C_Universal_Class)
    		return false;
    	
    	if((T & C_Mask_Tag) == tag)
    		return true;    	
    	return false;		
	}
	
	public boolean isUniversal() throws EndOfContainerAsnException
	{
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if (tagClass == C_Universal_Class)
        	return true;
        
        return false;
	}
	
	public boolean isClass(TagClass tc) throws EndOfContainerAsnException
	{
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if (tc == TagClass.ContextSpecific && tagClass == C_ContextSpecific_Class)
        	return true;

        if (tc == TagClass.Application && tagClass == C_Application_Class)
        	return true;

        if (tc == TagClass.Private && tagClass == C_Private_Class)
        	return true;

        return false;
	}
	
	public boolean isNull() throws EndOfContainerAsnException
	{
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();        

        int T = m_buffer[m_offset];        

        if ((T & C_Mask_Class) != C_Universal_Class) 
        	return false;

        if((T & C_Constructed_Flag) != 0)
            return false;
        
	    if((T & C_Mask_Tag) == UniversalTag.Null)
	    	return true;
        
    	return false;
	}
		
	public void skip() throws FormatAsnException, EndOfContainerAsnException
	{
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();        
    	int V_Length = decodeLength();
    	m_offset += V_Length;
	}
	
    public void end() throws EndNotReachedAsnException
    {
    	if (m_offset < data_end)
            throw new EndNotReachedAsnException();
    }
	
	public TexpDecoder Texp() throws FormatAsnException, TypeMismatchAsnException, EndOfContainerAsnException 
	{
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if (tagClass == C_Universal_Class)
        	throw new TypeMismatchAsnException(); 
     
        if(_validateClass && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) == 0) 
            throw new TypeMismatchAsnException();
        
        TagClass tc = TagClass.ContextSpecific;
        if(tagClass == C_Application_Class)
        	tc = TagClass.Application;
        else if(tagClass == C_Private_Class)
        	tc = TagClass.Private;
        
        int tag = T & C_Mask_Tag;     
        
        int V_Length = decodeLength();     
        if (V_Length < 2)
            throw new FormatAsnException();
        
        TexpDecoderImp decoder = new TexpDecoderImp(tag, tc, _validateClass, m_buffer, m_offset, V_Length);
        m_offset += V_Length;
        
        return decoder;
	}
	
	public TexpDecoder Texp(TagClass tc) throws FormatAsnException, TypeMismatchAsnException, EndOfContainerAsnException 
	{
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if (tagClass == C_Universal_Class)
        	throw new TypeMismatchAsnException(); 
             
        if ((T & C_Constructed_Flag) == 0) 
            throw new TypeMismatchAsnException();
        
        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();
        
        int tag = T & C_Mask_Tag;     
        
        int V_Length = decodeLength();       
        if (V_Length < 2)
            throw new FormatAsnException();

        TexpDecoderImp decoder = new TexpDecoderImp(tag, tc, _validateClass, m_buffer, m_offset, V_Length);        
        m_offset += V_Length;

        return decoder;
	}
	
    public SequenceDecoder Sequence() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if ((T & C_Constructed_Flag) == 0)
            throw new TypeMismatchAsnException();
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();
        
        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            SequenceDecoderImp decoder = new SequenceDecoderImp(m_buffer, m_offset, V_Length, _validateClass);
            m_offset += V_Length;

            return decoder;
        }
        else
        {
            int V_Length = decodeLength();
            SequenceDecoderImp decoder = new SequenceDecoderImp(m_buffer, m_offset, V_Length, _validateClass);
            m_offset += V_Length;

            return decoder;
        }
    }

    public SequenceDecoder Sequence(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
    	if(tc == TagClass.Application && tagClass != C_Application_Class)
    		throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();
        
        if ((T & C_Constructed_Flag) == 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        SequenceDecoderImp decoder = new SequenceDecoderImp(m_buffer, m_offset, V_Length, _validateClass);
        m_offset += V_Length;

        return decoder;
    }

    public SequenceOfDecoder SequenceOf(UType uType) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;

        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) == 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Sequence)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            SequenceOfDecoderImp decoder = new SequenceOfDecoderImp(uType, m_buffer, m_offset, V_Length, _validateClass);
            m_offset += V_Length;

            return decoder;
        }
        else
        {
            int V_Length = decodeLength();
            SequenceOfDecoderImp decoder = new SequenceOfDecoderImp(uType, m_buffer, m_offset, V_Length, _validateClass);
            m_offset += V_Length;

            return decoder;
        }
    }

    public SequenceOfDecoder SequenceOf(UType uType, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();	        

        if ((T & C_Constructed_Flag) == 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        SequenceOfDecoderImp decoder = new SequenceOfDecoderImp(uType, m_buffer, m_offset, V_Length, _validateClass);
        m_offset += V_Length;

        return decoder;
    }

    public int Int32() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            int value = Int32Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();            
            int value = Int32Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public int Int32(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();            
        int value = Int32Decoder.decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public int Int32(int minValue, int maxValue) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException, ConstraintAsnException
    {
    	int value = Int32();
        if (value < minValue || maxValue < value)
            throw new ConstraintAsnException(String.format("The value of the input integer must be in the range [%d, %d], while the actual value is %d.", minValue, maxValue, value));
        return value;
    }

    public int Int32(int minValue, int maxValue, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException, ConstraintAsnException
    {
    	int value = Int32(tc);
        if (value < minValue || maxValue < value)
            throw new ConstraintAsnException(String.format("The value of the input integer must be in the range [%d, %d], while the actual value is %d.", minValue, maxValue, value));
        return value;
    }

    public long Int64() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;

        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Integer)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            long value = Int64Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            long value = Int64Decoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public long Int64(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        long value = Int64Decoder.decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public boolean Boolean() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;

        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Boolean)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            if(V_Length != 1)
            	throw new FormatAsnException();
            
            int value = m_buffer[m_offset] & 0xFF;
            m_offset++;
                        
            if (value == 255) {
                return true;
            }
            else if (value == 0) {
                return false;
            }
            
            throw new FormatAsnException();
        }
        else
        {
            int V_Length = decodeLength();
            if (V_Length != 1)
                throw new FormatAsnException();

            int value = m_buffer[m_offset] & 0xFF;
            m_offset++;
                        
            if (value == 255) {
                return true;
            }
            else if (value == 0) {
                return false;
            }
            
            throw new FormatAsnException();
        }
    }

    public boolean Boolean(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        if (V_Length != 1)
            throw new FormatAsnException();

        int value = m_buffer[m_offset] & 0xFF;
        m_offset++;
                    
        if (value == 255) {
            return true;
        }
        else if (value == 0) {
            return false;
        }
        
        throw new FormatAsnException();
    }

    public float Real32() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real32(false);
    }

    public float Real32(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real32(false, tc);
    }

    public float Real32(boolean checkForUnderflow) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            float value = Real32Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            float value = Real32Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
    }

    public float Real32(boolean checkForUnderflow, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        float value = Real32Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
        m_offset += V_Length;

        return value;
    }

    public double Real64() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real64(false);
    }

    public double Real64(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
    	return Real64(false, tc);
    }

    public double Real64(boolean checkForUnderflow) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.Real)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            double value = Real64Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            double value = Real64Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
            m_offset += V_Length;

            return value;
        }
    }

    public double Real64(boolean checkForUnderflow, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, UnderflowAsnException, OverflowAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        double value = Real64Decoder.decode(m_buffer, m_offset, V_Length, checkForUnderflow);
        m_offset += V_Length;

        return value;
    }

    public String UTF8String() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;

        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.UTF8String)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            String value = UTF8StringDecoder.Decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            String value = UTF8StringDecoder.Decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public String UTF8String(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        String value = UTF8StringDecoder.Decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public String UTF8String(int requiredLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = UTF8String();
        if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String UTF8String(int requiredLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = UTF8String(tc);
        if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String UTF8String(int minLength, int maxLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = UTF8String();
        if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }

    public String UTF8String(int minLength, int maxLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = UTF8String(tc);
        if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }

    public String BMPString() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.BMPString)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            String value = BMPStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            String value = BMPStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public String BMPString(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();
        
        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        String value = BMPStringDecoder.decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public String BMPString(int requiredLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = BMPString();
        if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String BMPString(int requiredLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = BMPString(tc);
        if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String BMPString(int minLength, int maxLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = BMPString();
        if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }

    public String BMPString(int minLength, int maxLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = BMPString(tc);
        if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;   
    }

    public String IA5String() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.IA5String)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            String value = IA5StringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            String value = IA5StringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public String IA5String(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        String value = IA5StringDecoder.decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public String IA5String(int requiredLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = IA5String();
    	if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String IA5String(int requiredLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = IA5String(tc);
    	if (value.length() != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input string must be %d, while the actual length is %d.", requiredLength, value.length()));
        return value;   
    }

    public String IA5String(int minLength, int maxLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = IA5String();
    	if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;    
    }

    public String IA5String(int minLength, int maxLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	String value = IA5String(tc);
    	if (value.length() < minLength || maxLength < value.length())
            throw new ConstraintAsnException(String.format("The length of the input string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length()));
        return value;    
    }

    public String PrintableString() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.PrintableString)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            String value = PrintableStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            String value = PrintableStringDecoder.decode(m_buffer, m_offset, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public String PrintableString(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        String value = PrintableStringDecoder.decode(m_buffer, m_offset, V_Length);
        m_offset += V_Length;

        return value;
    }

    public GregorianCalendar GndTimeToGC() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.GeneralizedTime)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();
            GregorianCalendar value = GndTimeGCDecoder.decode(m_buffer, m_offset, V_Length); 
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();
            GregorianCalendar value = GndTimeGCDecoder.decode(m_buffer, m_offset, V_Length); 
            m_offset += V_Length;

            return value;
        }
    }

    public GregorianCalendar GndTimeToGC(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
        if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();
        GregorianCalendar value = GndTimeGCDecoder.decode(m_buffer, m_offset, V_Length); 
        m_offset += V_Length;

        return value;
    }

    public byte[] OctetString() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(_validateClass && tagClass != C_Universal_Class)
            throw new TypeMismatchAsnException();

        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        if (tagClass == C_Universal_Class)
        {
            if ((T & C_Mask_Tag) != UniversalTag.OctetString)
                throw new TypeMismatchAsnException();

            int V_Length = decodeLength();

            byte[] value = new byte[V_Length];
            System.arraycopy(m_buffer, m_offset, value, 0, V_Length);
            m_offset += V_Length;

            return value;
        }
        else
        {
            int V_Length = decodeLength();

            byte[] value = new byte[V_Length];
            System.arraycopy(m_buffer, m_offset, value, 0, V_Length);
            m_offset += V_Length;

            return value;
        }
    }

    public byte[] OctetString(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException
    {
    	if (m_offset == data_end)
            throw new EndOfContainerAsnException();

    	if(tc == null)
    		throwExceptionOnTagClassNull();
    	
        int T = m_buffer[m_offset];
        int tagClass = T & C_Mask_Class;
        
        if(tc == TagClass.Application && tagClass != C_Application_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.ContextSpecific && tagClass != C_ContextSpecific_Class)
            throw new TypeMismatchAsnException();

        if(tc == TagClass.Private && tagClass != C_Private_Class)
            throw new TypeMismatchAsnException();
        
        if ((T & C_Constructed_Flag) != 0)
            throw new TypeMismatchAsnException();

        int V_Length = decodeLength();

        byte[] value = new byte[V_Length];
        System.arraycopy(m_buffer, m_offset, value, 0, V_Length);
        m_offset += V_Length;

        return value;
    }

    public byte[] OctetString(int requiredLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString();
        if (value.length != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be %d, while the actual length is %d.", requiredLength, value.length));
        return value;   
    }

    public byte[] OctetString(int requiredLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString(tc);
        if (value.length != requiredLength)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be %d, while the actual length is %d.", requiredLength, value.length));
        return value;   
    }

    public byte[] OctetString(int minLength, int maxLength) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString();
        if (value.length < minLength || maxLength < value.length)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length));
        return value;   
    }

    public byte[] OctetString(int minLength, int maxLength, TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString(tc);
        if (value.length < minLength || maxLength < value.length)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be in the range [%d, %d], while the actual length is %d.", minLength, maxLength, value.length));
        return value;   
    }

    public UUID OctetStringToUUID() throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString();
        if (value.length != 16)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be 16, while the actual length is %d.", value.length));
        java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(value);            
		return new UUID(nioBuffer.getLong(), nioBuffer.getLong());
    }

    public UUID OctetStringToUUID(TagClass tc) throws FormatAsnException, EndOfContainerAsnException, TypeMismatchAsnException, ConstraintAsnException
    {
    	byte[] value = OctetString(tc);
        if (value.length != 16)
            throw new ConstraintAsnException(String.format("The length of the input octet string must be 16, while the actual length is %d.", value.length));
        java.nio.ByteBuffer nioBuffer = java.nio.ByteBuffer.wrap(value);            
		return new UUID(nioBuffer.getLong(), nioBuffer.getLong());
    }
    
    private void throwExceptionOnTagClassNull() {
    	throw new IllegalArgumentException("The argument value for the tag class parameter is null.");
    }
    
	private int decodeLength() throws FormatAsnException
    {
    	m_offset++;
    	try
    	{    		
    		if (m_offset >= data_end)
                throw new FormatAsnException();
    		
	        int L1_Byte = m_buffer[m_offset] & 0xFF;
	        m_offset++;
	
	        if (L1_Byte <= 127)
	        {
	        	if (m_offset + L1_Byte > data_end)
                    throw new FormatAsnException();
                return L1_Byte;
	        }
		        
	        if (L1_Byte == 128)
	        	throw new FormatAsnException("The ASN.1 Codec does not support the indefinite length form.");
	        
	        int bytes = L1_Byte & 0x7F;
	
	        if(bytes == 1)
	        {	        	
	        	if (m_offset >= data_end)
	                throw new FormatAsnException();
	        	
	        	int length = m_buffer[m_offset] & 0xFF;
                m_offset++;

                if (m_offset + length > data_end)
                    throw new FormatAsnException();

                return length;
	        }
	    	else if(bytes == 2)
	    	{        		
	    		if (m_offset + 1 >= data_end)
	                throw new FormatAsnException();
	    		
	    		int b1 = m_buffer[m_offset] & 0xFF;
                int b0 = m_buffer[m_offset + 1] & 0xFF;
                m_offset += 2;

                int length = (b1 << 8) | b0;

                if (m_offset + length > data_end)
                    throw new FormatAsnException();
                
                return length;
	    	}
	    	else if(bytes == 3)
	    	{
	    		if (m_offset + 2 >= data_end)
	                throw new FormatAsnException();
	    		
                int b2 = m_buffer[m_offset] & 0xFF;
                int b1 = m_buffer[m_offset + 1] & 0xFF;
                int b0 = m_buffer[m_offset + 2] & 0xFF;
                m_offset += 3;

                int length = (b2 << 16) | (b1 << 8) | b0;

                if (m_offset + length > data_end)
                    throw new FormatAsnException();

                return length;
	    	}
	    	else if(bytes == 4)
	    	{
	    		if (m_offset + 3 >= data_end)
	                throw new FormatAsnException();
	    		
	    		int b3 = m_buffer[m_offset] & 0xFF;
                if (b3 >= 128)
                    throw new FormatAsnException("The ASN.1 Codec does not support the length of content more than 2GB.");

                int b2 = m_buffer[m_offset + 1] & 0xFF;
                int b1 = m_buffer[m_offset + 2] & 0xFF;
                int b0 = m_buffer[m_offset + 3] & 0xFF;
                m_offset += 4;

                int length = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;

                if (m_offset + length > data_end)
                    throw new FormatAsnException();

                return length;
	    	}
	 
            throw new FormatAsnException("The ASN.1 Codec does not support the length of content more than 2GB.");
    	}
    	catch (ArrayIndexOutOfBoundsException e)
    	{
            throw new FormatAsnException("The size of the input buffer is not enough to contain all the Asn1 data.");
    	}
    }
}











