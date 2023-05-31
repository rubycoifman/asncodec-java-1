package softnet.asn;

public interface TexpEncoder {
	void setThis(int tag);
	void setThis(int tag, TagClass tc);

	TexpEncoder Texp();
	
	SequenceEncoder Sequence();
	SequenceEncoder Sequence(int tag);
	SequenceEncoder Sequence(int tag, TagClass tc);
	
	SequenceOfEncoder SequenceOf(UType uType);	
	SequenceOfEncoder SequenceOf(int tag, UType uType);	
	SequenceOfEncoder SequenceOf(int tag, UType uType, TagClass tc);	

	void Int32(int value);
	void Int32(int tag, int value);
	void Int32(int tag, int value, TagClass tc);

	void Int64(long value);
	void Int64(int tag, long value);
	void Int64(int tag, long value, TagClass tc);

	void Boolean(boolean value);
	void Boolean(int tag, boolean value);
	void Boolean(int tag, boolean value, TagClass tc);

	void Real32(float value);
	void Real32(int tag, float value);
	void Real32(int tag, float value, TagClass tc);

	void Real64(double value);
	void Real64(int tag, double value);
	void Real64(int tag, double value, TagClass tc);

	void UTF8String(String value);
	void UTF8String(int tag, String value);
	void UTF8String(int tag, String value, TagClass tc);

	void BMPString(String value);
	void BMPString(int tag, String value);
	void BMPString(int tag, String value, TagClass tc);

	void IA5String(String value);
	void IA5String(int tag, String value);
	void IA5String(int tag, String value, TagClass tc);

	void PrintableString(String value);
	void PrintableString(int tag, String value);
	void PrintableString(int tag, String value, TagClass tc);

	void GndTime(java.util.GregorianCalendar value);
	void GndTime(int tag, java.util.GregorianCalendar value);
	void GndTime(int tag, java.util.GregorianCalendar value, TagClass tc);

	void OctetString(byte[] buffer);
	void OctetString(int tag, byte[] buffer);
	void OctetString(int tag, byte[] buffer, TagClass tc);

	void OctetString(byte[] buffer, int offset, int length);
	void OctetString(int tag, byte[] buffer, int offset, int length);
	void OctetString(int tag, byte[] buffer, int offset, int length, TagClass tc);

	void OctetString(java.util.UUID uuid);
	void OctetString(int tag, java.util.UUID uuid);
	void OctetString(int tag, java.util.UUID uuid, TagClass tc);
}
