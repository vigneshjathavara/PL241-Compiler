package Optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;
import Structures.Result;

public class CSEElimination {


	public void CSEOptimise(CFG c)
	{

		HashMap<Integer, Instruction> instructionMap = new HashMap<Integer,Instruction>();
		HashMap<Integer, Integer> instructionReplaceMap = new HashMap<Integer,Integer>();

		ArrayList<Integer> instructions= c.GetRoot().GetInstructionList();
		Iterator<Integer> it = instructions.iterator();
		//System.out.println("***************************CSE*********************");
		while(it.hasNext())
		{
			int key = (int) it.next();
			boolean flag = false;
			Instruction insE = c.GetInstruction(key);

			if(insE.GetOpCode() == Instruction.marker)
			{	
				while(insE.GetOpCode()==Instruction.marker)
				{
					int ignoreInsNo =  (int) it.next();
					Instruction ignoreIns = c.GetInstruction(ignoreInsNo);
					//System.out.println(ignoreIns.toString());
					while(ignoreIns.GetOpCode()!= Instruction.load && ignoreIns.GetOpCode()!= Instruction.store)
					{
						ignoreInsNo =  (int) it.next();
						ignoreIns = c.GetInstruction(ignoreInsNo);
					}
					key = (int) it.next();
					insE = c.GetInstruction(key);
				}
			}
			
			
			if(insE.GetOpCode() == Instruction.write)
			{
				Result w = insE.getW();
				if(w.GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(w.GetInstructionId()))
				{
					insE.setW(new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(w.GetInstructionId())));
				}
			}
			
			
			if(insE.GetOpCode() == Instruction.push)
			{
				Result pp = insE.getpp();
				if(pp.GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(pp.GetInstructionId()))
				{
					insE.setW(new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(pp.GetInstructionId())));
				}
			}

			if(insE.GetOpCode() >= Instruction.add && insE.GetOpCode() <=Instruction.cmp)
			{

				if(insE.GetResult(1).GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(insE.GetResult(1).GetInstructionId()))
				{
					insE.setResult(1, new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(insE.GetResult(1).GetInstructionId())));
				}

				if(insE.GetResult(2).GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(insE.GetResult(2).GetInstructionId()))
				{
					insE.setResult(2, new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(insE.GetResult(2).GetInstructionId())));
				}



				for(int i : instructionMap.keySet())
				{
					Instruction ins = instructionMap.get(i);
					//System.out.println("E:"+key+" I:"+i);
					if(insE.GetOpCode() >= Instruction.add && insE.GetOpCode() <=Instruction.div)
					{
						//System.out.println("opCode safe");
						if(insE.GetOpCode()==ins.GetOpCode())
						{
							//System.out.println("opCode same");

							if((insE.GetResult(1).toString().compareTo(ins.GetResult(1).toString())==0 && insE.GetResult(1).toString().compareTo(ins.GetResult(1).toString())==0)||(insE.GetResult(1).toString().compareTo(ins.GetResult(2).toString())==0 && insE.GetResult(2).toString().compareTo(ins.GetResult(1).toString())==0))
							{
								//System.out.println("Entered");
								//System.out.println(ins.toString());
								//System.out.println(insE.toString());
								instructionReplaceMap.put(insE.GetId(), ins.GetId());
								it.remove();
								flag =true;
							}
						}
					}

				}
				if(flag == false)
				{
					instructionMap.put(key, insE);
				}

			}
		}
		ArrayList <BasicBlock> domList = c.GetRoot().GetDominatorChildren();

		//System.out.println("Block:"+bb.GetId()+"  "+domList.toString());

		for(BasicBlock b : domList)
		{
			if(b.GetId() != c.GetRoot().GetId())
			{
				HashMap<Integer, Instruction> instructionMap1 = new HashMap<Integer,Instruction>();
				HashMap<Integer, Integer> instructionReplaceMap1 = new HashMap<Integer,Integer>();

				for(Integer key : instructionMap.keySet())
				{
					instructionMap1.put(key, instructionMap.get(key));

				}

				for(Integer key : instructionReplaceMap.keySet())
				{
					instructionReplaceMap1.put(key, instructionReplaceMap.get(key));

				}

				optimise(b, instructionMap1, instructionReplaceMap1, c);
			}
		}



	}


	public void optimise(BasicBlock root, HashMap<Integer,Instruction> instructionMap, HashMap<Integer, Integer> instructionReplaceMap, CFG c)
	{

		if(root == null)
			return;




		ArrayList<Integer> instructions= root.GetInstructionList();
		Iterator<Integer> it = instructions.iterator();
		while(it.hasNext())
		{
			int key = (int) it.next();
			boolean flag = false;
			Instruction insE = c.GetInstruction(key);

			if(insE.GetOpCode() >= Instruction.add && insE.GetOpCode() <=Instruction.cmp)
			{
				if(insE.GetResult(1).GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(insE.GetResult(1).GetInstructionId()))
				{
					insE.setResult(1, new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(insE.GetResult(1).GetInstructionId())));
				}

				if(insE.GetResult(2).GetKind()==Result.Kind.INSTRUCTION && instructionReplaceMap.containsKey(insE.GetResult(2).GetInstructionId()))
				{
					insE.setResult(2, new Result(Result.Kind.INSTRUCTION, instructionReplaceMap.get(insE.GetResult(2).GetInstructionId())));
				}



				for(int i : instructionMap.keySet())
				{
					Instruction ins = instructionMap.get(i);
					if(insE.GetOpCode() >= Instruction.add && insE.GetOpCode() <=Instruction.div)
					{
						if(insE.GetOpCode()==ins.GetOpCode())
						{
							if((insE.GetResult(1)==ins.GetResult(1) && insE.GetResult(2)==ins.GetResult(2))||(insE.GetResult(1)==ins.GetResult(2) && insE.GetResult(2)==ins.GetResult(1)))
							{
								instructionReplaceMap.put(insE.GetId(), ins.GetId());
								it.remove();
								flag =true;
							}
						}
					}

				}
				if(flag == false)
				{
					instructionMap.put(key, insE);
				}

			}
		}
		ArrayList <BasicBlock> domList = root.GetDominatorChildren();

		//System.out.println("Block:"+bb.GetId()+"  "+domList.toString());

		for(BasicBlock b : domList)
		{
			if(b.GetId() != root.GetId())
			{
				HashMap<Integer, Instruction> instructionMap1 = new HashMap<Integer,Instruction>();
				HashMap<Integer, Integer> instructionReplaceMap1 = new HashMap<Integer,Integer>();

				for(Integer key : instructionMap.keySet())
				{
					instructionMap1.put(key, instructionMap.get(key));

				}

				for(Integer key : instructionReplaceMap.keySet())
				{
					instructionReplaceMap1.put(key, instructionReplaceMap.get(key));

				}

				optimise(b, instructionMap1, instructionReplaceMap1, c);
			}
		}





	}


}
