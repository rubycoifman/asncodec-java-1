package softnet.asn;

public interface SequenceOfDecoder {
	int count() throws FormatAsnException;
	boolean hasNext();
	void skip() throws AsnException;
	void end() throws EndNotReachedAsnException;	
	boolean isNull() throws AsnException;
	
	SequenceDecoder Sequence() throws AsnException;
	SequenceOfDecoder SequenceOf(UType uType) throws AsnException;
	int Int32() throws AsnException;
	int Int32(int minValue, int maxValue) throws AsnException;
    long Int64() throws AsnException;
    boolean Boolean() throws AsnException;
    float Real32() throws AsnException;
    float Real32(boolean checkForUnderflow) throws AsnException;
    double Real64() throws AsnException;
    double Real64(boolean checkForUnderflow) throws AsnException;
    String UTF8String() throws AsnException;
    String UTF8String(int requiredLength) throws AsnException;
    String UTF8String(int minLength, int maxLength) throws AsnException;
    String BMPString() throws AsnException;
    String BMPString(int requiredLength) throws AsnException;
    String BMPString(int minLength, int maxLength) throws AsnException;
    String IA5String() throws AsnException;
    String IA5String(int requiredLength) throws AsnException;
    String IA5String(int minLength, int maxLength) throws AsnException;
    String PrintableString() throws AsnException;
    java.util.GregorianCalendar GndTimeToGC() throws AsnException;
    byte[] OctetString() throws AsnException;
    byte[] OctetString(int requiredLength) throws AsnException;
    byte[] OctetString(int minLength, int maxLength) throws AsnException;
}
