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

		HashMap<String,Result> lV = new HashMap<String,Result>();
		HashMap<String, Integer> lSSA = new HashMap<String,Integer>();

		for(String key : c.GetRoot().GetLatestVariableVersion().keySet())
		{
			lV.put(key, new Result(Result.Kind.CONSTANT,0));
			lSSA.put(key, -1);
		}

		optimise(c.GetRoot(), lV, lSSA, c);
	}




	public void optimise(BasicBlock root, HashMap<String,Result> latestValue, HashMap<String, Integer> latestSSA, CFG c)
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


				if(r1.GetKind()==Result.Kind.VARIABLE && r1.getSSA() == latestSSA.get(r1.GetName()))
				{
					Result r = new Result(latestValue.get(r1.GetName()));
					ins.setResult(1, r);//1 is for left operand
				}

				if(r2.GetKind()==Result.Kind.VARIABLE && r2.getSSA() == latestSSA.get(r2.GetName()))
				{
					Result r = new Result(latestValue.get(r2.GetName()));
					ins.setResult(2, r);//2 is for right operand
				}
				
				/*if(opCode >= Instruction.add && opCode <= Instruction.div && r2.GetKind()==Result.Kind.CONSTANT && r1.GetKind()==Result.Kind.CONSTANT)
				{
					
				}*/
			}

			else if(opCode == Instruction.store)
			{
				Result r1 = ins.GetResult(1);

				if(r1.GetKind()==Result.Kind.VARIABLE && r1.getSSA() == latestSSA.get(r1.GetName()))
				{
					Result r = new Result(latestValue.get(r1.GetName()));
					ins.setResult(1, r);
				}
			}

			else if(opCode == Instruction.phi)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				Result r3 = ins.GetResult(3);

				if(r1.GetKind()==Result.Kind.VARIABLE && r1.getSSA() == latestSSA.get(r1.GetName()))
				{
					Result r = new Result(latestValue.get(r1.GetName()));
					ins.setResult(1, r);

				}

				if(r2.GetKind()==Result.Kind.VARIABLE && r2.getSSA() == latestSSA.get(r2.GetName()))
				{
					Result r = new Result(latestValue.get(r2.GetName()));
					ins.setResult(2, r);
				}

				if(r3.GetKind()==Result.Kind.VARIABLE)
				{
					latestSSA.put(r3.GetName(), r3.getSSA());
					latestValue.put(r3.GetName(), r3);
				}



			}

			else if(opCode == Instruction.move)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);

				if(r1.GetKind()==Result.Kind.VARIABLE && r1.getSSA() == latestSSA.get(r1.GetName()))
				{
					Result r = new Result(latestValue.get(r1.GetName()));
					ins.setResult(1, r);
				}

				if(r2.GetKind()==Result.Kind.VARIABLE)
				{
					latestSSA.put(r2.GetName(), r2.getSSA());
					latestValue.put(r2.GetName(), r1);
				}
				
				it.remove();
				
			}

		}

		ArrayList <BasicBlock> domList = root.GetDominatorChildren();

		//System.out.println("Block:"+bb.GetId()+"  "+domList.toString());

		for(BasicBlock b : domList)
		{
			if(b.GetId() != root.GetId())
			{
				HashMap<String,Result> lV = new HashMap<String,Result>();
				HashMap<String, Integer> lSSA = new HashMap<String,Integer>();

				for(String key : latestValue.keySet())
				{
					lV.put(key, latestValue.get(key));
					lSSA.put(key, latestSSA.get(key));
				}

				optimise(b, lV, lSSA, c);
			}
		}

	}




}