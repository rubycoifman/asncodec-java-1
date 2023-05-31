package softnet.asn;

class UTF8StringDecoder
{
	public static String Decode(byte[] buffer, int offset, int V_length)
    {
        if (V_length == 0)
            return "";
        return new String(buffer, offset, V_length, java.nio.charset.StandardCharsets.UTF_8);
    }
}
