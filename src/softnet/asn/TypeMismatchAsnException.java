package softnet.asn;

public class TypeMismatchAsnException extends AsnException
{
	private static final long serialVersionUID = 154896164472176485L;
	public TypeMismatchAsnException()
	{
		super("The content type of the input does not match the expected type.");
	}
}
