class rippleAdder
{
	public static longword add(longword a, longword b)
	{
		longword result = new longword();
		if(a.getBit(0).getValue() == b.getBit(0).getValue()) // if a and b have the same sign then perform addition
		{
			result.setBit(0, a.getBit(0)); // the result sign should equal the sign of the first longword
			bit carry = new bit();
			for(int i = 31; i > 0; i--) // makes sure to not add the sign bits together
			{
				bit input1 = a.getBit(i);
				bit input2 = b.getBit(i);
				
				// sum
				bit xor1 = input1.xor(input2);
				
				bit sum = xor1.xor(carry);
				result.setBit(i, sum);
				
				// carry
				bit and1 = input1.and(input2);
				bit and2 = xor1.and(carry);
				
				carry = and1.or(and2);
			}
		}
		else // if a and b do not have the same sign
		{
			// this identifies which longword is larger
			int i = 1;
			while(a.getBit(i).xor(b.getBit(i)).getValue() != 1 && i < 31) // finds the index where the bits are different in a and b
			{
				i++;
			}
			if(a.getBit(i).getValue() == 1 || a.getBit(i).getValue() == b.getBit(i).getValue()) // if a is larger than or equal to b
			{
				// negate the smaller
				longword negB = new longword(b.getSigned());
				negB.setBit(0, negB.getBit(0).not());
				
				// subtract the negative smaller from the bigger
				result = subtract(a, negB);
				/*
				 * ex.
				 * +3 + -2 = 1
				 * equivalent to
				 * +3 - +2 = 1
				 */
			}
			else // if a is smaller than b
			{
				// negate the smaller
				longword negA = new longword(a.getSigned());
				negA.setBit(0, negA.getBit(0).not());
				
				// subtract the negative smaller from the bigger
				result = subtract(b, negA);
				/*
				 * ex.
				 * +2 + -3 = -1
				 * equivalent to
				 * -3 - -2 = -1
				 */
			}
		}
		return result;
	}
	
	public static longword subtract(longword a, longword b)
	{
		longword result = new longword();
		
		if(a.getBit(0).getValue() == b.getBit(0).getValue()) // if they have the same sign
		{
			// this identifies which longword is larger
			int j = 1;
			while(a.getBit(j).xor(b.getBit(j)).getValue() != 1 && j < 31) // finds the index where the bits are different in a and b
			{
				j++;
			}
			if(a.getBit(j).getValue() == 1 || a.getBit(j).getValue() == b.getBit(j).getValue()) // if a is larger than or equal to b then perform subtraction
			{
				result.setBit(0, a.getBit(0)); // the result sign should equal the sign of the first longword
				bit borrow = new bit();
				for(int i = 31; i > 0; i--) // makes sure to not subtract the sign bits
				{
					bit input1 = a.getBit(i);
					bit input2 = b.getBit(i);
					
					// diff
					bit xor1 = input1.xor(input2);
					
					bit diff = xor1.xor(borrow);
					result.setBit(i, diff);
					
					// borrow
					bit and1 = input1.not().and(input2);
					bit and2 = xor1.not().and(borrow);
					
					borrow = and1.or(and2);
				}
			}
			else // if a is smaller than b
			{
				// negate the larger
				longword negB = new longword(b.getSigned());
				negB.setBit(0, negB.getBit(0).not());
				
				// negate the smaller
				longword negA = new longword(a.getSigned());
				negA.setBit(0, negA.getBit(0).not());
				
				// subtract the negative smaller from the negative larger
				result = subtract(negB, negA);
				/*
				 * ex.
				 * +2 - +3 = -1
				 * equivalent to
				 * -3 - -2 = -1
				 */
			}
		}
		else // if they have different signs
		{
			// negate b
			longword negB = new longword(b.getSigned());
			negB.setBit(0, negB.getBit(0).not());
			
			// add a and negative b
			result = add(a, negB);
			/*
			 * ex.
			 * +2 - -3 = 5
			 * equivalent to
			 * +2 + +3 = 5
			 */
		}
		return result;
	}
}