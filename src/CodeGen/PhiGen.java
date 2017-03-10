package CodeGen;

import java.util.ArrayList;
import java.util.HashMap;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;

public class PhiGen
{

	public ArrayList<String> Phi_if(BasicBlock left, BasicBlock right, BasicBlock join, IcCodeGen icGen, CFG c)
	{
		HashMap<String,Integer> leftVariable = left.GetLatestVariableVersion();
		HashMap<String,Integer> rightVariable = right.GetLatestVariableVersion();
		ArrayList<String> phiVariables = new ArrayList<String>();
		
		boolean isWhile = (join.GetInstructionList().size()!=0);


		for(String key : leftVariable.keySet())
		{
			if(leftVariable.get(key) != rightVariable.get(key))
			{
				phiVariables.add(key);
			}
		}


		for(String key : phiVariables)
		{
			Result r1 = new Result(key,leftVariable.get(key));
			Result r2 = new Result(key,rightVariable.get(key));
			int ssa = c.AddNewSSA(key);
			Result r3 = new Result(key,ssa);
			icGen.generate(r1, r2, r3, Instruction.phi, join, c);
			//int ssa = c.AddNewSSA(key);
			join.AddNewSSA(key,ssa );
		}
		
		
		if(isWhile)
		{
			ArrayList<Integer> instructions= join.GetInstructionList();

			for(int key :instructions)
			{
				Instruction ins = c.GetInstruction(key);

				int opCode = ins.GetOpCode();
				
				if(opCode >= Instruction.add && opCode <= Instruction.cmp )
				{
					Result r1 = ins.GetResult(1);
					Result r2 = ins.GetResult(2);


					if(r1.GetKind()==Result.Kind.VARIABLE)
					{
						if(phiVariables.contains(r1.GetName()) && r1.getSSA()==left.GetLastestSSAOf(r1.GetName()))
						{
							r1.SetSSA(c.GetLatestSSA(r1.GetName()));
						}
					}
					
					if(r2.GetKind()==Result.Kind.VARIABLE)
					{
						if(phiVariables.contains(r2.GetName()) && r2.getSSA()==left.GetLastestSSAOf(r2.GetName()))
						{
							r2.SetSSA(c.GetLatestSSA(r2.GetName()));
						}
					}

				}
			}
				
		}
		
		
		return phiVariables;
	}


	public void PropagatePhi(ArrayList<String> phiVariables, BasicBlock bb,BasicBlock parent, CFG c, ArrayList<Integer> visited)
	{
		if(phiVariables.size()==0 || bb==null || visited.contains(bb.GetId()))
			return;

		ArrayList<Integer> instructions= bb.GetInstructionList();
		//System.out.println("Instructions no:" +instructions.size()+" BlockId:"+bb.GetId() + " Block Type:"+ bb.getType());
		for(int key :instructions)
		{
			Instruction ins = c.GetInstruction(key);

			int opCode = ins.GetOpCode();

			if(opCode >= Instruction.add && opCode <= Instruction.cmp )
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);


				if(r1.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r1.GetName()) && r1.getSSA()==parent.GetLastestSSAOf(r1.GetName()))
					{
						r1.SetSSA(c.GetLatestSSA(r1.GetName()));
					}
				}
				
				if(r2.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r2.GetName()) && r2.getSSA()==parent.GetLastestSSAOf(r2.GetName()))
					{
						r2.SetSSA(c.GetLatestSSA(r2.GetName()));
					}
				}

			}

			else if(opCode == Instruction.store)
			{
				Result r = ins.GetResult(1);
				//System.out.println("The result:"+ r.GetName());
				if(r.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r.GetName()) && r.getSSA()==parent.GetLastestSSAOf(r.GetName()))
					{
						r.SetSSA(c.GetLatestSSA(r.GetName()));
					}
				}
			}
			
			else if(opCode == Instruction.write)
			{
				Result r = ins.getW();
				if(r.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r.GetName()) && r.getSSA()==parent.GetLastestSSAOf(r.GetName()))
					{
						r.SetSSA(c.GetLatestSSA(r.GetName()));
					}
				}
			}

			else if(opCode == Instruction.phi)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				Result r3 = ins.GetResult(3);

				if(r1.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r1.GetName()) && r1.getSSA()==parent.GetLastestSSAOf(r1.GetName()))
					{
						r1.SetSSA(c.GetLatestSSA(r1.GetName()));
					}
				}
				
				if(r2.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r2.GetName()) && r2.getSSA()==parent.GetLastestSSAOf(r2.GetName()))
					{
						r2.SetSSA(c.GetLatestSSA(r2.GetName()));
					}
				}
				
				if(phiVariables.contains(r3.GetName()))
						phiVariables.remove(r3.GetName());
	
				
				
			}

			else if(opCode == Instruction.move)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				
				if(r1.GetKind()==Result.Kind.VARIABLE)
				{
					if(phiVariables.contains(r1.GetName()) && r1.getSSA()==parent.GetLastestSSAOf(r1.GetName()))
					{
						r1.SetSSA(c.GetLatestSSA(r1.GetName()));
					}
				}
				
				if(phiVariables.contains(r2.GetName()))
					phiVariables.remove(r2.GetName());
				
			}
		}
		
		ArrayList<String> newPhiVariablesLeft = new ArrayList<String>();
		ArrayList<String> newPhiVariablesRight = new ArrayList<String>();
		for(String key :phiVariables)
		{
			newPhiVariablesLeft.add(key);
			newPhiVariablesRight.add(key);
		}
		visited.add(bb.GetId());
		PropagatePhi(newPhiVariablesLeft, bb.GetChild(1),parent, c, visited);
		PropagatePhi(newPhiVariablesRight, bb.GetChild(2),parent, c, visited);


	}
	
}