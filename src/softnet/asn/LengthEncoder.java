package softnet.asn;

class LengthEncoder
{
	public static int estimateSize(int length)
    {
        if (length <= 0x0000007f) {
            return 1;
        }
        else if (length <= 0x000000ff) {
            return 2;
        }
        else if (length <= 0x0000ffff) {
            return 3;
        }
        else if (length <= 0x00ffffff) {
            return 4;
        }
        else {
            return 5;
        }
    }
	
	public static int encode(int length, BinaryStack binStack)
    {
        if (length <= 0x0000007f)
        {
            binStack.stack((byte)length);
            return 1;
        }
        else if (length <= 0x000000ff)
        {
            binStack.stack((byte)length);
            binStack.stack((byte)(129));

            return 2;
        }
        else if (length <= 0x0000ffff)
        {
            byte b0 = (byte)(length & 0x000000ff);
            byte b1 = (byte)((length >> 8) & 0x000000ff);

            binStack.stack(b0);
            binStack.stack(b1);
            binStack.stack((byte)(130));

            return 3;
        }
        else if (length <= 0x00ffffff)
        {
            byte b0 = (byte)(length & 0x000000ff);
            byte b1 = (byte)((length >> 8) & 0x000000ff);
            byte b2 = (byte)((length >> 16) & 0x000000ff);

            binStack.stack(b0);
            binStack.stack(b1);
            binStack.stack(b2);
            binStack.stack((byte)(131));

            return 4;
        }
        else
        {
            byte b0 = (byte)(length & 0x000000ff);
            byte b1 = (byte)((length >> 8) & 0x000000ff);
            byte b2 = (byte)((length >> 16) & 0x000000ff);
            byte b3 = (byte)((length >> 24) & 0x000000ff);

            binStack.stack(b0);
            binStack.stack(b1);
            binStack.stack(b2);
            binStack.stack(b3);
            binStack.stack((byte)(132));

            return 5;
        }
    }
}
