package softnet.asn;

import java.util.regex.Pattern;

class IA5StringDecoder
{
	public static String decode(byte[] buffer, int offset, int V_length) throws FormatAsnException
    {
        if (V_length == 0)
            return "";
        
        String value = new String(buffer, offset, V_length, java.nio.charset.StandardCharsets.US_ASCII);
        
        if (Pattern.matches("[^\u0000-\u007F]", value))
        	throw new FormatAsnException("The input data contains characters that are not permitted in Asn1 IA5String.");
        
        return value;
    }
}
