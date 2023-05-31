package softnet.asn;

public class EndOfContainerAsnException extends AsnException
{
	private static final long serialVersionUID = 8957059346962906349L;
	public EndOfContainerAsnException()
	{
		super("There is no more data in the ASN.1 container.");
	}
}
