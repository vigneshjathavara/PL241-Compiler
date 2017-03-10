package RegisterAllocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import CodeGen.IcCodeGen;
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
		Iterator<Integer> it = list.iterator();
		while(it.hasNext())
		{
			int key = (int) it.next();
			
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

			else if(opCode >= Instruction.bne && opCode <=Instruction.bgt)
			{
				Result r = ins.getConditionInstruction();
				if(registerMap.containsKey(r.toString()))
				{
					r.setRegister(registerMap.get(r.toString()));
				}
			}

			else if(opCode == Instruction.phi)
			{
				Result r1 = ins.GetResult(1);
				Result r2 = ins.GetResult(2);
				Result r3 = ins.GetResult(3);
				
				if(registerMap.containsKey(r1.toString()))
				{
					r1.setRegister(registerMap.get(r1.toString()));
				}

				if(registerMap.containsKey(r2.toString()))
				{
					r2.setRegister(registerMap.get(r2.toString()));
				}
				
				if(registerMap.containsKey(r3.toString()))
				{
					r3.setRegister(registerMap.get(r3.toString()));
				}
				
				if(bb.getType() == BasicBlock.BlockType.WHILE_MAIN)
				{
					BasicBlock lastWhileBody = bb.getWhileBodyLast();
					BasicBlock parent = bb.getBranchParent();
					
					IcCodeGen icGen;
					icGen = new IcCodeGen();
					ArrayList<Integer> instructions = lastWhileBody.GetInstructionList();
					int branch_ins = instructions.get(instructions.size()-1);
					instructions.remove(instructions.size()-1);
					//if(r1.GetKind()==Result.Kind.VARIABLE ||r1.GetKind()==Result.Kind.INSTRUCTION)
						icGen.generate(r3, r1, Instruction.move, parent, c);
					//if(r2.GetKind()==Result.Kind.VARIABLE ||r2.GetKind()==Result.Kind.INSTRUCTION)
						icGen.generate(r3, r2, Instruction.move, lastWhileBody, c);
					instructions.add(branch_ins);
					it.remove();
				}
				
				if(bb.getType() == BasicBlock.BlockType.JOIN)
				{
					ArrayList<BasicBlock> parents = bb.getParents();
					BasicBlock elseBlk =parents.get(2);
					BasicBlock ifBlk = parents.get(1);
					System.out.println("SiZE:"+parents.size());
					System.out.println(parents.get(2).GetId());
					IcCodeGen icGen;
					icGen = new IcCodeGen();
					ArrayList<Integer> instructions = ifBlk.GetInstructionList();
					int branch_ins = instructions.get(instructions.size()-1);
					instructions.remove(instructions.size()-1);
					icGen.generate(r3, r1, Instruction.move, ifBlk, c);
					icGen.generate(r3, r2, Instruction.move, elseBlk, c);
					instructions.add(branch_ins);
					it.remove();
				}
			
				
				
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
