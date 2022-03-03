public class computer
{
	private longword PC;
	private longword currentInstruction;
	private memory memory;
	private longword[] registers;
	private bit[] opcode;
	private longword op1;
	private longword op2;
	private longword op3;
	private longword ALUOutputRegister;
	private longword moveRegister;
	private longword moveValue;
	private longword addressRaw;
	private longword booleanOperation;
	private longword result;
	private longword stackPointer;
	private bit halted;
	private bit interrupt;
	
	public computer()
	{
		PC = new longword();
		currentInstruction = new longword();
		memory = new memory();
		registers = new longword[16];
		for(int i = 0; i < registers.length; i++) registers[i] = new longword();
		opcode = new bit[4];
		op1 = new longword();
		op2 = new longword();
		op3 = new longword();
		ALUOutputRegister = new longword();
		moveValue = new longword();
		addressRaw = new longword();
		booleanOperation = new longword();
		result = new longword();
		stackPointer = new longword(1020);
		halted = new bit(0);
		interrupt = new bit(0);
	}
	
	public void run()
	{
		while(halted.getValue() != 1)
		{
			fetch();
			decode();
			execute();
			store();
		}
	}
	
	private void fetch()
	{
		interrupt.clear();
		longword read = memory.read(PC);
		currentInstruction = read; // second half of instruction
		PC = rippleAdder.add(PC, new longword(1));
		read = memory.read(PC);
		for(int i = 16; i < 24; i++)
			currentInstruction.setBit(i, read.getBit(i + 8)); // first half of instruction
		if(currentInstruction.getUnsigned() == 8192 || currentInstruction.getUnsigned() == 8193) interrupt.set(); // if longword represents an interrupt command
		PC = rippleAdder.add(PC, new longword(1));
	}
	
	private void decode()
	{
		if(interrupt.getValue() == 0) // don't decode during interrupt
		{
			longword opcodeLongword = currentInstruction.leftShift(16).rightShift(28); // isolate last 16 bits, keep 4 starting bits
			for(int i = 0; i < 4; i++)
				opcode[i] = opcodeLongword.getBit(i + 28); // set opcode to corresponding bits in the opcodeLongword from the currentInstruction
			longword source1 = currentInstruction.leftShift(20).rightShift(28); // isolate last 12 bits, keep 4 starting bits
			longword source2 = currentInstruction.leftShift(24).rightShift(28); // isolate last 8 bits, keep 4 starting bits
			longword source3 = currentInstruction.leftShift(28).rightShift(28); // isolate last 4 bits, keep 4 starting bits
			int source1Index = (int)source1.getUnsigned() - 1;
			int source2Index = (int)source2.getUnsigned() - 1;
			int source3Index = (int)source3.getUnsigned() - 1;
			if(source1Index < 0) source1Index = 0;
			if(source2Index < 0) source2Index = 0;
			if(source3Index < 0) source3Index = 0;
			longword op1Temp = registers[source1Index];
			longword op2Temp = registers[source2Index];
			longword op3Temp = registers[source3Index];
			op1 = op1Temp;
			op2 = op2Temp;
			op3 = op3Temp;
			ALUOutputRegister = source3;
			moveRegister = source1;
			moveValue = currentInstruction.leftShift(24).rightShift(24); // isolate last 8 bits, keep 8 starting bits
			addressRaw = currentInstruction.leftShift(20).rightShift(20); // isolate last 12 bits, keep 12 starting bits
			booleanOperation = currentInstruction.leftShift(20).rightShift(30); // isolate last 12 bits, keep 2 starting bits
		}
	}
	
	private void execute()
	{
		if(interrupt.getValue() == 1)
		{
			if(currentInstruction.getBit(31).getValue() == 0)
			{
				System.out.println("Registers:\n");
				for(longword register : registers) System.out.println(register);
			}
			else
			{
				System.out.println("Memory:\n");
				for(int i = 0; i < 1024; i++) System.out.println(memory.read(new longword(i)));
			}
		}
		else result = ALU.doOp(opcode, op1, op2); // if not interrupting, perform an operation
	}
	
	private void store()
	{
		if(interrupt.getValue() == 0)
		{
			if(opcodeEquals(0, 0, 0, 0)) halted.set(); // halt
			else if(opcodeEquals(0, 0, 0, 1)) // move
				registers[(int)moveRegister.getUnsigned() - 1] = moveValue;
			else if(opcodeEquals(0,0,1,1)) // jump
				jump(multiplier.multiply(addressRaw, new longword(2)));
			else if(opcodeEquals(0,1,0,0)) // compare
			{
				longword compare = rippleAdder.subtract(op2, op3);
				registers[15] = compare;
 			}
			else if(opcodeEquals(0,1,0,1)) // branchIf
			{
				if(booleanOperationIsTrue())
				{
					longword addressAdjusted = addressRaw.leftShift(21).rightShift(21); // isolate last 9 bits, keep starting 9 bits (10 bits without the sign)
					addressAdjusted.setBit(0, addressRaw.getBit(22)); // sets the sign bit
					addressAdjusted = multiplier.multiply(addressAdjusted, new longword(2));
					addressAdjusted = rippleAdder.subtract(addressAdjusted, new longword(2));
					PC = rippleAdder.add(PC, addressAdjusted);
				}
			}
			else if(opcodeEquals(0,1,1,0)) // stack related
			{
				if(booleanOperationEquals(0,0)) // push
					stackPush(op3);
				else if(booleanOperationEquals(0,1)) // pop
					registers[(int)ALUOutputRegister.getUnsigned() - 1] = stackPop(); // set a register to the pop value
				else if(booleanOperationEquals(1,0)) // call
				{
					stackPush(PC); // push the PC address
					longword addressAdjusted = addressRaw.leftShift(22).rightShift(22); // isolate last 10 bits, keep starting 10 bits
					addressAdjusted = multiplier.multiply(addressAdjusted, new longword(2));
					jump(addressAdjusted); // jump
				}
				else if(booleanOperationEquals(1,1)) // return
					jump(stackPop()); // jump to the pop value (the pop value should be a stored PC address)
			}
			else // ALU
				registers[(int)ALUOutputRegister.getUnsigned() - 1] = result;
		}
	}
	
	public void preload(String[] loadStrings)
	{
		for(int i = 0; i < loadStrings.length; i++)
		{
			String loadString = loadStrings[i];
			int instructionInt = 0;
			for(int j = 0; j < 16; j++)
			{
				char bitChar = loadString.charAt(j); // character representing the bit
				int bitInt = Integer.parseInt(Character.toString(bitChar)); // convert charater of bit to integer
				instructionInt += bitInt * Math.pow(2, 15 - j); // get corresponding power of 2 for the current bit
			}
			longword instruction = new longword(instructionInt);
			memory.write(new longword(i * 2), instruction); // first memory byte
			memory.write(new longword(i * 2 + 1), instruction.rightShift(8)); // second memory byte
		}
	}
	
	private boolean opcodeEquals(int value1, int value2, int value3, int value4)
	{
		return opcode[0].getValue() == value1 && opcode[1].getValue() == value2 && opcode[2].getValue() == value3 && opcode[3].getValue() == value4;
	}
	
	private void jump(longword address)
	{
		PC = address;
	}
	
	private boolean booleanOperationIsTrue()
	{
		longword compare = registers[15];
		if(booleanOperationEquals(0,0))
			return compare.getSigned() > 0;
		else if(booleanOperationEquals(0,1))
			return compare.getSigned() >= 0;
		else if(booleanOperationEquals(1,0))
			return compare.getSigned() != 0;
		else if(booleanOperationEquals(1,1))
			return compare.getSigned() == 0;
		return false;
	}
	
	private boolean booleanOperationEquals(int value1, int value2)
	{
		return booleanOperation.getBit(30).getValue() == value1 && booleanOperation.getBit(31).getValue() == value2;
	}
	
	private void stackPush(longword value)
	{
		for(int i = 0; i < 4; i++) // next 4 entries in memory
		{
			memory.write(new longword(stackPointer.getSigned() + i), value.rightShift((3 - i) * 8)); 
		}
		stackPointer = rippleAdder.subtract(stackPointer, new longword(4));
	}
	
	private longword stackPop()
	{
		longword pop = new longword(0);
		stackPointer = rippleAdder.add(stackPointer, new longword(4)); // need to move the pointer to a position where we can read data
		for(int i = 0; i < 4; i++) // next 4 entries in memory
		{
			longword memoryRead = memory.read(new longword(stackPointer.getSigned() + i));
			int currentOffset = i * 8;
			for(int j = 0; j < 8; j++) // set 8 bits of the longword at a time
			{
				pop.setBit(j + currentOffset, memoryRead.getBit(j + 24));
			}
			memory.write(new longword(stackPointer.getSigned() + i), new longword(0)); // set the stack bits back to 0
		}
		return pop;
	}
}