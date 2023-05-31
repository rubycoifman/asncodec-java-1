package softnet.asn;

class Int32Decoder 
{
    public static int decode(byte[] buffer, int offset, int length) throws FormatAsnException, OverflowAsnException
    {
        if (length < 1)
            throw new FormatAsnException();

        int A = buffer[offset];
        if (length == 1)
        {
            return A;
        }

        int B = buffer[offset + 1] & 0xFF;
        if (length == 2)
        {
            return (A << 8) | B;
        }

        int C = buffer[offset + 2] & 0xFF;
        if (length == 3)
        {
            return (A << 16) | (B << 8) | C;
        }

        if (length == 4)
        {
            int D = buffer[offset + 3] & 0xFF;
            return (A << 24) | (B << 16) | (C << 8) | D;
        }

        // length >= 5
        throw new OverflowAsnException("The input number cannot be represented as a 32-bit signed integer.");
    }
}
