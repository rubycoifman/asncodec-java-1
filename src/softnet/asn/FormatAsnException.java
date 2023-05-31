package softnet.asn;

public class FormatAsnException extends AsnException
{
	private static final long serialVersionUID = 2272592628121722310L;

	public FormatAsnException(String message)
	{		
		super(message);
	}
	
    public FormatAsnException()
    {
    	super("The input data has an invalid ASN.1 format.");
    }
}
