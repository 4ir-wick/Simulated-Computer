public class multiplier
{
	public static longword multiply(longword a, longword b)
	{
		longword result = new longword();
		for(int i = 31; i > 0; i--)
		{
			longword mult = new longword();
			for(int j = 31; j > 0; j--)
			{
				bit aBit = a.getBit(j); // multiply the entirety of a
				bit bBit = b.getBit(i); // to a single bit of b
				mult.setBit(j, aBit.and(bBit)); // multiplication = and
			}
			mult = mult.leftShift(31-i); // add zeros before the product
			result = rippleAdder.add(result, mult); // add to the result
		}
		result.setBit(0, a.getBit(0).xor(b.getBit(0))); // sign change can be simplified to a xor operation
		return result;
	}
}