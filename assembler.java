import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class assembler
{
	public static String[] assemble(String[] inputStrings) throws Exception
	{
		if(inputStrings.length > 512)
			throw new Exception("Not enough memory");
		ArrayList<ArrayList<String>> inputStringsElements = new ArrayList<ArrayList<String>>();
		for(int i  = 0; i < inputStrings.length; i++)
		{
			String inputString = inputStrings[i];
			ArrayList<String> inputStringElements = new ArrayList<String>();
			String currentInputStringElement = "";
			int state = 0;
			for(int j = 0; j < inputString.length(); j++)
			{
				char inputChar = inputString.charAt(j);
				state = getState(inputChar);
				if(state == 1) //letters and numbers
				{
					currentInputStringElement += inputChar; // build the element
				}
				if(j == inputString.length() - 1) // end of line
					state = 2;
				if(state == 2) // end of word/line
				{
					if(currentInputStringElement.length() > 0) // if not an ignored character
					{
						inputStringElements.add(currentInputStringElement); // add the element
						currentInputStringElement = "";
					}
					state = 0;
				}
			}
			inputStringsElements.add(inputStringElements);
		}
		String[] loadStrings = new String[inputStrings.length];
		HashMap<Integer, ArrayList<String>> conditionalMap = new HashMap<Integer, ArrayList<String>>();
		for(int i = 0; i < inputStringsElements.size(); i++)
		{
			ArrayList<String> inputStringElements = inputStringsElements.get(i);
			if(inputStringElements.get(0).equals("compare") || inputStringElements.get(0).contains("BranchIf"))
				conditionalMap.put(i, inputStringElements);
		}
		Map.Entry<Integer, ArrayList<String>> currentCompare = null;
		for(Map.Entry<Integer, ArrayList<String>> conditionalMapElement: conditionalMap.entrySet())
		{
			String elementType = conditionalMapElement.getValue().get(0);
			if(elementType.equals("compare"))
				currentCompare = conditionalMapElement;
			if(elementType.equals("BranchIfLessThan") || elementType.equals("BranchIfLessThanOrEqual"))
			{
				if(currentCompare != null)
				{
					currentCompare = swapCompareRegisters(currentCompare);
					conditionalMap.replace(currentCompare.getKey(), currentCompare.getValue());
					currentCompare = null;
				}
				else
					throw new Exception("BranchIf missing compare");
			}
		}
		for(Map.Entry<Integer, ArrayList<String>> conditionalMapElement: conditionalMap.entrySet())
			inputStringsElements.set(conditionalMapElement.getKey(), conditionalMapElement.getValue());
		for(int i = 0; i < inputStringsElements.size(); i++)
		{
			ArrayList<String> inputStringElements = inputStringsElements.get(i);
			try
			{
				String loadString = convertToLoadString(inputStringElements);
				loadStrings[i] = loadString;
			}
			catch(Exception e)
			{
				System.out.println("Line " + i + ": "+ e.getMessage());
			}
		}
		return loadStrings;
	}
	
	private static Map.Entry<Integer, ArrayList<String>> swapCompareRegisters(Map.Entry<Integer, ArrayList<String>> compare)
	{
		ArrayList<String> stringElements = compare.getValue();
		String register1 = stringElements.get(1);
		String register2 = stringElements.get(2);
		stringElements.set(1, register2);
		stringElements.set(2, register1);
		Map.Entry<Integer, ArrayList<String>> newCompare = compare;
		newCompare.setValue(stringElements);
		return newCompare;
	}
	
	private static int getState(char inputChar)
	{
		if(isNullSpace(inputChar))
			return 2;
		else if(isLetter(inputChar) || isNumber(inputChar))
			return 1;
		return 0;
	}
	
	private static boolean isNullSpace(char inputChar)
	{
		return inputChar == ' ' || inputChar == '\t' || inputChar == '\n';
	}
	
	private static boolean isUpperCaseLetter(char inputChar)
	{
		return inputChar >= 65 && inputChar <= 90;
	}
	
	private static boolean isLowerCaseLetter(char inputChar)
	{
		return inputChar >= 97 && inputChar <= 122;
	}
	
	private static  boolean isLetter(char inputChar)
	{
		return isUpperCaseLetter(inputChar) || isLowerCaseLetter(inputChar);
	}
	
	private static boolean isNumber(char inputChar)
	{
		return (inputChar >= 48 && inputChar <= 57) || inputChar == '-';
	}
	
	private static String convertToLoadString(ArrayList<String> stringElements) throws Exception
	{
		if(stringElements.size() > 4)
			throw new Exception("Too many arguments");
		String loadString = "";
		String previousStringElement = "";
		for(String stringElement : stringElements)
		{
			loadString += convertStringToBits(previousStringElement, stringElement);
			previousStringElement = stringElement;
		}
		return loadString;
	}
	
	private static String convertStringToBits(String previous, String string) throws Exception
	{
		if(string.charAt(0) == 'R')
		{
			int register = -1;
			try
			{
				register = Integer.parseInt(string.substring(1)); // convert the number after R to int
			}catch(Exception e){}
			if(register > 16 || register < 1)
				throw new Exception("Not a valid register");
			return convertIntToBits(register, 4); // a register may only be 4 bits
		}
		else if(string.equals("move"))
			return "0001";
		else if(string.equals("and"))
			return "1000";
		else if(string.equals("or"))
			return "1001";
		else if(string.equals("xor"))
			return "1010";
		else if(string.equals("not"))
			return "1011";
		else if(string.equals("lshift"))
			return "1100";
		else if(string.equals("rshift"))
			return "1101";
		else if(string.equals("add"))
			return "1110";
		else if(string.equals("sub"))
			return "1111";
		else if(string.equals("mult"))
			return "0111";
		else if(string.equals("jump"))
			return "0011";
		else if(string.equals("compare"))
			return "01000000";
		else if(string.contains("BranchIf"))
			return generateBranchIf(string);
		else if(string.equals("push"))
			return "011000000000";
		else if(string.equals("pop"))
			return "011001000000";
		else if(string.equals("call"))
			return "011010";
		else if(string.equals("return"))
			return "0110110000000000";
		else if(string.equals("halt"))
			return "0000000000000000";
		else if(string.equals("printreg"))
			return "0010000000000000";
		else if(string.equals("printmem"))
			return "0010000000000001";
		try
		{
			int stringAsInt = Integer.parseInt(string);
			if(previous.equals("jump"))
				return convertIntToBits(stringAsInt, 12);
			else if(previous.contains("BranchIf"))
				return convertIntToBits(stringAsInt, 10);
			else if(previous.equals("call"))
				return convertIntToBits(stringAsInt, 10);
			else
				return convertIntToBits(stringAsInt, 8);
		}catch(Exception e){}
		throw new Exception("Not a valid command");
	}
	
	private static String generateBranchIf(String string) throws Exception
	{
		if(string.equals("BranchIfGreaterThan") || string.equals("BranchIfLessThan"))
			return "010100";
		else if(string.equals("BranchIfGreaterThanOrEqual") || string.equals("BranchIfLessThanOrEqual"))
			return "010101";
		else if(string.equals("BranchIfNotEqual"))
			return "010110";
		else if(string.equals("BranchIfEqual"))
			return "010111";
		else
			throw new Exception("Not a boolean operation");
	}
	
	private static String convertIntToBits(int integer, int size)
	{
		int i = size - 1;
		boolean isNeg = false;
		if(integer < 0) // if negative
		{
			isNeg = true;
			i--;
		}
		// a formula for finding the bit values
		String bits = "";
    	while (integer != 0 || i >= 0)
    	{
    		bits = Math.abs(integer % 2) + bits; // add the bit to the left of the string
    		integer /= 2;
    		i--;
    	}
    	if(isNeg)
    		bits = "1" + bits;
    	return bits;
	}
}