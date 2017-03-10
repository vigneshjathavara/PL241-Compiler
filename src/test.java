import Display.DotGen;
import Optimization.CSEElimination;
import Optimization.CopyPropagation;
import Optimization.DeadCodeElimination;
import Parser.Parser;
import RegisterAllocation.GraphColoring;
import RegisterAllocation.LiveRangeAnalyzer;
import RegisterAllocation.ReplaceWithRegisters;
import Structures.CFG;
import Structures.DominatorTree;
import Structures.InterferenceGraph;
import Structures.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CodeGen.CodeGenerator;
import DLX.dlx;

class test
{

 public static void main(String arg[])
 {
   Parser p = new Parser("src/Parser/input_1.txt");
   //Parse the file for syntax and also prepare all structures
   //Structures are:
   try{
        p.computation();
      }

    catch(RuntimeException e){
	System.out.println(e);
	return;
	}
   
   
   DotGen cfgg_b4cp = new DotGen("src/Display/CFG.gv");
   cfgg_b4cp.generate(p.GetCFG());
   System.out.println("CFG DONE!!");
   
   
   //Prepare the dominator tree.
   DominatorTree dt = new DominatorTree(p.GetCFG());
   dt.CreateDominatorTree(p.GetCFG());
   
   System.out.println("DOM TREE DONE!!");
   
   
   DotGen dtg_b4cp = new DotGen("src/Display/DomTree.gv");
   dtg_b4cp.generateDomTree(p.GetCFG());
   
   
   
   //Copy Propagation is performed on the 
   CopyPropagation cp = new CopyPropagation();
   cp.CPOptimise(p.GetCFG());
   
   DotGen cfgg_b4cse = new DotGen("src/Display/CFG_afterCP.gv");
   cfgg_b4cse.generate(p.GetCFG());
   
   CSEElimination cse = new CSEElimination();
   cse.CSEOptimise(p.GetCFG());
   
   //Compute the visual graph of CFG
   DotGen cfgg = new DotGen("src/Display/CFG_afterCSE.gv");
   cfgg.generate(p.GetCFG());
   
   //compute visual graph of Dominator Tree
   DotGen dtg = new DotGen("src/Display/DomTree_afterCSE.gv");
   dtg.generateDomTree(p.GetCFG());   
   
   LiveRangeAnalyzer lra = new LiveRangeAnalyzer(p.GetCFG());
   System.out.println("The Interference Graph:");
   lra.PrintInterferenceGraph();
   
   
   GraphColoring gC = new GraphColoring();
   gC.GreedyColoring(lra.getiG());
   gC.printRegisters();
   
   ReplaceWithRegisters rwr = new ReplaceWithRegisters();
   rwr.replace(p.GetCFG().GetRoot(), p.GetCFG(), gC.getRegisterMap());
   
   
   System.out.println("creating CFGWR");
   DotGen CFGWR = new DotGen("src/Display/CFG_afterRegisterAllocation.gv");
   CFGWR.generateWithRegister(p.GetCFG()); 
   
   DeadCodeElimination dCE = new DeadCodeElimination();
   dCE.eliminate(p.GetCFG().GetRoot(), p.GetCFG());
   
   DotGen CFGWR_DcE = new DotGen("src/Display/CFG_afterDeadCodeElimination.gv");
   CFGWR_DcE.generateWithRegister(p.GetCFG()); 
   
   CodeGenerator cG = new CodeGenerator(p.GetCFG());
   cG.generateCode(p.GetCFG().GetRoot(),null,false,null,false);
   cG.FixUpArrays();
   List<Integer> programCodes = cG.getProgramCodes();
   int codes[] = new int[programCodes.size()];
   System.out.println("Program  Codes");
   for(int i=0;i<programCodes.size();i++)
   {
	   codes[i]=programCodes.get(i);
	   System.out.println(codes[i]);
   }
   
  
   System.out.println("Test:");
   dlx d = new dlx();
   dlx.load(codes);
  try{
   dlx.execute();
  }
   catch(IOException e){
		System.out.println(e);
		return;
		}
   System.out.println(":Test");
   HashMap<String, CFG> functions = p.GetCFG().getFunctionList();
   for(String fKey:functions.keySet())
   {
	   CFG c = functions.get(fKey);
	   
	   DotGen fcfgg_b4cp = new DotGen("src/Display/"+ fKey +"_b4opt.gv");
	   fcfgg_b4cp.generate(c);
	   
	   DominatorTree fDT = new DominatorTree(c);
	   fDT.CreateDominatorTree(c);
	   
	   CopyPropagation fcp = new CopyPropagation();
	   fcp.CPOptimise(c);
	   
	   DotGen fcfgg_aftercp = new DotGen("src/Display/"+ fKey +"_aftercp.gv");
	   fcfgg_aftercp.generate(c);
	   
	   CSEElimination fcse = new CSEElimination();
	   fcse.CSEOptimise(c);
	   
	   LiveRangeAnalyzer flra = new LiveRangeAnalyzer(c);
	   System.out.println("The Interference Graph:");
	   flra.PrintInterferenceGraph();
	   
	   
	   GraphColoring fgC = new GraphColoring();
	   fgC.GreedyColoring(flra.getiG());
	   fgC.printRegisters();
	   
	   ReplaceWithRegisters frwr = new ReplaceWithRegisters();
	   frwr.replace(c.GetRoot(), c, fgC.getRegisterMap());
	   System.out.println("creating CFGWR");
	   DotGen FCFGWR = new DotGen("src/Display/" + fKey +".gv");
	   FCFGWR.generateWithRegister(c); 
	   
   }
   
   
 } 
	
	/*
	
	public static void main(String args[])
	{
		InterferenceGraph ig = new InterferenceGraph();
		ArrayList<String> arr = new ArrayList<String>();
		
		for(int i=0;i<3;i++)
		{
			char ch =(char)(65 + i);
			String str = ""+ch+i;
			arr.add(str);
		}
		//System.out.println("chkpnt2");
		
		ig.AddToGraph(arr);
		System.out.println(ig);
		String str = ""+(char)68+0;
		arr.add(str);
		arr.remove(1);
		ig.AddToGraph(arr);
		System.out.println(ig);
	}*/
 /*
 public static void main(String arg[])
 {
   Parser p = new Parser("src/Parser/input_1.txt");
   //Parse the file for syntax and also prepare all structures
   //Structures are:
   try{
        p.computation();
      }

    catch(RuntimeException e){
	System.out.println(e);
	return;
	}
   
   HashMap<String, CFG> functions = p.GetCFG().getFunctionList();
   for(String fKey:functions.keySet())
   {
	   CFG c = functions.get(fKey);
	   DotGen cfgg_b4cp = new DotGen("src/Display/dotOutput_"+ fKey +".gv");
	   cfgg_b4cp.generate(c);
	   
   }
   
   DotGen cfgg_b4cp = new DotGen("src/Display/dotOutput.gv");
   cfgg_b4cp.generate(p.GetCFG());
  
  
 }
 */
	
}
