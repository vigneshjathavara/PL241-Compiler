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
		Analyze(c.getTail(),this.liveSet,c,null,false,null,false);
	}
	
	public InterferenceGraph getiG() {
		return iG;
	}



	public void setiG(InterferenceGraph iG) {
		this.iG = iG;
	}
	
	public ArrayList<String> Analyze(BasicBlock b, ArrayList<String> liveSet, CFG c, BasicBlock stop, boolean flag,BasicBlock retBlk, boolean flag2)
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
			String res = "(" +ins.GetId() +")";
			liveSet.remove(res);
			if(ins.GetOpCode()>=Instruction.add && ins.GetOpCode()<=Instruction.store)
			{
				
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				if(left.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(left.toString()) && left.GetKind()!=Result.Kind.FRAME_POINTER && left.GetKind()!=Result.Kind.BASE_ADDRESS)
				{
					liveSet.add(left.toString());
				}
				
				if(right.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(right.toString()) && left.GetKind()!=Result.Kind.FRAME_POINTER && left.GetKind()!=Result.Kind.BASE_ADDRESS)
				{
					liveSet.add(right.toString());
				}
				
				iG.AddToGraph(liveSet);
			}
			
		/*	if(ins.GetOpCode()==Instruction.cmp)
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
			}*/
			
			if(ins.GetOpCode()==Instruction.phi)
			{
				liveSet.remove(ins.GetResult(3).toString());
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				iG.AddToGraph(liveSet);
				leftPhi.add(left.toString());
				rightPhi.add(right.toString());								
			}	
			
		/*	if(ins.GetOpCode()==Instruction.store)
			{
				
			}
		*/	
			if(ins.GetOpCode()==Instruction.load)
			{
				Result left = ins.GetResult(1);
				if(left.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(left.toString()) && left.GetKind()!=Result.Kind.FRAME_POINTER && left.GetKind()!=Result.Kind.BASE_ADDRESS)
				{
					liveSet.add(left.toString());
				}
				iG.AddToGraph(liveSet);
			}
			
			if(ins.GetOpCode()==Instruction.push)
			{
				Result pp = ins.getpp();
				if(pp.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(pp.toString()))
				{
					liveSet.add(pp.toString());
				}
				
				iG.AddToGraph(liveSet);
			}
			
			if(ins.GetOpCode()==Instruction.write)
			{
				Result w = ins.getW();
				if(w.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(w.toString()))
				{
					liveSet.add(w.toString());
				}
				
				iG.AddToGraph(liveSet);
			}
			
		/*	if(ins.GetOpCode()==Instruction.adda)
			{
				Result left = ins.GetResult(1);
				Result right = ins.GetResult(2);
				if(left.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(left.toString()) && left.GetKind()!=Result.Kind.FRAME_POINTER && left.GetKind()!=Result.Kind.BASE_ADDRESS)
				{
					liveSet.add(left.toString());
				}
				
				if(right.GetKind()!=Result.Kind.CONSTANT && !liveSet.contains(right.toString()) && left.GetKind()!=Result.Kind.FRAME_POINTER && left.GetKind()!=Result.Kind.BASE_ADDRESS)
				{
					liveSet.add(right.toString());
				}
				
				iG.AddToGraph(liveSet);
			}*/
			
		}
		
		
		if(flag2 && b==retBlk)
			return liveSet;
		
		if(b.getType() == BasicBlock.BlockType.JOIN || b.getType() == BasicBlock.BlockType.WHILE_MAIN )
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
						for(String s:leftPhi)
						{
							ls.add(s);
						}
						iG.AddToGraph(ls);
					}
					
					if(cnt==2)
					{
						for(String s:rightPhi)
						{
							ls.add(s);
						}
						iG.AddToGraph(ls);
					}
					cnt++;
					ArrayList<String> temp = Analyze(bb,ls,c,b.getBranchParent(),true,null,false);
					for(String s:temp)
					{
						if(!lsu.contains(s))
							lsu.add(s);
					}
					iG.AddToGraph(lsu);
				}
				
				return Analyze(b.getBranchParent(),lsu,c,null,false,null,false);
				
			}
			
			else
			{
				ArrayList<BasicBlock> parents = b.getParents();
				
				ArrayList<String> ls = new ArrayList<String>();
				for(String str:liveSet)
				{
					ls.add(str);
				}
				for(String str:rightPhi)
				{
					ls.add(str);
				}
				iG.AddToGraph(ls);
				ArrayList<String> temp = Analyze(b.getWhileBodyLast(), ls, c, b.getBranchParent(),true,b,true);
				
				for(BasicBlock bb: parents)
				{
					ls = new ArrayList<String>();
					for(String str:temp)
					{
						ls.add(str);
					}
					for(String str:leftPhi)
					{
						ls.add(str);
					}
					iG.AddToGraph(ls);
					return Analyze(bb, ls, c, null,false,null,false);
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
				
				return Analyze(bb,ls,c,stop,flag,retBlk,flag2);
				/*
				if(flag != true && flag2 !=true)
				{
					return Analyze(bb,ls,c,null,false,null,false);
				}
				
				else if(flag!=true && flag2==true)
				{
					return Analyze(bb,ls,c,stop,false,retBlk,true);
				}
				
				else if(flag==true && flag2!=true)
				{
					return Analyze(bb,ls,c,stop,true,retBlk,true);
				}*/
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




