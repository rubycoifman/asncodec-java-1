package softnet.asn;

public interface TexpDecoder {
	boolean isThis(int tag);
	boolean isThis(TagClass tc);
	
	void validateClass(boolean flag);
	
	boolean exists(UType type);
	boolean exists(int tag);
	
	boolean isUniversal();
	boolean isClass(TagClass tc);
	
	TexpDecoder Texp() throws AsnException;
	TexpDecoder Texp(TagClass tc) throws AsnException;

	SequenceDecoder Sequence() throws AsnException;
	SequenceDecoder Sequence(TagClass tc) throws AsnException;
	
	SequenceOfDecoder SequenceOf(UType uType) throws AsnException;
	SequenceOfDecoder SequenceOf(UType uType, TagClass tc) throws AsnException;
	
	int Int32() throws AsnException;
	int Int32(TagClass tc) throws AsnException;
	
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

	String BMPString() throws AsnException;
	String BMPString(TagClass tc) throws AsnException;

	String IA5String() throws AsnException;
	String IA5String(TagClass tc) throws AsnException;

	String PrintableString() throws AsnException;
	String PrintableString(TagClass tc) throws AsnException;

	java.util.GregorianCalendar GndTimeToGC() throws AsnException;
	java.util.GregorianCalendar GndTimeToGC(TagClass tc) throws AsnException;

	byte[] OctetString() throws AsnException;
	byte[] OctetString(TagClass tc) throws AsnException;
}
