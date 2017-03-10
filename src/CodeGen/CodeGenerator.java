package CodeGen;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import DLX.dlx;
import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;


public class CodeGenerator {
	
	private static final int STACK_POINTER = 29;
	private static final int FRAME_POINTER = 28;
	private static final int GLOBAL_VARIABLE_MEMORY = 30;
	
	private List<Integer> programCodes;
	public List<Integer> getProgramCodes() {
		return programCodes;
	}

	public void setProgramCodes(List<Integer> programCodes) {
		this.programCodes = programCodes;
	}



	private CFG cfg;
	private int codeNumber;
	private HashMap<Integer, Integer> blockVsCodeNumber;
	private HashMap<Integer,Integer> fixUp;
	private HashMap<Integer,String> fixUp_A;
	private HashMap<String,Integer> array_baseAddress;
	
	
	public CodeGenerator(CFG cfg){
		this.cfg = cfg;
		programCodes = new ArrayList<Integer>();
		blockVsCodeNumber = new HashMap<Integer, Integer>();
		fixUp = new HashMap<Integer, Integer>();
		fixUp_A = new HashMap<Integer, String>();
		array_baseAddress = new HashMap<String,Integer>();
		
		
		HashMap<String,ArrayList<Integer>>arrayTable = this.cfg.getArrayTable();
		int size =0;
		for(String s:arrayTable.keySet())
		{
			
			array_baseAddress.put(s,size );
			
			int size_individual =1;
			
			for(int i:arrayTable.get(s))
				size_individual*=i;
			
			size+=size_individual*4;
		}
		
		
		/*for(String s : array_baseAddress.keySet())
		{
			System.out.println(s +" "+array_baseAddress.get(s));
		}
		*/
		int code = dlx.assemble(16, 28, 0 ,0);
		addCodeToProgram(code);
	}
	
	public void generateCode(BasicBlock b, BasicBlock stop, boolean flag, BasicBlock ret, boolean flagR )
	{
		
		if(b==null)
			return;
		
		if(flag==true && b==stop)
			return;
		
		if(flagR==true && b == ret)
			return;
		
		generateCodeForBlockBody(b);
	
		
		if(b.getType() == BasicBlock.BlockType.IF_MAIN)
		{
			generateCode(b.GetChild(1), b.getIfJoin(), true,ret,flagR);
			generateCode(b.GetChild(2), b.getIfJoin(), true,ret,flagR);
			
			
			generateCode(b.getIfJoin(), stop, flag,ret,flagR);
			updateBranch(b.GetId(), b.GetChild(2).GetId());
			updateJSRBranch(b.getIfJoin().getParents().get(1).GetId(), b.getIfJoin().GetId() );
		}
	
		else if(b.getType() == BasicBlock.BlockType.WHILE_MAIN)
		{
			generateCode(b.GetChild(1), stop, flag,b,true);
			
			generateCode(b.GetChild(2), stop, flag,ret,flagR);
			updateBranch(b.GetId(), b.GetChild(2).GetId());
			//updateJSRBranch(b.getWhileBodyLast().GetId(),b.GetId());
		}
/*		else if(basicBlock.getType() == BasicBlock.BlockType.IF)
		{
			
		}
		else if(basicBlock.getType() == BasicBlock.BlockType.JOIN )
		{
			int code = blockVsCodeNumber.get(basicBlock.GetId());
			updateBranchCode(OpCode.JSR, code);
			
		}
		else if(basicBlock.getType() == BasicBlock.BlockType.ELSE)
		{
			
		}
		else if(basicBlock.getType() == BasicBlock.BlockType.NORMAL)
		{
			
		}
	*/	
		else
		{
		generateCode(b.GetChild(1), stop, flag,ret,flagR);
		generateCode(b.GetChild(2), stop, flag,ret,flagR);
		}
		
		
	}
	
	public void addCodeToProgram(int code)
	{
		//System.out.println("Code being added: "+code);
		programCodes.add(code);
		codeNumber++;
	}
	
	public void updateCodeIntheProgram(int code, int position)
	{
		//System.out.println("Code being updated: "+code +" At pos: "+position);
		if(position < 0 || position >= programCodes.size()){
			error("WRONG_FIXUP_LOCATION");
			return;
		}
		programCodes.add(position, code);
		programCodes.remove(position+1);
	}
	
	public void generateCodeForBlockBody(BasicBlock basicBlock){
		
		//System.out.println("Generating code for:" + basicBlock.GetId());
		blockVsCodeNumber.put(basicBlock.GetId(), codeNumber);
		ArrayList<Integer> instructions = basicBlock.GetInstructionList();
		for(int instructionId : instructions){
			Instruction ins = cfg.GetInstruction(instructionId);
			//System.out.println(ins.toString());
			//Operation operation = instruction.getOperation();
			switch(ins.GetOpCode()){
				
				case Instruction.add:
					addArithmeticCode(dlx.ADD, dlx.ADDI, ins);
					break;
				
				case Instruction.sub:
					addArithmeticCode(dlx.SUB, dlx.SUBI, ins);
					break;
				
				case Instruction.mul:
					addArithmeticCode(dlx.MUL, dlx.MULI, ins);
					break;
				
				case Instruction.div:
					addArithmeticCode(dlx.DIV, dlx.DIVI, ins);
					break;
				
				case Instruction.adda:
					addArithmeticCode(dlx.ADD, dlx.ADDI, ins);
					break;
				
				case Instruction.cmp:
					addArithmeticCode(dlx.CMP, dlx.CMPI, ins);
					break;
					
				case Instruction.read:
					addIOCode(Instruction.read, ins);
					break;
				
				case Instruction.write:
					addIOCode(Instruction.write, ins);
					break;
					
				case Instruction.writeNL:
					addIOCode(Instruction.writeNL, ins);
					break;	
					
				case Instruction.end:
					addEndCode();
					break;
					
				case Instruction.bra:
					addBranchCode(dlx.JSR, ins, basicBlock);
					break;
	
				case Instruction.beq:
					addControlCode(dlx.BEQ, ins, basicBlock);
					break;
				case Instruction.bne:
					addControlCode(dlx.BNE, ins, basicBlock);
					break;
				case Instruction.blt:
					addControlCode(dlx.BLT, ins, basicBlock);
					break;
				case Instruction.bge:
					addControlCode(dlx.BGE, ins, basicBlock);
					break;
				case Instruction.ble:
					addControlCode(dlx.BLE, ins, basicBlock);
					break;
				case Instruction.bgt:
					addControlCode(dlx.BGT, ins, basicBlock);
					break;
					
				case Instruction.move:
					addArithmeticCode(dlx.ADD, dlx.ADDI, ins);
					break;
					
				case Instruction.load:
					addLoadStoreCode(dlx.LDW,ins);
					break;
					
				case Instruction.store:
					addLoadStoreCode(dlx.STW,ins);
					break;
	/*			case CALL:
					addCallCode(instruction);
					break;
				case RET:
					addReturnCode(instruction);
					break;
				case PARAM:
					addParamCode(instruction);
					break;	
	*/				
			}
		}
	}
	
	

	private void addBranchCode(int opCode, Instruction instruction, BasicBlock b) {
		
		Integer targetCode = blockVsCodeNumber.get(instruction.getTargetBlock());
		if(targetCode == null){
			targetCode = 0;
			fixUp.put(b.GetId(), codeNumber);
		}
		else
		{
			targetCode*=4;
		}
		
		int code = dlx.assemble(opCode,targetCode);
		addCodeToProgram(code);
	}
	
	private void updateBranch(int from, int to)
	{
		
		int pos = fixUp.get(from);
		int code = programCodes.get(pos);
		int target = blockVsCodeNumber.get(to);
		//System.out.println("Blk vs codeNum: " +to+" "+target);
		target = target-pos;
		code = code | target;
		updateCodeIntheProgram(code,pos);
	}
	private void updateJSRBranch(int from, int to)
	{
		//System.out.println("Update JSR: "+ from +" "+ to +" "+fixUp.get(from));
		int pos = fixUp.get(from);
		int code = programCodes.get(pos);
		int target = blockVsCodeNumber.get(to);
		//System.out.println("Blk vs codeNum: " +to+" "+target);
		target = target*4;
		code = code | target;
		updateCodeIntheProgram(code,pos);
	}
	

	private void addParamCode(Instruction instruction) {
		
		
	}

	private void addReturnCode(Instruction instruction) {
		
		
	}

	private void addCallCode(Instruction instruction) {
		
	}
	
	
	private void addControlCode(int opCode, Instruction instruction , BasicBlock b) {
		int code =0;
		Result condition = instruction.getConditionInstruction();
		//System.out.println("Test: " + instruction.toString());
		int target = instruction.getTargetBlock();
		fixUp.put(b.GetId(), codeNumber);
		code = dlx.assemble(opCode, condition.getRegister(), 0);
		
		
		addCodeToProgram(code);
	}
	
	
	

	private void addEndCode() {
		int code = dlx.assemble(dlx.RET, 0);		
		addCodeToProgram(code);
	}

	
	private void addLoadStoreCode(int opCode, Instruction ins)
	{
		int code = 0;
		Result operand1 = ins.GetResult(1); 
		Result operand2 = ins.GetResult(2);
		int resultRegNo = ins.getRegister();
		
		if(ins.GetOpCode()==Instruction.load)
		{
			code = dlx.assemble(opCode,resultRegNo,operand1.getRegister(),0);
		}
		
		if(ins.GetOpCode()==Instruction.store)
		{
			if(operand1.getRegister()==-1)
			{
				code = dlx.assemble(dlx.ADDI, 27,0 ,operand1.GetValue());
				addCodeToProgram(code);
				code = dlx.assemble(dlx.STW,27,operand2.getRegister(),0);
				
			}
			else
			{
				code = dlx.assemble(opCode,operand1.getRegister(),operand2.getRegister(),0);
			}
		}
		addCodeToProgram(code);
		
	}
	
	private void addArithmeticCode(int opCode, int immediateOpCode, Instruction instruction) {
	
		int code = 0;
		Result operand1 = instruction.GetResult(1); 
		Result operand2 = instruction.GetResult(2);
		
		if(instruction.GetOpCode()==Instruction.move)
		{
			
			if(operand2.getRegister()!=-1)
			{
				if(operand1.getRegister()==-1)
				{
					code = dlx.assemble(immediateOpCode, operand2.getRegister(),0 ,operand1.GetValue());
					//System.out.println(operand2.getRegister() +" "+operand1.GetValue());
					
				}
				else
				{
					code = dlx.assemble(opCode, operand2.getRegister(),0 ,operand1.getRegister());
				}
			}
			//System.out.println("move code: "+ code);
			addCodeToProgram(code);
			return;
		}
		
		
		
		
		
		int resultRegNo = instruction.getRegister();
		
		
		
		if(operand1.GetKind()==Result.Kind.FRAME_POINTER && operand2.GetKind()==Result.Kind.BASE_ADDRESS)
		{
			code = dlx.assemble(dlx.ADDI ,resultRegNo, 28, 0);
			//System.out.println("array add code: "+operand2.getVarName()+" "+ code);
			fixUp_A.put(codeNumber, operand2.getVarName());
			addCodeToProgram(code);
			return;
		}
		
		
		
		if(operand1.getRegister()!=-1 && operand2.getRegister()!=-1)
		{
			code = 		dlx.assemble(opCode ,resultRegNo, operand1.getRegister(), operand2.getRegister());					
		}
		
		else if(operand1.getRegister()!=-1 && operand2.getRegister()==-1)
		{
			code = dlx.assemble(immediateOpCode, resultRegNo, operand1.getRegister(), operand2.GetValue());
		}
		
		else if(operand1.getRegister()==-1 && operand2.getRegister()!=-1)
		{
			code = dlx.assemble(immediateOpCode, resultRegNo, operand2.getRegister(), operand1.GetValue());
		}
		else if(operand1.getRegister()==-1 && operand2.getRegister()==-1)
		{
			//System.out.println(getF1Format(16, 0, 0, operand1.GetValue()));
			
			addCodeToProgram(dlx.assemble(dlx.ADDI, resultRegNo, 0, operand1.GetValue()));
			code = dlx.assemble(immediateOpCode, resultRegNo, resultRegNo, operand2.GetValue());
		}
		addCodeToProgram(code);
		
	}

	
	private void addIOCode(int opCode, Instruction ins)
	{
		
		int code =0;
		
		if(opCode == Instruction.read)
		{
			code = dlx.assemble(dlx.RDI, ins.getRegister());
			
		}
		
		else if (opCode == Instruction.write)
		{
			if(ins.getW().getRegister()!=-1)
			{
			code = dlx.assemble(dlx.WRD, ins.getW().getRegister());
			//System.out.println("Write register:"+ ins.getW().getRegister() );
	
			}
			else
			{
				code = dlx.assemble(dlx.ADDI, 27,0 ,ins.getW().GetValue());
				addCodeToProgram(code);
				code = dlx.assemble(dlx.WRD, 27);
				
			}
		}
		
		else if (opCode == Instruction.writeNL)
		{
			code = dlx.assemble(dlx.WRL);
		}
		addCodeToProgram(code);
	}
	
	
	
	/**
	 * op --> 6 bits
	 * a --> 5 bits
	 * b --> 5 bits
	 * c --> 16 bits
	 * @param op
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
/*	public int getF1Format(int op, int a, int b, int c){
		if(op > 63 || op < 0){
			error("ErrorMessage.OP_CODE_NOT_WITHIN_THE_RANGE");
		}
		if(a < 0 || a > 31 || b < 0 || b > 31){
			error("ErrorMessage.REGISTER_NOT_WITHIN_RANGE");
		}
		if(c > (65536 - 1) || c < 0){
			error("ErrorMessage.LITERAL_NOT_WITHIN_RANGE");
		}
		
		return op << 26 | a << 21 | b << 16 | c;
		
	}
	*/
	/**
	 * op --> 6 bits
	 * a --> 5 bits
	 * b --> 5 bits
	 * empty --> 11 bits
	 * c --> 5 bits
	 * @param op
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
/*	public int getF2Format(int op, int a, int b, int c){
		if(op > 63 || op < 0){
			error("ErrorMessage.OP_CODE_NOT_WITHIN_THE_RANGE");
		}
		if(a < 0 || a > 31 || b < 0 || b > 31 || c < 0 || c > 31){
			error("ErrorMessage.REGISTER_NOT_WITHIN_RANGE");
		}
		return op << 26 | a << 21 | b << 16 | c;
	}
	*/
	/**
	 * op --> 6 bits
	 * c --> 26 bits
	 * @param op
	 * @param c
	 * @return
	 */
/*	public int getF3Format(int op, int c){
		if(op > 63 || op < 0){
			error("ErrorMessage.OP_CODE_NOT_WITHIN_THE_RANGE");
		}
		if(c < 0 || c > 67108864 - 1){
			error("ErrorMessage.C_ABSOLUTE_NOT_WITHIN_THE_RANGE");
		}
		return op << 26 | c;
	}
	*/
	public void error(String errorMessage){
		System.out.println(errorMessage);
	}
	
	
	
	public void FixUpArrays()
	{
		int code = dlx.assemble(16, 28, 0 ,codeNumber*4);
		updateCodeIntheProgram( code, 0);
		/*for(int pos:fixUp_A.keySet())
		{
			System.out.println("position:"+pos+" "+fixUp_A.get(pos));
		}*/
		
		for(int pos:fixUp_A.keySet())
		{
			code = programCodes.get(pos);
			code = code | array_baseAddress.get(fixUp_A.get(pos));
			updateCodeIntheProgram( code, pos);
		}
		
		
		
		
	}
	
}
