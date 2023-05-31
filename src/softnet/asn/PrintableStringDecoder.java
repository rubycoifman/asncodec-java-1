package softnet.asn;

import java.util.regex.Pattern;

class PrintableStringDecoder
{
    public static String decode(byte[] buffer, int offset, int V_length) throws FormatAsnException
    {
        if (V_length == 0)
            return "";
        
        String value = new String(buffer, offset, V_length, java.nio.charset.StandardCharsets.US_ASCII);
        
        if (Pattern.matches("^[\\u0020-\\u007F]+$", value) == false)
        	throw new FormatAsnException("The input data contains characters that are not permitted in Asn1 PrintableString.");
        
        if (Pattern.matches("^[A-Za-z0-9\\s'()+,\\-./:=?]*$", value) == false)
        	throw new FormatAsnException("The input data contains characters that are not permitted in Asn1 PrintableString.");
        
        return value;
    }
}
