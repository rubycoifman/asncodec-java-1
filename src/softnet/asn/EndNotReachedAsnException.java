package softnet.asn;

public class EndNotReachedAsnException extends AsnException
{
	private static final long serialVersionUID = 759574769116654999L;
	public EndNotReachedAsnException()
	{
		super("There is more data in the ASN.1 Sequence to be decoded.");
	}
}
