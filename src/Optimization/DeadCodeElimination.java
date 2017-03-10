package Optimization;

import java.util.ArrayList;
import java.util.Iterator;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;

public class DeadCodeElimination {

	ArrayList<Integer> visited;

	public DeadCodeElimination()
	{
		visited = new ArrayList<Integer>();
	}

	
	
	public void eliminate(BasicBlock bb, CFG c)
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
			
			if(opCode >= Instruction.add && opCode <= Instruction.div )
			{
				if(ins.getRegister()==-1)
				{
					it.remove();
				}
				
			}
		
			if(opCode == Instruction.move )
			{
				
				if(ins.GetResult(2).getRegister() ==-1)
				{
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
				

				eliminate(b, c);
			}
		}
		
	}
}
