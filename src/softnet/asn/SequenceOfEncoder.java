package softnet.asn;

public interface SequenceOfEncoder{
	int count();
	int getSize();	
	
	SequenceEncoder Sequence();
	SequenceOfEncoder SequenceOf(UType uType);
	void Int32(int value);
	void Int64(long value);
	void Boolean(boolean value);
	void Real32(float value);
	void Real64(double value);
	void UTF8String(String value);
	void BMPString(String value);
	void IA5String(String value);
	void PrintableString(String value);
	void GndTime(java.util.GregorianCalendar value);
	void OctetString(byte[] buffer);
	void OctetString(byte[] buffer, int offset, int length);
	void Null();
}
