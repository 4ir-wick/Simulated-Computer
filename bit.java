public class bit implements IBit {
	private int bit;

	public bit()
	{
		this.clear();
	}

	public bit(int value)
	{
		this.set(value);
	}

	public void set(int value) // sets the value of the bit
	{
		bit = value;
	}

	public void toggle() // changes the value from 0 to 1 or 1 to 0
	{
		switch (bit) {
		case 0:
			bit = 1;
			break;
		case 1:
			bit = 0;
			break;
		}
	}

	public void set() // sets the bit to 1
	{
		bit = 1;
	}

	public void clear() // sets the bit to 0
	{
		bit = 0;
	}

	public int getValue() // returns the current value
	{
		return bit;
	}

	public bit and(bit other) // performs and on two bits and returns a new bit
								// set to the result
	{
		bit newBit = new bit();
		if (bit == 1) {
			if (other.getValue() == 1) { //if both bits are 1
				newBit.set();
			}
		}
		return newBit;
	}

	public bit or(bit other) // performs or on two bits and returns a new bit
								// set to the result
	{
		bit newBit = new bit();
		if (bit == 1) { //if either bit is a 1
			newBit.set();
		} else if (other.getValue() == 1) {
			newBit.set();
		}
		return newBit;
	}

	public bit xor(bit other) // performs xor on two bits and returns a new bit
								// set to the result
	{
		bit newBit = new bit();
		if (bit == 1) {
			if (other.getValue() == 0) { //requires a specific combination of a 1 and a 0
				newBit.set();
			}
		} else if (bit == 0) {
			if (other.getValue() == 1) {
				newBit.set();
			}
		}
		return newBit;
	}

	public bit not() // performs not on the existing bit, returning the result
						// as a new bit
	{
		bit newBit = new bit();
		switch (bit) {
		case 0:
			newBit.set();
			break;
		case 1:
			newBit.clear();
			break;
		}
		return newBit;
	}

	@Override
	public String toString() // returns "0" or "1"
	{
		String str = "";
		str += bit;
		return str;
	}
}