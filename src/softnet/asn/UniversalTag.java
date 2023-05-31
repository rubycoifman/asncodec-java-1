package softnet.asn;

class UniversalTag
{
	public final static byte Boolean = 1;
	public final static byte Integer = 2;
	public final static byte OctetString = 4;
	public final static byte Null = 5;
	public final static byte Real = 9;
	public final static byte UTF8String = 12;           
	public final static byte Sequence = 16;
	public final static byte PrintableString = 19;	// A-Z a-z 0-9 space ' ( ) + , - . / : = ?
	public final static byte IA5String = 22;		// ASCII 0-127
	public final static byte GeneralizedTime = 24;
	public final static byte BMPString = 30;		// Big-Endian UTF-16
}
