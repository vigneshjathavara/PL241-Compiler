package Structures;


public class Instruction
{
	public enum Type{PHI, UBRANCH, CBRANCH, NORMAL, END}

	static int pc=0;

	public static final int neg = 1;
	public static final int add = 2;
	public static final int sub = 3;
	public static final int mul = 4;
	public static final int div = 5;
	public static final int cmp = 6;	
	public static final int adda =7;
	public static final int store =8;
	public static final int phi = 9;
	public static final int load =10;
	public static final int move = 11;
	public static final int end = 12;
	public static final int bra = 13;
	public static final int bne = 14;
	public static final int beq = 15;
	public static final int ble = 16;
	public static final int blt = 17;
	public static final int bge = 18;
	public static final int bgt = 19;
	public static final int read = 20;
	public static final int write= 21;
	public static final int writeNL =22;


	int currentInstructionId;
	int opCode;

	/*Left if variable*/
	Result left;
	int ssaLeft;

	/*Right if variable*/
	Result right;
	int ssaRight;
	
	/*For Phi*/
	Result phiResult;
	int ssaPhiResult;

	int leftInstructionId;//left if instruction
	int rightInstructionId;//right if instruction


	Result conditionInstruction;
	int targetBlock;//target if branch
	Type type;

	public Instruction(Type t, Result l, Result r, int op, CFG c, BasicBlock bb)
	{	

		this.left=l; 
		this.right = r;
		this.opCode = op;
		this.type = t;
		//System.out.println("Instruction :" + op);
		this.currentInstructionId=Instruction.pc++;
		c.AddInstruction(currentInstructionId, this);
		bb.AddInstruction(currentInstructionId);

	}
	
	public Instruction(Type t, Result l, Result r, Result pR, int op, CFG c, BasicBlock bb)
	{	

		this.left=l; 
		this.right = r;
		this.phiResult =pR;
		this.opCode = op;
		this.type = t;
		//System.out.println("Instruction :" + op);
		this.currentInstructionId=Instruction.pc++;
		c.AddInstruction(currentInstructionId, this);	
		bb.AddInstructionFront(currentInstructionId);

	}

	public Instruction(Type t,int op, CFG c, BasicBlock bb)
	{
		if(op ==Instruction.end)
		{
			this.opCode = op;
			this.currentInstructionId = Instruction.pc++;
			c.AddInstruction(currentInstructionId, this);
			bb.AddInstruction(currentInstructionId);
		}
	}

	public Instruction(Type t, int targetBlock, int op, CFG c, BasicBlock bb)
	{
		if(t== Type.UBRANCH)
		{
			this.type=t;
			this.opCode=op;
			this.targetBlock= targetBlock;
			this.currentInstructionId = Instruction.pc++;
			c.AddInstruction(currentInstructionId, this);
			bb.AddInstruction(currentInstructionId);
			
		}
	}
	
	public Instruction(Type t, Result l, int op, CFG c, BasicBlock bb)
	{
		if(t==Type.CBRANCH)
		{
			this.type=t;
			this.conditionInstruction=l;
			this.opCode=op;
			this.targetBlock=-1;
		}	
		
		else
		{
			this.type=t;
			this.left=l;
			this.opCode=op;		
		}

		this.currentInstructionId=Instruction.pc++;
		c.AddInstruction(currentInstructionId, this);
		bb.AddInstruction(currentInstructionId);

	}

	public int GetId()
	{
		return this.currentInstructionId;
	}
	
	public Instruction.Type GetType()
	{
		return this.type;
	}
	
	public int GetOpCode()
	{
		return this.opCode;
	}
	
	public void setResult(int i, Result r)
	{
		if(i==1)
			this.left = r;
		if(i==2)
			this.right = r;
		if(i==3 && this.type == Instruction.Type.PHI)
			this.phiResult = r;
	}
	

	public Result GetResult(int i)
	{
		if(i==1)
			return this.left;
		if(i==2)
			return this.right;
		if(i==3 && this.type == Instruction.Type.PHI)
			return this.phiResult;
		
		return null;
	}
	String OpToString(int op)
	{
		switch(op)
		{
		case Instruction.add: return "add";

		case Instruction.adda: return "adda";

		case Instruction.beq: return "beq";

		case Instruction.bge: return "bge";

		case Instruction.bgt: return "bgt";

		case Instruction.ble: return "ble";

		case Instruction.blt: return "blt";

		case Instruction.bne: return "bne";

		case Instruction.bra: return "bra";

		case Instruction.cmp: return "cmp";

		case Instruction.div: return "div";

		case Instruction.end: return "end";

		case Instruction.load: return "load";

		case Instruction.move: return "move";

		case Instruction.mul: return "mul";

		case Instruction.neg: return "neg";

		case Instruction.phi: return "phi";

		case Instruction.read: return "read";

		case Instruction.store: return "store";

		case Instruction.sub: return "sub";

		case Instruction.write: return "write";

		case Instruction.writeNL: return "writeNL";

		}

		return "N/A";
	}

	public String toString()
	{
		StringBuffer ins = new StringBuffer();
		
		ins.append("" + this.currentInstructionId + " : ");
		ins.append(OpToString(this.opCode));
		ins.append(" ");

		if(this.type == Instruction.Type.NORMAL || this.type == Instruction.Type.END)
		{
			if(this.left != null)
			{
				ins.append(left.toString());
				ins.append(" ");
			}


			if(this.right != null)
			{
				ins.append(right.toString());
				ins.append(" ");
			}
		}
		
		else if(this.type == Instruction.Type.PHI)
		{
			if(this.phiResult != null)
			{
				ins.append(phiResult.toString());
				ins.append(" ");
			}
			
			
			if(this.left != null)
			{
				ins.append(left.toString());
				ins.append(" ");
			}


			if(this.right != null)
			{
				ins.append(right.toString());
				ins.append(" ");
			}
		}
		
		else if(this.type == Instruction.Type.CBRANCH)
		{
			if(conditionInstruction !=null)
			{
				ins.append(conditionInstruction.toString());
				ins.append(" ");
				ins.append("["+this.targetBlock+"]");
				
			}
		}
		
		else if(this.type == Instruction.Type.UBRANCH)
		{
			ins.append("["+this.targetBlock+"]");
		}


		return ins.toString();
	}

	public void FixUp(int blockId)
	{
		this.targetBlock = blockId;
	}


}