package softnet.asn;

public interface SequenceDecoder
{
	int count() throws FormatAsnException;
	boolean hasNext();
	void skip() throws AsnException;
	void end() throws EndNotReachedAsnException;

	boolean exists(int tag);	
	boolean exists(UType type);

	boolean isUniversal() throws EndOfContainerAsnException;
	boolean isClass(TagClass tc) throws EndOfContainerAsnException;
	boolean isNull() throws EndOfContainerAsnException;

	void validateClass(boolean flag);

	TexpDecoder Texp() throws AsnException;
	TexpDecoder Texp(TagClass tc) throws AsnException;
	
	SequenceDecoder Sequence() throws AsnException;
	SequenceDecoder Sequence(TagClass tc) throws AsnException;
	
	SequenceOfDecoder SequenceOf(UType uType) throws AsnException;
	SequenceOfDecoder SequenceOf(UType uType, TagClass tc) throws AsnException;
	
	int Int32() throws AsnException;
	int Int32(TagClass tc) throws AsnException;
	
	int Int32(int minValue, int maxValue) throws AsnException;
	int Int32(int minValue, int maxValue, TagClass tc) throws AsnException;
    
	long Int64() throws AsnException;
	long Int64(TagClass tc) throws AsnException;

	boolean Boolean() throws AsnException;
	boolean Boolean(TagClass tc) throws AsnException;

	float Real32() throws AsnException;
	float Real32(TagClass tc) throws AsnException;

	float Real32(boolean checkForUnderflow) throws AsnException;
    float Real32(boolean checkForUnderflow, TagClass tc) throws AsnException;

    double Real64() throws AsnException;
    double Real64(TagClass tc) throws AsnException;

    double Real64(boolean checkForUnderflow) throws AsnException;
    double Real64(boolean checkForUnderflow, TagClass tc) throws AsnException;

    String UTF8String() throws AsnException;
    String UTF8String(TagClass tc) throws AsnException;

    String UTF8String(int requiredLength) throws AsnException;
    String UTF8String(int requiredLength, TagClass tc) throws AsnException;

    String UTF8String(int minLength, int maxLength) throws AsnException;
    String UTF8String(int minLength, int maxLength, TagClass tc) throws AsnException;

    String BMPString() throws AsnException;
    String BMPString(TagClass tc) throws AsnException;

    String BMPString(int requiredLength) throws AsnException;
    String BMPString(int requiredLength, TagClass tc) throws AsnException;

    String BMPString(int minLength, int maxLength) throws AsnException;
    String BMPString(int minLength, int maxLength, TagClass tc) throws AsnException;

    String IA5String() throws AsnException;
    String IA5String(TagClass tc) throws AsnException;

    String IA5String(int requiredLength) throws AsnException;
    String IA5String(int requiredLength, TagClass tc) throws AsnException;

    String IA5String(int minLength, int maxLength) throws AsnException;
    String IA5String(int minLength, int maxLength, TagClass tc) throws AsnException;

    String PrintableString() throws AsnException;
    String PrintableString(TagClass tc) throws AsnException;

    java.util.GregorianCalendar GndTimeToGC() throws AsnException;
    java.util.GregorianCalendar GndTimeToGC(TagClass tc) throws AsnException;

    byte[] OctetString() throws AsnException;
    byte[] OctetString(TagClass tc) throws AsnException;

    byte[] OctetString(int requiredLength) throws AsnException;
    byte[] OctetString(int requiredLength, TagClass tc) throws AsnException;

    byte[] OctetString(int minLength, int maxLength) throws AsnException;
    byte[] OctetString(int minLength, int maxLength, TagClass tc) throws AsnException;

    java.util.UUID OctetStringToUUID() throws AsnException;
    java.util.UUID OctetStringToUUID(TagClass tc) throws AsnException;
}
