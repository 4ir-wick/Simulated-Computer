public class memory
{
	private bit[][] storage;
	
	public memory()
	{
		storage = new bit[1024][8];
		for(int i = 0; i < 1024; i++)
		{
			for(int j = 0; j < 8; j++)
			{
				storage[i][j] = new bit(0);
			}
		}
	}
	
	public longword read(longword address)
	{
		int intAddress = getAddress(address);
		bit[] bits = storage[intAddress];
		longword read = longwordFromBits(bits);
		return read;
	}
	
	public void write(longword address, longword value)
	{
		int intAddress = getAddress(address);
		for(int i = 0; i < 8; i++)
		{
			storage[intAddress][i] = value.getBit(24 + i); // for each value in the array of bits, set the corresponding bits that represent the last 8 bits
		}
	}
	
	private int getAddress(longword longword) // returns a number address from a longword
	{
		int num = 0;
		for(int i = 22; i < 32; i++) // only including last 10 bits (2^10 = 1024)
		{
			num += longword.getBit(i).getValue() * Math.pow(2, 31 - i); // add powers 2 for each corresponding bit
		}
		return num;
	}
	
	private longword longwordFromBits(bit[] bits) // returns a longword from an array of bits
	{
		longword newLongword = new longword();
		for(int i = 32 - bits.length; i < 32; i++) // only including last bits.length(8) bits
		{
			newLongword.setBit(i, bits[i - (32 - bits.length)]); // set the corresponding bit from the array of bits
		}
		return newLongword;
	}
}