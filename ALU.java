public class ALU
{
	public static longword doOp(bit[] operation, longword a, longword b)
	{
		longword result = new longword();
		if(operation.length == 4) // must be 4 bits
		{
			// checks each individual bit and chooses the correct operation
			if(operation[0].getValue() == 1)
			{
				if(operation[1].getValue() == 1)
				{
					if(operation[2].getValue() == 1)
					{
						if(operation[3].getValue() == 1)
						{
							result = rippleAdder.subtract(a, b);
						}
						else
						{
							result = rippleAdder.add(a, b);
						}
					}
					else
					{
						if(operation[3].getValue() == 1)
						{
							int shift = getShift(b); // only shift based on the last 5 bits of b
							result = a.rightShift(shift);
						}
						else
						{
							int shift = getShift(b); // only shift based on the last 5 bits of b
							result = a.leftShift(shift);
						}
					}
				}
				else
				{
					if(operation[2].getValue() == 1)
					{
						if(operation[3].getValue() == 1)
						{
							result = a.not();
						}
						else
						{
							result = a.xor(b);
						}
					}
					else
					{
						if(operation[3].getValue() == 1)
						{
							result = a.or(b);
						}
						else
						{
							result = a.and(b);
						}
					}
				}
			}
			else
			{
				if(operation[1].getValue() == 1)
				{
					result = multiplier.multiply(a, b);
				}
			}
		}
		return result;
	}
	
	private static int getShift(longword l)
	{
		int shift = 0;
		for(int i = 27; i < 32; i++) // last 5 bits
		{
			// sums the multiples of 2 for the corresponding bits
			shift += l.getBit(i).getValue() * Math.pow(2, 31 - i);
		}
		return shift;
	}
}