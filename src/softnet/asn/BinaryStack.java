package softnet.asn;

class BinaryStack
{
	private byte[] m_buffer = null;
	private int m_position = 0;

    public byte[] buffer()
    {
        return m_buffer;
    }

    public int position()
    {
        return m_position;
    }

    public int count()
    {
        return m_buffer.length - m_position;
    }

    public void allocate(int memorySize)
    {
        m_buffer = new byte[memorySize];
        m_position = memorySize;
    }

    public void stack(byte value)
    {
    	m_position -= 1;
        m_buffer[m_position] = value;
    }

    public void stack(int byteValue)
    {
    	m_position -= 1;
        m_buffer[m_position] = (byte)byteValue;
    }
    
    public void stack(byte[] data)
    {
    	m_position -= data.length;
        System.arraycopy(data, 0, m_buffer, m_position, data.length);
    }
    
    public void stack(byte[] data, int offset, int size)
    {
    	m_position -= size;
    	System.arraycopy(data, offset, m_buffer, m_position, size);
    }
}
