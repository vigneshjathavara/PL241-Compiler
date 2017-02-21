import Display.DotGen;
import Optimization.CSEElimination;
import Optimization.CopyPropagation;
import Parser.Parser;
import RegisterAllocation.LiveRangeAnalyzer;
import Structures.DominatorTree;
import Structures.InterferenceGraph;
import Structures.Result;
import java.util.ArrayList;

class test
{

 public static void main(String arg[])
 {
   Parser p = new Parser("src/Parser/test31.txt");
   //Parse the file for syntax and also prepare all structures
   //Structures are:
   try{
        p.computation();
      }

    catch(RuntimeException e){
	System.out.println(e);
	return;
	}
   
   //Prepare the dominator tree.
   DominatorTree dt = new DominatorTree(p.GetCFG());
   dt.CreateDominatorTree(p.GetCFG());
   
   DotGen dtg_b4cp = new DotGen("src/Display/domTreeDotOutput_b4cp.gv");
   dtg_b4cp.generateDomTree(p.GetCFG());
   
   DotGen cfgg_b4cp = new DotGen("src/Display/dotOutput_b4cp.gv");
   cfgg_b4cp.generate(p.GetCFG());
   
   //Copy Propagation is performed on the 
   CopyPropagation cp = new CopyPropagation();
   cp.CPOptimise(p.GetCFG());
   
   DotGen cfgg_b4cse = new DotGen("src/Display/dotOutput_b4cse.gv");
   cfgg_b4cse.generate(p.GetCFG());
   
   CSEElimination cse = new CSEElimination();
   cse.CSEOptimise(p.GetCFG());
   
   //Compute the visual graph of CFG
   DotGen cfgg = new DotGen("src/Display/dotOutput.gv");
   cfgg.generate(p.GetCFG());
   
   //compute visual graph of Dominator Tree
   DotGen dtg = new DotGen("src/Display/domTreeDotOutput.gv");
   dtg.generateDomTree(p.GetCFG());   
   
   LiveRangeAnalyzer lra = new LiveRangeAnalyzer(p.GetCFG());
   System.out.println("The Interference Graph:");
   lra.PrintInterferenceGraph();

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
	
}
