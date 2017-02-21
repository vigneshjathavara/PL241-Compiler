package Optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;

public class CopyPropagation
{




	public void CPOptimise(CFG c)
	{

		//HashMap<String,Result> lV = new HashMap<String,Result>();
		//HashMap<String, Integer> lSSA = new HashMap<String,Integer>();
		HashMap<String,HashMap<Integer,Result>> lValue = new HashMap<String,HashMap<Integer,Result>>();
		

		for(String key : c.GetRoot().GetLatestVariableVersion().keySet())
		{
			lValue.put(key, new HashMap<Integer,Result>());      
			lValue.get(key).put(-1, new Result(Result.Kind.CONSTANT,0)); 
		}

		optimise(c.GetRoot(), lValue, c);
	}




	public void optimise(BasicBlock root, HashMap<String,HashMap<Integer,Result>> lValue, CFG c)
	{

		if(root == null)
			return;



		ArrayList<Integer> instructions= root.GetInstructionList();
		Iterator<Integer> it = instructions.iterator();
		while(it.hasNext())
		{
			int key = (int) it.next();
			
			Instruction ins = c.GetInstruction(key);

			int opCode = ins.GetOpCode();

			if(opCode >= Instruction.add && opCode <= Instruction.cmp )
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);


				if(r1.GetKind()==Result.Kind.VARIABLE &&  lValue.get(r1.GetName()).containsKey(r1.getSSA()))
				{
					Result r = new Result(lValue.get(r1.GetName()).get(r1.getSSA()));
					ins.setResult(1, r);//1 is for left operand
				}

				if(r2.GetKind()==Result.Kind.VARIABLE && lValue.get(r2.GetName()).containsKey(r2.getSSA()))
				{
					Result r = new Result(lValue.get(r2.GetName()).get(r2.getSSA()));
					ins.setResult(2, r);//2 is for right operand
				}
				
				/*if(opCode >= Instruction.add && opCode <= Instruction.div && r2.GetKind()==Result.Kind.CONSTANT && r1.GetKind()==Result.Kind.CONSTANT)
				{
					
				}*/
			}

			else if(opCode == Instruction.store)
			{
				Result r1 = ins.GetResult(1);

				if(r1.GetKind()==Result.Kind.VARIABLE && lValue.get(r1.GetName()).containsKey(r1.getSSA()))
				{
					Result r = new Result(lValue.get(r1.GetName()).get(r1.getSSA()));
					ins.setResult(1, r);
				}
			}

			else if(opCode == Instruction.phi)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				Result r3 = ins.GetResult(3);

				if(r1.GetKind()==Result.Kind.VARIABLE && lValue.get(r1.GetName()).containsKey(r1.getSSA()))
				{
					Result r = new Result(lValue.get(r1.GetName()).get(r1.getSSA()));
					ins.setResult(1, r);

				}

				if(r2.GetKind()==Result.Kind.VARIABLE && lValue.get(r2.GetName()).containsKey(r2.getSSA()))
				{
					Result r = new Result(lValue.get(r2.GetName()).get(r2.getSSA()));
					ins.setResult(2, r);
				}

				if(r3.GetKind()==Result.Kind.VARIABLE)
				{
					lValue.get(r3.GetName()).put(r3.getSSA(), r3);
					
				}



			}

			else if(opCode == Instruction.move)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);

				if(r1.GetKind()==Result.Kind.VARIABLE && lValue.get(r1.GetName()).containsKey(r1.getSSA()))
				{
					Result r = new Result(lValue.get(r1.GetName()).get(r1.getSSA()));
					ins.setResult(1, r);
				}

				if(r2.GetKind()==Result.Kind.VARIABLE)
				{
					lValue.get(r2.GetName()).put(r2.getSSA(), r1);
				}
				
				it.remove();
				
			}

		}

		ArrayList <BasicBlock> domList = root.GetDominatorChildren();

		//System.out.println("Block:"+bb.GetId()+"  "+domList.toString());
		/*For Phi operands*/
		//ArrayList <HashMap<String,Result>> lvList = new ArrayList <HashMap<String,Result>>();
		//ArrayList <HashMap<String,Integer>> lssaList = new ArrayList <HashMap<String,Integer>>();
		
		for(BasicBlock b : domList)
		{
			if(b.GetId() != root.GetId())
			{
				//HashMap<String,Result> lV = new HashMap<String,Result>();
				//HashMap<String, Integer> lSSA = new HashMap<String,Integer>();
				

				optimise(b, lValue, c);
			}
		}
		
		if(root.getType()==BasicBlock.BlockType.WHILE_MAIN)
		{
			ArrayList<Integer> inss= root.GetInstructionList();
			
			for(int i:inss)
			{		
				Instruction inst = c.GetInstruction(i);
				if(inst.GetType()!=Instruction.Type.PHI)
					break;
				
				Result r1 = inst.GetResult(1);
				Result r2 = inst.GetResult(2);

				if(r1.GetKind()==Result.Kind.VARIABLE && lValue.get(r1.GetName()).containsKey(r1.getSSA()))
				{
					Result r = new Result(lValue.get(r1.GetName()).get(r1.getSSA()));
					inst.setResult(1, r);

				}

				if(r2.GetKind()==Result.Kind.VARIABLE && lValue.get(r2.GetName()).containsKey(r2.getSSA()))
				{
					Result r = new Result(lValue.get(r2.GetName()).get(r2.getSSA()));
					inst.setResult(2, r);
				}
			}
		}
		
		
		
		
		/*if(domList.size()==3)
		{
			BasicBlock phiBlk = domList.get(2);
			ArrayList<Integer> inss= root.GetInstructionList();
			
			HashMap<String,Result> leftLV = lvList.get(0);
			HashMap<String, Integer> leftLSSA = lssaList.get(0);
			HashMap<String,Result> rightLV = lvList.get(1);
			HashMap<String, Integer> rightLSSA = lssaList.get(1);
			
			for(int i:inss)
			{		
				Instruction inst = c.GetInstruction(i);
				if(inst.GetType()!=Instruction.Type.PHI)
					break;
				
				Result r1 = inst.GetResult(1);
				Result r2 = inst.GetResult(2);

				if(r1.GetKind()==Result.Kind.VARIABLE && r1.getSSA() == leftLSSA.get(r1.GetName()))
				{
					Result r = new Result(leftLV.get(r1.GetName()));
					inst.setResult(1, r);

				}

				if(r2.GetKind()==Result.Kind.VARIABLE && r2.getSSA() == rightLSSA.get(r2.GetName()))
				{
					Result r = new Result(rightLV.get(r2.GetName()));
					inst.setResult(2, r);
				}
			}
		}*/

	}




}