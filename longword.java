public class longword implements ILongword
{
	bit[] bits = new bit[32];
	
	public longword()
	{
		this.set(0); // default longword will be set to an empty longword
	}
	
	public longword(int value)
	{
		this.set(value); // call the set method to create the longword
	}
	
	public bit getBit(int i) // Get bit i
	{
		return bits[i];
	}
	
    public void setBit(int i, bit value) // set bit i's value
    {
    	bits[i] = value;
    }
    
    public longword and(longword other) // and two longwords, returning a third
    {
    	longword newLongword = new longword();
    	for(int i = 0; i < 32; i++)
    	{
    		bit thisBit = this.getBit(i);
    		bit otherBit = other.getBit(i);
    		newLongword.setBit(i, thisBit.and(otherBit));
    	}
    	return newLongword;
    }
    
    public longword or(longword other) // or two longwords, returning a third
    {
    	longword newLongword = new longword();
    	for(int i = 0; i < 32; i++)
    	{
    		bit thisBit = this.getBit(i);
    		bit otherBit = other.getBit(i);
    		newLongword.setBit(i, thisBit.or(otherBit));
    	}
    	return newLongword;
    }
    
    public longword xor(longword other) // xor two longwords, returning a third
    {
    	longword newLongword = new longword();
    	for(int i = 0; i < 32; i++)
    	{
    		bit thisBit = this.getBit(i);
    		bit otherBit = other.getBit(i);
    		newLongword.setBit(i, thisBit.xor(otherBit));
    	}
    	return newLongword;
    }
    
    public longword not() // negate this longword, creating another
    {
    	longword newLongword = new longword();
    	for(int i = 0; i < 32; i++)
    	{
    		bit thisBit = this.getBit(i);
    		newLongword.setBit(i, thisBit.not());
    	}
    	return newLongword;
    }
    
    public longword rightShift(int amount) // rightshift this longword by amount bits, creating a new longword
    {
    	longword newLongword = new longword(); // default longword is 0s
    	if(amount >= 0)
    	{
			for(int i = 0; i < 32 - amount; i++) // excludes bits that will not be included after the shift
			{
				bit thisBit = this.getBit(i);
				newLongword.setBit(i + amount, thisBit); // copy values from this longword into the new longword at an offset equal to the shift
			}
    	}
    	else
    	{
    		newLongword = leftShift(-amount);
    	}
    	return newLongword;
    }
    
    public longword leftShift(int amount) // leftshift this longword by amount bits, creating a new longword
    {
    	longword newLongword = new longword(); // default longword is 0s
    	if(amount >= 0)
    	{
	    	for(int i = amount; i < 32; i++) // offset the counter to exclude bits that will not be included after the shift
	    	{
	    		bit thisBit = this.getBit(i);
	    		newLongword.setBit(i - amount, thisBit); // copy values from this longword into the new longword at an index without the offset
	    	}
    	}
    	else
    	{
    		newLongword = rightShift(-amount);
    	}
    	return newLongword;
    }
    
    @Override
    public String toString() // returns a comma separated string of 0's and 1's: "0,0,0,0,0 (etcetera)" for example
    {
    	String longwordString = "";
    	for(int i = 0; i < 32; i++)
    	{
    		bit currentBit = this.getBit(i);
    		int value = currentBit.getValue();
    		longwordString += value + ", ";
    	}
    	longwordString = longwordString.substring(0, longwordString.length() - 2); // cut off the excess ", "
    	return longwordString;
    }
    
    public long getUnsigned() // returns the value of this longword as a long
    {
    	long unsignedLongword = 0;
    	for(int i = 31; i >= 0; i--) // loop from the last bit to the first bit
    	{
    		unsignedLongword += this.getBit(i).getValue() * Math.pow(2, 31 - i); // add the corresponding power of 2 at the current bit
    	}
    	return unsignedLongword;
    }
    
    public int getSigned() // returns the value of this longword as an int
    {
    	int signedLongword = 0;
    	for(int i = 31; i > 0; i--) // loop from the last bit to the bit before the sign bit
    	{
    		signedLongword += this.getBit(i).getValue() * Math.pow(2, 31 - i); // add the corresponding power of 2 at the current bit
    	}
    	if(this.getBit(0).getValue() == 1) // check the sign bit for a sign
    	{
    		signedLongword *= -1; // invert the number
    	}
    	return signedLongword;
    }
    
    public void copy(longword other) // copies the values of the bits from another longword into this one
    {
    	for(int i = 0; i < 32; i++)
    	{
    		bit otherBit = other.getBit(i);
    		this.setBit(i, otherBit);
    	}
    }
    
    public void set(int value) // set the value of the bits of this longword (used for tests)
    {
    	if(value < 0) // if the value is negative
    	{
    		this.setBit(0, new bit(1)); // set the sign bit
    	}
    	else
    	{
    		this.setBit(0, new bit(0)); //otherwise clear the sign bit
    	}
    	int i = 31; // need to set the bits in a reverse order to represent the number in a standard binary form for numbers
    	// some method of converting decimal to binary
    	while (value != 0) // loops until no more bits can be set to 1
    	{
    		this.setBit(i, new bit(Math.abs(value % 2))); // sets the modulus of the value by 2 to the bit
    		value /= 2; // divide the value by 2
    		i--;
    	}
    	for(;i > 0; i--) // set the rest of the bits to 0
    	{
    		this.setBit(i, new bit(0));
    	}
    }
}