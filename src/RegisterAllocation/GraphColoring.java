package RegisterAllocation;

import Structures.InterferenceGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GraphColoring {

	HashMap<String,Integer> registerMap;
	
	
	
	public HashMap<String, Integer> getRegisterMap() {
		return registerMap;
	}


	public void setRegisterMap(HashMap<String, Integer> registerMap) {
		this.registerMap = registerMap;
	}


	public GraphColoring()
	{
		registerMap = new HashMap<String,Integer>();
	}
	
	
	public void GreedyColoring(InterferenceGraph iG)
	{
	 
	    HashMap<String,ArrayList<String>> adjList = iG.getAdjacencyList();
	    int i=0;
	    for(String str:adjList.keySet())
	    {
	    	if(i==0)
	    		registerMap.put(str, 1);
	    	else
	    		registerMap.put(str, -1);
	    	
	    	i++;
	    }
	    int size = adjList.size();
	    // Assign the first color to first vertex
	   //result[0]  = 0;
	 
	    // Initialize remaining V-1 vertices as unassigned
	    //for (int u = 1; u < V; u++)
	    //    result[u] = -1;  // no color is assigned to u
	 
	    // A temporary array to store the available colors. True
	    // value of available[cr] would mean that the color cr is
	    // assigned to one of its adjacent vertices
	   /* boolean available[]=new boolean[i];
	    for (int cr = 0; cr < i; cr++)
	        available[cr] = false;
	 */
	    // Assign colors to remaining V-1 vertices
	    Iterator<String> it = adjList.keySet().iterator();
	    if(it.hasNext())
	    	it.next();
	    
	    while(it.hasNext())//for (int u = 1; u < V; u++)
	    {
	        // Process all adjacent vertices and flag their colors
	        // as unavailable
	    	boolean available[]=new boolean[adjList.size()+1];
	 	    for (int cr = 1; cr <= size; cr++)
	 	        available[cr] = false;
	    	
	    	String s = it.next();
	    	
	        for (String str: adjList.get(s))
	            if (registerMap.get(str) != -1)
	                available[registerMap.get(str)] = true;
	 
	        // Find the first available color
	        int cr;
	        for (cr = 1; cr <= size; cr++)
	            if (available[cr] == false)
	                break;
	 
	        registerMap.put(s, cr) ; // Assign the found color   
	    }
	}
	
	public void printRegisters()
	{
		 for (String str:registerMap.keySet())
		        System.out.println("Vertex: " + str + " --->  Color "+ registerMap.get(str));
	}
}
