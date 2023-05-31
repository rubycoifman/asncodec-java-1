package softnet.asn;

public enum UType {
	Boolean(1),
	Integer(2),
	OctetString(4),
	Null(5),
	Real(9),
	UTF8String(12),
	Sequence(16),
	PrintableString(19),
	IA5String(22),
	GeneralizedTime(24),
	BMPString(30);

	public final int tag;
	private UType(int tag) { 
		this.tag = tag; 
	}
}
