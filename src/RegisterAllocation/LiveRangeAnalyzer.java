package RegisterAllocation;


import Structures.CFG;
import Structures.Instruction;
import Structures.InterferenceGraph;
import Structures.Result;
import Structures.BasicBlock;

import java.util.ArrayList;
import java.util.ListIterator;


public class LiveRangeAnalyzer {
	
	
	InterferenceGraph iG; 
	ArrayList<String> liveSet;
	
	public LiveRangeAnalyzer(CFG c)
	{
		iG = new InterferenceGraph();
		liveSet = new ArrayList<String>();
		Analyze(c.getTail(),this.liveSet,c,null,false);
	}
	
	
	
	public ArrayList<String> Analyze(BasicBlock b, ArrayList<String> liveSet, CFG c, BasicBlock stop, boolean flag)
	{
		if(flag && b==stop)
			return liveSet;
		
		if(b == null)
			return liveSet;
		
		ArrayList<Integer> iList = b.GetInstructionList();
		ListIterator<Integer> li = iList.listIterator(iList.size());
		ArrayList<String> leftPhi = new ArrayList<String>();
		ArrayList<String> rightPhi = new ArrayList<String>();
		while(li.hasPrevious())
		{
			Instruction ins = c.GetInstruction(li.previous());
			if(ins.GetOpCode()>=Instruction.add && ins.GetOpCode()<=Instruction.div)
			{
				String res = "(" +ins.GetId() +")";
				liveSet.remove(res);
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				if(left.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(left.toString()))
				{
					liveSet.add(left.toString());
				}
				
				if(right.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(right.toString()))
				{
					liveSet.add(right.toString());
				}
				
				iG.AddToGraph(liveSet);
			}
			
			if(ins.GetOpCode()==Instruction.cmp)
			{
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				if(left.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(left.toString()))
				{
					liveSet.add(left.toString());
				}
				
				if(right.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(right.toString()))
				{
					liveSet.add(right.toString());
				}
				
				iG.AddToGraph(liveSet);
			}
			
			if(ins.GetOpCode()==Instruction.phi)
			{
				liveSet.remove(ins.GetResult(3).toString());
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				iG.AddToGraph(liveSet);
				leftPhi.add(left.toString());
				rightPhi.add(right.toString());								
			}			
		}
		
		if(b.getType() == BasicBlock.BlockType.JOIN || b.getType() == BasicBlock.BlockType.WHILE_JOIN )
		{
			if(b.getType() == BasicBlock.BlockType.JOIN)
			{
				ArrayList<BasicBlock> parents = b.getParents();
				int cnt =1;
				ArrayList<String> lsu = new ArrayList<String>();
				for(BasicBlock bb:parents)
				{
					ArrayList<String> ls = new ArrayList<String>();
					for(String str:liveSet)
					{
						ls.add(str);
					}
					if(cnt==1)
					{
						for(String s:rightPhi)
						{
							ls.remove(s);
						}
					}
					
					if(cnt==2)
					{
						for(String s:leftPhi)
						{
							ls.remove(s);
						}
					}
					cnt++;
					ArrayList<String> temp = Analyze(bb,ls,c,b.getBranchParent(),true);
					for(String s:temp)
					{
						if(!lsu.contains(s))
							lsu.add(s);
					}
				}
				
				return Analyze(b.getBranchParent(),lsu,c,null,false);
				
			}
			
			else
			{
				ArrayList<BasicBlock> parents = b.getParents();
				for(BasicBlock bb: parents)
				{
					ArrayList<String> ls = new ArrayList<String>();
					for(String str:liveSet)
					{
						ls.add(str);
					}
					return Analyze(bb.getWhileBodyLast(), ls, c, null,false);
				}
			}
		}
		
		else
		{
			ArrayList<BasicBlock> parents = b.getParents();
			for(BasicBlock bb:parents)
			{
				ArrayList<String> ls = new ArrayList<String>();
				for(String str:liveSet)
				{
					ls.add(str);
				}
				return Analyze(bb,ls,c,null,false);
			}
			//return liveSet;
			
		}
		return liveSet;
		
	}
	
	public void PrintInterferenceGraph()
	{
		System.out.println(iG);
	}
	
}




