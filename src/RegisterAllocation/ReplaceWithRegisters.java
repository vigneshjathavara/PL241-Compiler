package RegisterAllocation;

import java.util.ArrayList;
import java.util.HashMap;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;

public class ReplaceWithRegisters {

	ArrayList<Integer> visited;

	public ReplaceWithRegisters()
	{
		visited = new ArrayList<Integer>();
	}

	public void replace(BasicBlock bb, CFG c,HashMap<String,Integer> registerMap)
	{
		if(bb==null)
			return;

		if(visited.contains(bb.GetId()))
			return;

		visited.add(bb.GetId());//mark visited node

		ArrayList<Integer> list = bb.GetInstructionList();
		for(int key :list)
		{
			Instruction ins = c.GetInstruction(key);
			int opCode = ins.GetOpCode();

			if(registerMap.containsKey("(" +ins.GetId() +")"))
				ins.setRegister(registerMap.get("(" +ins.GetId() +")"));


			if(opCode >= Instruction.add && opCode <= Instruction.store )
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);

				if(r1 !=null && registerMap.containsKey(r1.toString()))
				{
					r1.setRegister(registerMap.get(r1.toString()));
				}

				if(r2 != null && registerMap.containsKey(r2.toString()))
				{
					r2.setRegister(registerMap.get(r2.toString()));
				}
			}

			else if(opCode == Instruction.load)
			{
				Result r1 = ins.GetResult(1);

				if(registerMap.containsKey(r1.toString()))
				{
					r1.setRegister(registerMap.get(r1.toString()));
				}
			}

			else if(opCode == Instruction.write)
			{
				Result w = ins.getW();

				if(registerMap.containsKey(w.toString()))
				{
					w.setRegister(registerMap.get(w.toString()));
				}
			}

			else if(opCode == Instruction.push)
			{
				Result pp = ins.getpp();

				if(registerMap.containsKey(pp.toString()))
				{
					pp.setRegister(registerMap.get(pp.toString()));
				}
			}


			else if(opCode == Instruction.phi)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				Result r3 = ins.GetResult(3);
				/*	
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

				}*/
			}

		}
		
		ArrayList <BasicBlock> domList = bb.GetDominatorChildren();
		
		for(BasicBlock b : domList)
		{
			if(b.GetId() != bb.GetId())
			{
				//HashMap<String,Result> lV = new HashMap<String,Result>();
				//HashMap<String, Integer> lSSA = new HashMap<String,Integer>();
				

				replace(b, c, registerMap);
			}
		}
	}


}
