package softnet.asn;

interface ElementEncoder
{
	boolean isConstructed();	
	int estimateSize();
	int encodeTLV(BinaryStack binStack);
	int encodeLV(BinaryStack binStack);
}
