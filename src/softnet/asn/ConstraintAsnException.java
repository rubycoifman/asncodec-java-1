package softnet.asn;

public class ConstraintAsnException extends AsnException
{
	private static final long serialVersionUID = 2395269591363432969L;

	public ConstraintAsnException(String message)
	{		
		super(message);
	}
	
    public ConstraintAsnException()
    {
    	super("The input data doesn't match the validation restrictions.");
    }	
}
