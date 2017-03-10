package Structures;

import java.util.ArrayList;
import java.util.HashMap;

public class DominatorTree
{
	
	BasicBlock root;
	
	public DominatorTree(CFG c)
	{
		root =c.GetRoot();
	}
	
	public BasicBlock GetRoot()
	{
		return this.root;
	}
	
	
	public void CreateDominatorTree(CFG c)
	{
		HashMap <Integer,BasicBlock> blockList = c.GetBasicBlockList();
		
		HashMap <Integer, ArrayList<Integer>> reachableOnlyMap = new HashMap<Integer,ArrayList<Integer>>();
		
		HashMap <Integer, ArrayList<Integer>> domMap = new HashMap<Integer,ArrayList<Integer>>();
		
		
		for(int key : blockList.keySet())
		{
			ArrayList<Integer> reachableBlocks = new ArrayList<Integer>();
			ArrayList<Integer> visited = new ArrayList<Integer>();
			
			for(int k :blockList.keySet())
			{
				reachableBlocks.add(k);
			}
			
			visited.add(key);
			
			FindUnreachableBlocks(c.GetRoot(),reachableBlocks,visited);
			//System.out.println("\"" + key + "\"" + reachableBlocks.toString());
			reachableOnlyMap.put(key, reachableBlocks);
			
			ArrayList<Integer> reachableOnly = new ArrayList<Integer>();
			domMap.put(key, reachableOnly);
		}
		
		
		
		for(int key : reachableOnlyMap.keySet())
		{
			ArrayList<Integer> reachableOnly = reachableOnlyMap.get(key);
			
			for(int k :reachableOnly)
			{
				domMap.get(k).add(key);
			}
		}
		
		
		
		//System.out.println(domMap.toString());
		
		ArrayList<Integer> added = new ArrayList<Integer>();
		while(domMap.size()!=0)
		{	
			int blockId=-1;
			for(int key : domMap.keySet())
			{
				ArrayList<Integer> children = domMap.get(key);
				if(children.size()==1)
				{
					blockId = key;
					BasicBlock bb = c.GetBasicBlock(key);
					bb.AddDominatorChild(c.GetBasicBlock(children.get(0)));
					//System.out.print(bb.GetId() + "->");
					//domMap.remove(key);
					for(int k : domMap.keySet())
					{
						/*
						for(int i :childList)
						{
							if(i== children.get(0))
							{
								childList.remove(i);
							}
						}
						*/
						ArrayList<Integer> childList = domMap.get(k);
						//System.out.println("1st  " + childList.toString());
						childList.remove((Object)key);
						//System.out.println("2nd  " + childList.toString());
						if(childList.size()==1)
						{
							if(!added.contains(childList.get(0)))
							{
							added.add(childList.get(0));	
							//System.out.println(childList.get(0));
							bb.AddDominatorChild(c.GetBasicBlock(childList.get(0)));
							}
						}
						
					}
					break;
				}
			}
			if(blockId != -1)
				domMap.remove(blockId);
			
		}
		
		
	}
	
	
	void FindUnreachableBlocks(BasicBlock root, ArrayList<Integer> reachableBlocks, ArrayList<Integer> visited )
	{
		if(root == null || visited.contains(root.GetId()))
			return;
		
		reachableBlocks.remove((Object)root.GetId());
		visited.add(root.GetId());
		
		FindUnreachableBlocks(root.GetChild(1),reachableBlocks,visited);
		FindUnreachableBlocks(root.GetChild(2),reachableBlocks,visited);
		
	}
}

