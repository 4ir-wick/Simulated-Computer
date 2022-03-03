public class cpu_test3
{
	public static void main(String[] args) throws Exception
	{
		runTests();
	}
	
	private static void runTests() throws Exception
	{
		cpuTests();
	}
	
	private static void cpuTests() throws Exception
	{
		String[] pushTest = {"move R1 63", "move R2 62", "push R1", "push R2", "push R1", "printmem", "printreg", "halt"};
		
		String[] popTest = {"move R1 63", "move R2 62", "push R1", "push R2", "push R1", "pop R3", "pop R4", "pop R5", "printmem", "printreg", "halt"};
		
		String[] callTest = {"call 5", "move R1 1", "printmem", "printreg", "halt", "printmem", "return", "move R1 2", "printmem", "printreg", "halt"};
		
		System.out.println("Push Test");
		cpuTest(assembler.assemble(pushTest));
		
		System.out.println("Pop Test");
		cpuTest(assembler.assemble(popTest));
		
		System.out.println("Call Test");
		cpuTest(assembler.assemble(callTest));
	}
	
	private static void cpuTest(String[] loadStrings)
	{
		computer computer = new computer();
		computer.preload(loadStrings);
		computer.run();
	}
}