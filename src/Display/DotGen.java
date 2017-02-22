package Display;

import java.io.*;
import java.util.ArrayList;

import Structures.BasicBlock;
import Structures.CFG;
import Structures.Instruction;


public class DotGen
{
	FileWriter write;
	PrintWriter printer;
	ArrayList<Integer> visited;
	StringBuffer links;
	
	public DotGen(String fName)
	{
		try {
			this.write = new FileWriter(fName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.printer = new PrintWriter(this.write);
		visited = new ArrayList<Integer>();
		links = new StringBuffer();
	}
	
	public void generate(CFG c)
	{
		printer.println("digraph structs {");
		generateBlock(c.GetRoot(),c);
		printer.println(links.toString());
		printer.println("}");
		printer.close();
	}
	
	public void generateDomTree(CFG c)
	{
		printer.println("digraph structs {");
		generateDTreeBlock(c.GetRoot(),c);
		printer.println(links.toString());
		printer.println("}");
		printer.close();
	}
	
	
	
	public void generateBlock(BasicBlock bb, CFG c)
	{
		/*InputStream in = new FileInputStream("dotOutput.gv");
		InputStreamReader reader = new InputStreamReader(in);
		this.buffer = new BufferedReader(reader);
		*/
		
		if(bb==null)
			return;
		
		if(visited.contains(bb.GetId()))
			return;
		
		visited.add(bb.GetId());//mark visited node
		
		printer.print(bb.GetId() + " [shape=record,label=\"");
		printer.print("***Block: " + bb.GetId() + " ***\\n" );
		ArrayList<Integer> list = bb.GetInstructionList();
		for(int key :list)
		{
			Instruction i = c.GetInstruction(key);
			
				printer.print(i.toString());
				printer.print("\\n");
			
			
		}
		printer.println("\"]");
		
		if(bb.GetChild(1)!=null)
		{	
			links.append(bb.GetId() + " -> " + bb.GetChild(1).GetId() +";\n");
			generateBlock(bb.GetChild(1),c);
			
		}
		
		if(bb.GetChild(2)!=null)
		{
			links.append(bb.GetId() + " -> " + bb.GetChild(2).GetId() +";\n");
			generateBlock(bb.GetChild(2),c);
		
		}
		
		
	}
	
	
	
	
	
	public void generateDTreeBlock(BasicBlock bb, CFG c)
	{
		/*InputStream in = new FileInputStream("dotOutput.gv");
		InputStreamReader reader = new InputStreamReader(in);
		this.buffer = new BufferedReader(reader);
		*/
		//System.out.println("Enter Check 1");
		if(bb==null)
			return;
		
		if(visited.contains(bb.GetId()))
			return;
		//System.out.println("Enter Check 2");
		visited.add(bb.GetId());//mark visited node
		
		printer.print(bb.GetId() + " [shape=record,label=\"");
		printer.print("***Block: " + bb.GetId() + " ***\\n" );
		ArrayList<Integer> list = bb.GetInstructionList();
		for(int key :list)
		{
			Instruction i = c.GetInstruction(key);
			printer.print(i.toString());
			printer.print("\\n");
			
		}
		printer.println("\"]");
		
		
		ArrayList <BasicBlock> domList = bb.GetDominatorChildren();
		
		//System.out.println("Block:"+bb.GetId()+"  "+domList.toString());
		
		for(BasicBlock b : domList)
		{
			if(b.GetId() != bb.GetId())
			{	
				links.append(bb.GetId() + " -> " + b.GetId() +";\n");
				generateDTreeBlock(b,c);
			}
		}
		
		
	/*	if(bb.GetChild(1)!=null)
		{	
			links.append(bb.GetId() + " -> " + bb.GetChild(1).GetId() +";\n");
			generateBlock(bb.GetChild(1),c);
			
		}
		
		if(bb.GetChild(2)!=null)
		{
			links.append(bb.GetId() + " -> " + bb.GetChild(2).GetId() +";\n");
			generateBlock(bb.GetChild(2),c);
		
		}
	*/	
		
	}
	
	
}