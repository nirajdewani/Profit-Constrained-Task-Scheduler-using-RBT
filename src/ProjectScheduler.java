/***
References:
http://www.sanfoundry.com/java-program-implement-ford-fulkerson-algorithm/
http://algs4.cs.princeton.edu/64maxflow/FordFulkerson.java.html
http://www.geeksforgeeks.org/ford-fulkerson-algorithm-for-maximum-flow-problem/
http://www.geeksforgeeks.org/breadth-first-traversal-for-a-graph/
***/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;


public class ProjectScheduler 
{
	static int[] parent;
	static int numberOfNodes;
	public static void main(String args[]) throws IOException
	{
		ProjectScheduler ps = new ProjectScheduler();
		Scanner scanner = new Scanner(new InputStreamReader(System.in));
        
		System.out.println("Enter input file path");
        String inputFilePath = scanner.nextLine();
        inputFilePath = inputFilePath.replace("/", File.separator);	
        
        System.out.println("Enter output file path");
        String outputFilePath = scanner.nextLine();
        outputFilePath = outputFilePath.replace("/", File.separator);	
        scanner.close();
        
        int[][] graph = ps.getGraph(inputFilePath);
                
        numberOfNodes = graph.length -1;
		parent = new int[numberOfNodes + 1];
		
		int[][] residualGraph = ps.getAllocation(graph, numberOfNodes - 1, numberOfNodes);
		
		ps.generateOutput(graph, residualGraph, outputFilePath);
	}
	
	public int[][] getGraph(String inputFilePath) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
		String fileContents;
		try 
	    {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) 
	        {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        fileContents = sb.toString();
	    } 
	    finally 
	    {
	        br.close();
	    }

		String[] inputLines = fileContents.split(System.lineSeparator());
		int numberOfTasks = Integer.parseInt(inputLines[0]);
		int inputMatrixDimension = numberOfTasks + 1;									//extra location for profit
		int[][] inputMatrix = new int[inputMatrixDimension][inputMatrixDimension];	
		
		for(int i = 1; i< inputMatrixDimension; i++)
		{
			StringTokenizer st = new StringTokenizer(inputLines[i],",");
			for(int j = 0; j < inputMatrixDimension; j++)
			{
				if(st.hasMoreTokens())
				{
					inputMatrix[i][j] = Integer.parseInt(st.nextToken(","));
				}
			}
		}
		
		int numberOfNodes = numberOfTasks + 2;											//since source and sink nodes would be added  
		int[][] graph = new int[numberOfNodes + 1][numberOfNodes + 1];					//since 0th position is not used
		
		//initializing the graph
		for(int i = 1; i < inputMatrixDimension; i++)
		{
			int currentNode = inputMatrix[i][0];
			if(inputMatrix[i][1] > 0)													//checking profit
			{
					graph[numberOfNodes - 1][currentNode] = inputMatrix[i][1];			//adding edge from source to concerned node
			}
			else if(inputMatrix[i][1] < 0)
			{
					graph[currentNode][numberOfNodes] = inputMatrix[i][1]*-1;														//adding edge from concerned node to sink
			}
			
			for(int j = 2; j < inputMatrixDimension; j++)								//adding dependency edges
			{
				if(inputMatrix[i][j] != 0)
					graph[currentNode][inputMatrix[i][j]] = Integer.MAX_VALUE;
			}
		}			
		return graph;
	}
	
	public int[][] getAllocation(int[][] graph, int source, int sink)
	{
		int u, v, pathFlow = 0;
		int[][] residualGraph = new int[numberOfNodes + 1][numberOfNodes + 1];
		
        for (int sourceNode = 1; sourceNode <= numberOfNodes; sourceNode++)
        {
            for (int destinationNode = 1; destinationNode <= numberOfNodes; destinationNode++)
            {
                residualGraph[sourceNode][destinationNode] = graph[sourceNode][destinationNode];
            }
        }
		
        while(hasPath(residualGraph, source, sink))
        {
        	pathFlow = Integer.MAX_VALUE;
        	for (v = sink; v != source; v = parent[v])
            {
                u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (v = sink; v != source; v = parent[v])
            {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }         	
        }
		return residualGraph;
	}
	
	//BFS to find if path exists from given source to sink
	public boolean hasPath(int[][] graph, int source, int sink)
		{
			boolean pathFound = false;
			boolean[] visited = new boolean[numberOfNodes + 1];
			Queue<Integer> queue = new LinkedList<Integer>();
			int currentElement, destination;
			
			for(int i = 1; i <= numberOfNodes; i++)
			{
				parent[i] = -1;
			}
			
			queue.add(source);
			visited[source] = true;
			parent[source] = -1;
			
			while(!queue.isEmpty())
			{
				currentElement = queue.remove();
				destination = 1;
				
				while(destination <= numberOfNodes)
				{
					if(visited[destination] == false && graph[currentElement][destination] > 0)
					{
	                    parent[destination] = currentElement;
	                    queue.add(destination);
	                    visited[destination] = true;
					}
					destination++;
				}
			}
			if(visited[sink] == true)
			{
				pathFound = true;
			}
			
			return pathFound;		
		}

	public void generateOutput(int[][] graph, int[][] residualGraph, String outputFilePath) throws IOException
	{
		int[] profitableProjects = new int[numberOfNodes];
        int[] nonprofitableProjects = new int[numberOfNodes];
        boolean[] completedProjects = new boolean[numberOfNodes];
        
        int z = 0;
        for(int i = 0; i<numberOfNodes; i++)
        {
        	if(graph[numberOfNodes -1][i] > 0)
        	{
        		profitableProjects[z] = i;
        		z++;
        	}        	
        }
        
        z = 0;
        for(int i = 0; i<numberOfNodes; i++)
        {
        	if(graph[i][numberOfNodes] > 0)
        	{
        		nonprofitableProjects[z] = i;
        		z++;
        	}
        }
        
        for(int i = 0; i < numberOfNodes; i++)
        {
        	if(nonprofitableProjects[i] != 0 && residualGraph[nonprofitableProjects[i]][numberOfNodes] == 0)
        	{
        		completedProjects[nonprofitableProjects[i]] = true;
        	}
        }
        
        for(int i = 0; i < numberOfNodes; i++)
        {
        	if(profitableProjects[i] != 0)
        	{
        		completedProjects[profitableProjects[i]] = true;
        		for(int j = 0; j < numberOfNodes; j++)
        		{ 
        			if(graph[profitableProjects[i]][j] == Integer.MAX_VALUE && completedProjects[j] == false)
        				completedProjects[profitableProjects[i]] = false;
        		}
        	}
        }
        
        File output = new File(outputFilePath);
		try
		{
			if(output.exists()==false)
			{
	            output.createNewFile();
			}
		}	
		catch(IOException e)
		{
	        System.out.println("Error in writing output");
		}
		
		for(int i = 0; i < numberOfNodes; i++)
		{
			if(completedProjects[i])
			{
				PrintWriter writer = new PrintWriter(new FileWriter(output, true));
				writer.append(i + System.lineSeparator());
				writer.close();
			}
		}
	}
}
