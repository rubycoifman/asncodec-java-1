package softnet.asn;

public class ASNEncoder 
{
    private SequenceEncoderImp sequence;
	public ASNEncoder() {
        sequence = new SequenceEncoderImp();
    }

	public int getSize() {
		return sequence.estimateSize();
	}
	
    public SequenceEncoder Sequence() {
        return sequence;
    }

    public byte[] getEncoding()
    {
    	int estimatedSize = sequence.estimateSize();
        BinaryStack binStack = new BinaryStack();
        binStack.allocate(estimatedSize);
        sequence.encodeTLV(binStack);
        
        if(binStack.position() == 0)
        	return binStack.buffer();

        System.out.println(String.format("Data size estimation error. Binstack position: %d", binStack.position()));

        byte[] trimmedBuffer = new byte[binStack.count()];
        System.arraycopy(binStack.buffer(), binStack.position(), trimmedBuffer, 0, binStack.count());
        return trimmedBuffer;
    }

    public byte[] getEncoding(int prefixSize)
    {
        if (prefixSize < 0)
            throw new IllegalArgumentException("'prefixSize' must not be negative.");

        int estimatedSize = sequence.estimateSize();
        BinaryStack binStack = new BinaryStack();
        binStack.allocate(prefixSize + estimatedSize);
        sequence.encodeTLV(binStack);
        
        if(binStack.position() == prefixSize)
        	return binStack.buffer();
        
        System.out.println(String.format("Data size estimation error. Binstack position: %d", binStack.position()));
        
        byte[] trimmedBuffer = new byte[prefixSize + binStack.count()];
        System.arraycopy(binStack.buffer(), binStack.position(), trimmedBuffer, prefixSize, binStack.count());
        return trimmedBuffer;
    }	
}























