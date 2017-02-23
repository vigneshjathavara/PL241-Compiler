package CodeGen;

import Structures.Result;

import java.util.ArrayList;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;

public class IcCodeGen
{

	public Result generate(Result r1, Result r2, int opCode, BasicBlock bb, CFG c)
	{
		Result res = null;

		if(opCode>=Instruction.add && opCode <=Instruction.store)
		{
			if((r1.GetKind() == Result.Kind.CONSTANT) && (r2.GetKind()==Result.Kind.CONSTANT))
			{
				switch(opCode)
				{
				case Instruction.add : res = new Result(Result.Kind.CONSTANT, r1.GetValue()+r2.GetValue());
				break;

				case Instruction.sub : res = new Result(Result.Kind.CONSTANT,r1.GetValue()-r2.GetValue());
				break;

				case Instruction.mul : res = new Result(Result.Kind.CONSTANT,r1.GetValue()*r2.GetValue());
				break;				

				case Instruction.div : res = new Result(Result.Kind.CONSTANT,r1.GetValue()/r2.GetValue());
				break;						
				}

				return res;
			}

			if(r1.GetKind()==Result.Kind.ARRAY || r2.GetKind()==Result.Kind.ARRAY)
			{
				//To Be Implemented
				System.out.print("IcCodeGen Encountered Result of type Array");
			}


			else 
			{
				Instruction i = new Instruction(Instruction.Type.NORMAL, r1,r2,opCode ,c, bb);
				System.out.println("Instruction : " + i.toString());
				res = new Result(Result.Kind.INSTRUCTION,i.GetId());
			}

			return res;
		}
		
		
		
		
		if(opCode == Instruction.move)
		{
			Instruction i = new Instruction(Instruction.Type.NORMAL, r2,r1,opCode,c,bb);
			System.out.println("Instruction : " + i.toString());
			res = new Result(Result.Kind.INSTRUCTION, i.GetId());		
			return res;
		}

		if(opCode == Instruction.end)
		{
			Instruction i = new Instruction(Instruction.Type.END,Instruction.end,c,bb);
			System.out.println("Instruction : " + i.toString());
			return res;
		}

		/*if(opCode >=Instruction.bne && opCode <= Instruction.bgt)
		{
			Instruction i = new Instruction(Instruction.Type.BRANCH, r1, opCode, c,bb);
			System.out.println("Instruction : " + i.toString());
			res = new Result(Result.Kind.INSTRUCTION, i.GetId());		
			return res;
		}
		*/
		




		return res;

	}

	
	public void generate(Result r1, Result r2, Result r3, int opCode, BasicBlock bb, CFG c)
	{
		if(opCode == Instruction.phi)
		{
			Instruction i = new Instruction(Instruction.Type.PHI, r1,r2, r3, opCode ,c, bb);
			System.out.println("Instruction : " + i.toString());
			//res = new Result(Result.Kind.INSTRUCTION,i.GetId());
			
		}
	}
	

	public Result generate(Result r1, int opCode, BasicBlock bb, CFG c)
	{
		Result res = null;
		Instruction i = null;
		if(opCode>=Instruction.bne && opCode<= Instruction.bgt)
			i =new Instruction(Instruction.Type.CBRANCH,r1,opCode,c,bb);//conditional branch
		else
			i = new Instruction(Instruction.Type.NORMAL,r1,opCode,c,bb);//load
		System.out.println("Instruction : " + i.toString());
		res = new Result(Result.Kind.INSTRUCTION,i.GetId());
		return res;

	}
	
	public Result generate(int blockId, int opCode, BasicBlock bb, CFG c )
	{
		Instruction i = new Instruction(Instruction.Type.UBRANCH, blockId, opCode, c, bb);
		System.out.println("Instruction : " + i.toString());
		Result res = new Result(Result.Kind.INSTRUCTION,i.GetId());
		return res;
	}

	public Result generate(Result arr, BasicBlock bb, CFG c)
	{
		Result res = null;
		//Instruction i =null;
		ArrayList<Integer> dims = c.GetArrayDims(arr.GetName());
		ArrayList<Result> loc = arr.GetDims();
		Result r1=null,r2;

		if(loc.size()==1)
		{
				r1 = loc.get(0);	
		}
		else
		{	
			
			for(int i =0; i<loc.size()-1;i++)
			{
				int num=1;
				for(int j=i+1; j<dims.size();j++)
				{
					num*=dims.get(j);
				}
				Result temp = new Result(Result.Kind.CONSTANT,num);
				
				if(i>0)
				{
					Result temp2 = generate(loc.get(i), temp, Instruction.mul, bb, c);
					r1 = generate(r1 , temp2, Instruction.add, bb, c);
				}
				
				else
				{
					r1 = generate(loc.get(i), temp, Instruction.mul, bb, c);
				}
				
			}
			
			r1 = generate(r1, loc.get(loc.size()-1) , Instruction.add, bb, c);
		}

		r2 = new Result(Result.Kind.CONSTANT, 4);
		r1 = generate(r1, r2, Instruction.mul, bb, c);
		
		Result fp = new Result(Result.Kind.FRAME_POINTER);
		Result ba = new Result(arr.GetName());
		
		r2 = generate(fp , ba, Instruction.add, bb, c); 
		res = generate(r1 , r2, Instruction.adda, bb, c);

		return res;
	}

}