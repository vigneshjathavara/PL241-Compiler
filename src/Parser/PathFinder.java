package Parser;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathFinder {

	public static void main(String[] args)
			throws FileNotFoundException, IOException {
		String filename = "src/Parser/input_3.txt";
		if (args.length > 0) {
			filename = args[0];
		}   

		List<String> answer = parseFile(filename);
		System.out.println(answer);
	}   

	static List<String> parseFile(String filename)
			throws FileNotFoundException, IOException {
		/*  
		 *  Don't modify this function
		 */
		BufferedReader input = new BufferedReader(new FileReader(filename));
		List<String> allLines = new ArrayList<String>();
		String line;
		while ((line = input.readLine()) != null) {
			allLines.add(line);
		}   
		input.close();

		return parseLines(allLines);    
	}   

	static List<String> parseLines(List<String> lines) {
		/*  
		 * 
		 *  Your code goes here
		 *  
		 */


		//ArrayList<String> visited = new ArrayList<String>();
		HashMap<Character,ArrayList<Character>> graph = new HashMap<Character,ArrayList<Character>> ();

		ArrayList<String> result = new ArrayList<String>();
		char start = lines.get(0).charAt(0);
		char end = lines.get(0).charAt(2);

		for(int i=1;i<lines.size();i++)
		{
			String line = lines.get(i);
			char key = line.charAt(0);
			ArrayList<Character> l = new ArrayList<Character>();
			for(int j=1;j<line.length();j++)
			{
				if(Character.isAlphabetic(line.charAt(j)))
					l.add(line.charAt(j));
			}
			graph.put(key, l);
		}
		System.out.println(lines);
		System.out.println(graph);
		ArrayList<Character> visited =new ArrayList<Character>();
		visited.add(start);
		String path;
		path = ""+start;
		getPaths(graph, visited,path, start, end, result);

		return result;
	}   


	public static void getPaths(HashMap<Character,ArrayList<Character>> graph, ArrayList<Character> visited, String path, Character node, Character end, ArrayList<String> result  )
	{
		ArrayList<Character> children = graph.get(node);
		if(children!=null){
			for(int i=0;i<children.size();i++)
			{
				char child = children.get(i);
				if(child==end)
				{
					path = path + end;
					result.add(path);
				}
				else
				{
					if(!(visited.contains(child)))
					{
						String newpath = path+child;
						ArrayList<Character> visitedNew = new ArrayList<Character>(visited);
						visitedNew.add(child);
						getPaths(graph,visitedNew, newpath, child, end, result);

					}
				}

			}
		}

	}


}
