package softnet.asn;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

class GndTimeGCEncoder implements ElementEncoder
{
	private byte[] V_bytes;
	private int V_length;
	
    private GndTimeGCEncoder()
    {
    	V_bytes = new byte[19];
    	V_length = 0;
    }

    public static GndTimeGCEncoder create(GregorianCalendar value)
    {
    	GndTimeGCEncoder encoder = new GndTimeGCEncoder();
        encoder.encodeV(value);
        return encoder;
    }

    public boolean isConstructed() {
    	return false;
    }

    public int estimateSize() {
    	return 2 + V_length;
    }

	public int encodeTLV(BinaryStack binStack)
	{
		binStack.stack(V_bytes, 0, V_length);
    	binStack.stack((byte)V_length);    	
        binStack.stack(UniversalTag.GeneralizedTime);
        return 2 + V_length;
	}
	
	public int encodeLV(BinaryStack binStack)
	{
		binStack.stack(V_bytes, 0, V_length);
    	binStack.stack((byte)V_length);    	
        return 1 + V_length;
	}
	
	private void encodeV(GregorianCalendar value)
	{
		value.setTimeZone(TimeZone.getTimeZone("UTC"));
				
		int year = value.get(Calendar.YEAR);
		int month = value.get(Calendar.MONTH);
		int day = value.get(Calendar.DAY_OF_MONTH);
		int hour = value.get(Calendar.HOUR_OF_DAY);
		int minute = value.get(Calendar.MINUTE);
		int second = value.get(Calendar.SECOND);
		int millisecond = value.get(Calendar.MILLISECOND);
				
		int digit = year / 1000;
		V_bytes[0] = (byte)(48 + digit);
		year = year - digit * 1000;
		digit = year / 100;
		V_bytes[1] = (byte)(48 + digit);
		year = year - digit * 100;
		digit = year / 10;
		V_bytes[2] = (byte)(48 + digit);
		digit = year % 10;
		V_bytes[3] = (byte)(48 + digit);
		
		digit = month + 1;
		V_bytes[4] = (byte)(48 + digit / 10);
		V_bytes[5] = (byte)(48 + digit % 10);
		
		V_bytes[6] = (byte)(48 + day / 10);
		V_bytes[7] = (byte)(48 + day % 10);

		V_bytes[8] = (byte)(48 + hour / 10);
		V_bytes[9] = (byte)(48 + hour % 10);

		V_bytes[10] = (byte)(48 + minute / 10);
		V_bytes[11] = (byte)(48 + minute % 10);

		V_bytes[12] = (byte)(48 + second / 10);
		V_bytes[13] = (byte)(48 + second % 10);

		int offset = 14;
		if(millisecond > 0)
		{
			int d1 = millisecond / 100;
			millisecond = millisecond - d1 * 100;
			int d2 = millisecond / 10;
			int d3 = millisecond % 10;

			V_bytes[offset] = '.';
			offset++;
			
			if(d3 != 0)
			{
				V_bytes[offset] = (byte)(48 + d1);
				offset++;
				V_bytes[offset] = (byte)(48 + d2);
				offset++;
				V_bytes[offset] = (byte)(48 + d3);
				offset++;
			}
			else if(d2 != 0)
			{
				V_bytes[offset] = (byte)(48 + d1);
				offset++;
				V_bytes[offset] = (byte)(48 + d2);
				offset++;				
			}
			else
			{
				V_bytes[offset] = (byte)(48 + d1);
				offset++;				
			}
		}
		
		V_bytes[offset] = 'Z';
		V_length = offset + 1;
	}
}
