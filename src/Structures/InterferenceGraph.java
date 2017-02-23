package Structures;


import java.util.ArrayList;
import java.util.HashMap;



public class InterferenceGraph {

	
	
	private HashMap<String, ArrayList<String>> adjacencyList;

	public InterferenceGraph()
	{
		adjacencyList = new HashMap<String, ArrayList<String>>();
	}
	
	
	
	public HashMap<String, ArrayList<String>> getAdjacencyList() {
		return adjacencyList;
	}

	public void setAdjacencyList(HashMap<String, ArrayList<String>> adjacencyList) {
		this.adjacencyList = adjacencyList;
	}
	
	
	
	public void AddToGraph(ArrayList<String> liveSet)
	{
		System.out.println("LiveSet:");
		System.out.println(liveSet);
		for(String r:liveSet)
		{
			boolean flag = false;
			if( !adjacencyList.containsKey(r))
			{
				System.out.println("New entry");
				adjacencyList.put(r, new ArrayList<String>());
			}
		}
		
		for(String r:liveSet)
		{
			for(String r2:liveSet)
			{
				if(r.equals(r2)==false)
				{
					System.out.println(r.toString());
					
					if(!adjacencyList.get(r).contains(r2))
					{
						adjacencyList.get(r).add(r2);
					}
					
					if(!adjacencyList.get(r2).contains(r))
					{
						adjacencyList.get(r2).add(r);
					}
				}
			}
			
			
		}
	}
	
	public String toString()
	{
		return adjacencyList.toString();
	}
}
