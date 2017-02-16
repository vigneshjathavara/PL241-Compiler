import Display.DotGen;
import Optimization.CSEElimination;
import Optimization.CopyPropagation;
import Parser.Parser;
import Structures.DominatorTree;

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
	}
   
   //Prepare the dominator tree.
   DominatorTree dt = new DominatorTree(p.GetCFG());
   dt.CreateDominatorTree(p.GetCFG());
   
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

 } 
}
