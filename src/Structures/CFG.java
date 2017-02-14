package Structures;

import java.util.ArrayList;
import java.util.HashMap;


public class CFG
{

	HashMap<String,ArrayList<Integer>> variableTable;
	HashMap<String,ArrayList<Integer>>arrayTable;
	BasicBlock root;
	HashMap<Integer,BasicBlock> blockList;
	HashMap<Integer,CFG> functionList;
	HashMap<Integer,Instruction> instructionList;


	public CFG()
	{
		variableTable = new HashMap<String,ArrayList<Integer>>();
		arrayTable = new HashMap<String,ArrayList<Integer>>();
		blockList = new HashMap<Integer,BasicBlock>();
		functionList = new HashMap<Integer,CFG>();
		instructionList = new HashMap<Integer,Instruction>();
		root = new BasicBlock(BasicBlock.BlockType.ROOT, this);
	}

	public HashMap<Integer, BasicBlock> GetBasicBlockList()
	{
		return blockList;
	}
	

	public void AddFunction(int id, CFG func)
	{
		functionList.put(id, func);		
	}

	public CFG GetFunction(int funcId)
	{
		return functionList.get(funcId);
	}

	public void AddBasicBlock(int id,BasicBlock bb)
	{
		blockList.put(id, bb);
	}

	public BasicBlock GetBasicBlock(int blockId)
	{
		return blockList.get(blockId);
	}

	public void AddInstruction(int id, Instruction i)
	{
		System.out.println("Adding Instruction to CFG : " + i.GetId() + "->" + i.toString());
		instructionList.put(id,i);
	}

	public Instruction GetInstruction(int instructionId)
	{
		return instructionList.get(instructionId);
	}	

	public BasicBlock GetRoot()
	{
		return this.root;	
	}

	public void AddNewVariable(String name)
	{
		ArrayList<Integer> a = new ArrayList<Integer>();
		//a.add(-1);
		this.variableTable.put(name, a);
	}

	public int AddNewSSA(String name)
	{

		for(String key : variableTable.keySet()) {
			if(key.equals(name)) {

				ArrayList<Integer> a = variableTable.get(key);
				int ssa = a.size();
				a.add(ssa);
				return ssa;
			}
		}

		System.out.println("Variable not found to Update SSA");
		return -1;
	}

	public int GetLatestSSA(String name)
	{

		for(String key : variableTable.keySet()) {
			if(key.equals(name)) {
				ArrayList<Integer> a = variableTable.get(key);
				return a.get(a.size()-1);
			}
		}

		System.out.println("Variable not found to Update SSA");
		return -1;


	}

	public ArrayList<Integer> GetArrayDims(String name)
	{
		for( String key : arrayTable.keySet())
		{
			if(key.equals(name))
			{
				return arrayTable.get(key);
			}
		}

		return null;

	}


	public void AddNewArray(String name, ArrayList<Integer> dim)
	{
		this.arrayTable.put(name, dim);
	}

	public void FixUp(int instructionId, int trgtId)
	{
		for(int key : instructionList.keySet()) {
			if(key == instructionId) {
				instructionList.get(key).FixUp(trgtId);
			}		
		}
	}

}