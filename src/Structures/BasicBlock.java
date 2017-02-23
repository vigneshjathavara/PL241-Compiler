package Structures;

import java.util.ArrayList;
import java.util.HashMap;


public class BasicBlock
{	
  public enum BlockType{ ROOT, JOIN, FUNCTION,IF,ELSE,WHILE_MAIN, WHILE_JOIN,WHILE_BODY, NORMAL}
  
  static int bbcounter=0; 

  HashMap<String,Integer> latestVariableVersion;
  HashMap<String,ArrayList<Integer>>blockArrayTable;
  ArrayList<Integer> instructionList;
  BasicBlock leftBlock;
  BasicBlock rightBlock;
  private BasicBlock branchParent;
  private BasicBlock whileBodyLast;
  ArrayList<BasicBlock> dominates;
  BasicBlock dominator;
  private ArrayList<BasicBlock> parents;
  private BlockType type;
  int blockId;
  
  public BasicBlock()
  {
	  
  }
  
  public BasicBlock(BlockType t, CFG c)
  {
	  latestVariableVersion = new HashMap<String,Integer>();
	  blockArrayTable = new HashMap<String,ArrayList<Integer>>();
	  instructionList = new ArrayList<Integer>();
	  this.leftBlock = null;
	  this.rightBlock = null;
	  dominates = new ArrayList<BasicBlock>();
	  dominator = null;
	  this.setParents(new ArrayList<BasicBlock>());
	  blockId = bbcounter++;
	  this.setType(t);
	  c.AddBasicBlock(blockId, this);
  }
  
  public BasicBlock(BlockType t, HashMap<String,Integer> latest, HashMap<String,ArrayList<Integer>> array, BasicBlock parent,CFG c)
  {
	  this.latestVariableVersion = new HashMap<String,Integer>();
	  for(String key : latest.keySet())
	  {
		  this.latestVariableVersion.put(key, latest.get(key));
	  }
	  this.blockArrayTable = new HashMap<String,ArrayList<Integer>>();
	  for(String key : array.keySet())
	  {	  
		  this.blockArrayTable.put(key, array.get(key));
	  }
	  this.setParents(new ArrayList<BasicBlock>());
	  this.getParents().add(parent);
	  //this.leftBlock = join;
	  instructionList =  new ArrayList<Integer>();
	  dominates = new ArrayList<BasicBlock>();
	  dominator = null;
	  blockId = bbcounter++;
	  this.setType(t);
	  if(c!=null)
	  {	  
	  c.AddBasicBlock(blockId, this);
	  }
	  
  }
  
  public void SetDominator(BasicBlock d)
  {
	  this.dominator =d;
  }
  
  public void AddDominatorChild(BasicBlock bb)
  {
	  this.dominates.add(bb);
  }
  
  public BasicBlock GetDominator()
  {
	  return this.dominator;
  }
  
  public ArrayList<BasicBlock> GetDominatorChildren()
  {
	  return this.dominates;
  }
  
  public void AddParent(BasicBlock p)
  {
	  this.getParents().add(p);
  }
  
  public void AddInstruction(int i)
  {
	  this.instructionList.add(i);	  
  }
  
  public void AddInstructionFront(int i)
  {
	  this.instructionList.add(0, i);	  
  }
  
  
  public ArrayList<Integer> GetInstructionList()
  {
	  return this.instructionList;
  }
  
  
  public HashMap<String,Integer> GetLatestVariableVersion()
  {
	  return this.latestVariableVersion;
  }
  
 
  public int GetLastestSSAOf(String name)
  {
	  
		for(String key : this.latestVariableVersion.keySet()) {
			if(key.equals(name)) {
				
				return latestVariableVersion.get(key);
			}
		}
		
	 System.out.println("Variable not found to GET latest SSA in Block:"+ this.blockId +" Variable:"+ name);
	 return -1;
	  
  }
  
  
  public void AddNewVariable(String name)
	{
		this.latestVariableVersion.put(name, -1);
	}
  
  
  
  
  
  
	
	public void AddNewSSA(String name, int i)
	{
		boolean flag=false;
		for(String key : this.latestVariableVersion.keySet()) {
			if(key.equals(name)) {
				flag=true;
				//System.out.println("update ssa of" + key);
				latestVariableVersion.put(name, i);
				break;
			}
		}
		if(flag==false)
		{
			System.out.println("Variable not found to Update SSA in Block:"+ this.blockId +" Variable:"+ name+" SSA:"+i);
		}
	}
	
	
	
	public int GetId()
	{
		return this.blockId;
	}
	
	public BasicBlock GetChild(int child)
	{
		if( child == 1)
			return this.leftBlock;
		else
			return this.rightBlock;
	}
	
	public HashMap<String,ArrayList<Integer>> GetArrayTable()
	{
		return this.blockArrayTable;
	}
	
	public void SetLeft(BasicBlock l)
	{
		this.leftBlock = l;
	}
	
	public void SetRight(BasicBlock r)
	{
		this.rightBlock = r;
	}
	
	public void AddNewArray(String name, ArrayList<Integer> dim)
	{
		this.blockArrayTable.put(name, dim);
	}

	public BasicBlock getBranchParent() {
		return branchParent;
	}

	public void setBranchParent(BasicBlock branchParent) {
		this.branchParent = branchParent;
	}

	public BasicBlock getWhileBodyLast() {
		return whileBodyLast;
	}

	public void setWhileBodyLast(BasicBlock whileBodyLast) {
		this.whileBodyLast = whileBodyLast;
	}

	public BlockType getType() {
		return type;
	}

	public void setType(BlockType type) {
		this.type = type;
	}

	public ArrayList<BasicBlock> getParents() {
		return parents;
	}

	public void setParents(ArrayList<BasicBlock> parents) {
		this.parents = parents;
	}
  
  
}
