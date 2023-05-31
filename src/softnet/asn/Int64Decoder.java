package softnet.asn;

class Int64Decoder
{
    public static long decode(byte[] buffer, int offset, int length) throws FormatAsnException, OverflowAsnException
    {
        if (length < 1)
            throw new FormatAsnException();

        long A = buffer[offset];
        if (length == 1)
        {
            return A;
        }

        long B = buffer[offset + 1] & 0xFF;
        if (length == 2)
        {
            return (A << 8) | B;
        }

        long C = buffer[offset + 2] & 0xFF;
        if (length == 3)
        {
            return (A << 16) | (B << 8) | C;
        }

        long D = buffer[offset + 3] & 0xFF;
        if (length == 4)
        {
            return (A << 24) | (B << 16) | (C << 8) | D;
        }

        long E = buffer[offset + 4] & 0xFF;
        if (length == 5)
        {
            return (A << 32) | (B << 24) | (C << 16) | (D << 8) | E;
        }

        long F = buffer[offset + 5] & 0xFF;
        if (length == 6)
        {
            return (A << 40) | (B << 32) | (C << 24) | (D << 16) | (E << 8) | F;
        }

        long G = buffer[offset + 6] & 0xFF;
        if (length == 7)
        {
            return (A << 48) | (B << 40) | (C << 32) | (D << 24) | (E << 16) | (F << 8) | G;
        }

        if (length == 8)
        {
            long H = buffer[offset + 7] & 0xFF;
            return (A << 56) | (B << 48) | (C << 40) | (D << 32) | (E << 24) | (F << 16) | (G << 8) | H;
        }

        // length > 8
        throw new OverflowAsnException("The input number cannot be represented as a 64-bit signed integer.");
    }
}
