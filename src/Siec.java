//zewnêtrzny plik
//ile informacji miêdzy dowolnymi dwoma wezlami?
//dwie macierze: przepustowosc/probability
//druga: ile informacji
//jesli nieprzepustowa, to UMARLO

//Jacek Mucha
//11 marca 2014
//Symulacja sieci
//nieefektywne algorytmy

import java.util.*;
import java.io.*;


public class Siec
{
	int n = 5;
	//czy sukces
	public static boolean success = true;
	//zmienna okreslajaca liczbe wezlow w sieci
	public static int nodeNumber = 0;
	//zmienna okreslajaca liczbe krawedzi w sieci
	public static int edgeNumber = 0;
	//lista wezlow
	Vector<Node> nodeList = new Vector<Node>();
	//lista krawedzi
	Vector<Edge> edgeList = new Vector<Edge>();
	File connectionsFile, capacitiesFile, probabilitiesFile;
	//ile pakietow miedzy danymi wezlami
	public static int[][] connections;
	//lista poprzednikow najkrotszych sciezek
	public static int[][] paths;
	//prawdopodobienstwo rozerwania danej krawedzi
	public static int[][] probabilities;
	//przepustowosci krawedzi
	public static int[][] capacities;
	public static void main(String[] args)
	{
		Siec siec = new Siec();
		siec.readNetFromFile();
		System.out.println("Sukcesów Monte Carlo: "+siec.MonteCarlo(10000));
	}
	public void readNetFromFile()
	{
		//System.out.println("Odczyt z plików...");
		connectionsFile = new File("conn.txt");
		capacitiesFile = new File("cap.txt");
		probabilitiesFile = new File("pro.txt");
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(connectionsFile));
			String line = null;
			String[] numbers;
			boolean firstControl = true;
			int currentLine=0,currentRow=0;
			//System.out.println("connections:");
			while((line=br.readLine())!=null)
			{
				currentRow = 0;
				numbers = line.split(" ");
				//System.out.print(line);
				//System.out.println(numbers[1]+"\n");
				if(firstControl)
				{
					n = numbers.length;
					connections = new int[n][n];
					paths = new int[n][n];
					capacities = new int[n][n];
					probabilities = new int[n][n];
					firstControl = false;
				}
				for(int i=0;i<numbers.length;i++)
				{
					connections[currentRow][currentLine] = Integer.parseInt(numbers[i]);
					//System.out.print(connections[currentRow][currentLine]+" ");
					currentRow++;
				}
				currentLine++;
				//System.out.println("");
			}
			br.close();
			br = new BufferedReader(new FileReader(capacitiesFile));
			currentLine = 0;
			while((line=br.readLine())!=null)
			{
				currentRow = 0;
				numbers = line.split(" ");
				for(int i=0;i<numbers.length;i++)
				{
					capacities[currentRow][currentLine] = Integer.parseInt(numbers[i]);
					currentRow++;
				}
				currentLine++;
			}
			br.close();
			br = new BufferedReader(new FileReader(probabilitiesFile));
			currentLine = 0;
			while((line=br.readLine())!=null)
			{
				currentRow = 0;
				numbers = line.split(" ");
				for(int i=0;i<numbers.length;i++)
				{
					probabilities[currentRow][currentLine] = Integer.parseInt(numbers[i]);
					currentRow++;
				}
				currentLine++;
			}
			br.close();
		}
		catch(Exception e)
		{
			System.out.print(e+" B³¹d");
		}
		//System.out.println("Wczytano wêz³ów: "+n);
	}
	//makeNet
	public void makeNet()
	{
		Random generator = new Random();
		int r;
		for(int i=0;i<n;i++)
		{
			addNode();
		}
		for(int i=0;i<n;i++)
		{
			for(int j=i;j<n;j++)
			{
				r = generator.nextInt(1000);
				if(probabilities[i][j]>r && probabilities[i][j]>0)
				{
					makeEdge(nodeList.get(i),nodeList.get(j),probabilities[i][j],capacities[i][j]);
				}
			}
		}
	} 
	//makeEdge probability, capacity
	public void makeEdge(Node start, Node finish, int probability, int capacity)
	{
		int startNodeIndex = nodeList.indexOf(start);
		int finishNodeIndex = nodeList.indexOf(finish);
		Edge newEdge = new Edge(start, finish, probability, capacity,edgeNumber);
		edgeList.add(newEdge);
		nodeList.get(startNodeIndex).edgeList.add(newEdge);
		nodeList.get(startNodeIndex).neighbourList.add(finish);
		nodeList.get(finishNodeIndex).edgeList.add(newEdge);
		nodeList.get(finishNodeIndex).neighbourList.add(start);		
		edgeNumber++;
	}
	//test
	//theShortestPathFinder Floyd-Warshall
	public int[][] theShortestPathFinder()
	{
		int d[][] = new int[nodeNumber][nodeNumber];
		int poprzednik[][] = new int[nodeNumber][nodeNumber];
		for(int i=0;i<nodeNumber;i++)
		{
			for(int j=0;j<nodeNumber;j++)
			{
				d[i][j] = 1000;
				poprzednik[i][j] = -1;
			}
			d[i][i] = 0;
		}
		for(int k=0;k<edgeNumber;k++)
		{
			d[edgeList.get(k).startNode.adress][edgeList.get(k).finalNode.adress] = 1;
			d[edgeList.get(k).finalNode.adress][edgeList.get(k).startNode.adress] = 1;
			poprzednik[edgeList.get(k).startNode.adress][edgeList.get(k).finalNode.adress] = edgeList.get(k).startNode.adress;
			poprzednik[edgeList.get(k).finalNode.adress][edgeList.get(k).startNode.adress] = edgeList.get(k).finalNode.adress;
			//edgeList.get(k).weight+=connections[edgeList.get(k).startNode.adress][edgeList.get(k).finalNode.adress];
			//edgeList.get(k).weight+=connections[edgeList.get(k).finalNode.adress][edgeList.get(k).startNode.adress];
		}
		for(int i=0;i<nodeNumber;i++)
		{
			for(int j=0;j<nodeNumber;j++)
			{
				for(int k=0;k<nodeNumber;k++)
				{
					if(d[j][k]>d[j][i]+d[i][k])
					{
						d[j][k]=d[j][i]+d[i][k];
						poprzednik[j][k]=poprzednik[i][k];
					}
				}
			}
		}
		return poprzednik;
	}
	public void drawConn()
	{
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
				
				System.out.print(paths[i][j]+" ");
			System.out.println("");
		}
	}
	public void drawPaths()
	{
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
				System.out.print(paths[i][j]+" ");
			System.out.println("");
		}
	}
	public void addNode()
	{
		Node newNode = new Node(nodeNumber);
		nodeNumber++;
		nodeList.add(newNode);
	}
	//deleteNode
	//deleteEdge
	//findNodeByEdge
	//findEdgeByNode
	public int findEdgeByNodes(Node n1, Node n2)
	{
		int name=0;
		boolean breaks = false;
		for(int i=0;i<edgeNumber&& !breaks;i++)
		{
			if((edgeList.get(i).startNode==n1 && edgeList.get(i).finalNode==n2 )||(edgeList.get(i).startNode==n2 && edgeList.get(i).finalNode==n1 ) )
			{
				name = i;
				breaks = true;
				return name;
			}
		}
		return -100;
	}
	public void countWeights()
	{
		Edge currentEdge;
		for(int i=0;i<nodeNumber && success;i++)
		{
			for(int j=0;j<nodeNumber && success;j++)
			{
				if(j!=i)
				{
					int medium,s=i;
					int f=j;
					while(s!=f && success)
					{
						medium = paths[s][f];
						currentEdge = edgeList.get(findEdgeByNodes(nodeList.get(medium),nodeList.get(f)));
						currentEdge.weight+=connections[i][j];
						//System.out.print(currentEdge.name);
						if(currentEdge.weight>currentEdge.capacity && s!=medium)
						{
							//System.out.print("\nP: E("+currentEdge.name+")="+currentEdge.weight+">"+currentEdge.capacity+" ("+s+","+medium+")"+" wp="+currentEdge.startNode.adress+" wk="+currentEdge.finalNode.adress);
							success = false;
						}
						f=medium;
					}
				}
			}
		}
		//System.out.println("\nSieæ przeci¹¿ona: "+!success);
	}
	public int MonteCarlo(int tests)
	{
		int successes = 0;
		for(int i=0;i<tests;i++)
		{
			success = true;
			testNet();
			if(success)
				successes++;
		}
		return successes;
	}
	//testNet
	public void testNet()
	{
		success = true;
		edgeNumber = 0;
		nodeNumber = 0;
		edgeList.clear();
		nodeList.clear();
		//readNetFromFile();
		makeNet();
		//System.out.println("Krawêdzi: "+edgeNumber);
		paths = theShortestPathFinder();
		testReliability();
		//System.out.println("Sieæ spójna: "+success);
		//drawPaths();
		countWeights();
	}
	//testReliability
	public void testReliability()
	{
		//System.out.print("Badanie przepustowoœci: ");
		for(int i=0;i<nodeNumber;i++)
			for(int j=0;j<nodeNumber;j++)
			{
				if(paths[i][j]<0 && i!=j)
				{
					success = false;
					//System.out.print(0);
				}
				else
					;
					//System.out.print(1);
			}
		//System.out.println("");
	}
	public double T()
	{
		
		return 1.0;
	}
}

class Node
{
	//numer porzadkowy wezla (jego adres)
	public int adress;
	//lista krawedzi
	Vector<Edge> edgeList;
	Vector<Node> neighbourList;
	public Node(int adress)
	{
		this.adress = adress;
		edgeList = new Vector<Edge>();
		neighbourList = new Vector<Node>();
	}
}

class Edge
{
	//probability_unormowane = probability/100
	public int probability;
	//przepustowosc
	public int capacity;
	//wezel poczaTKOWY
	Node startNode;
	//wezel koncowy
	Node finalNode;
	//numer porzadkowy krawedzi
	int name;
	//obciazenie
	int weight = 0;
	public Edge(Node start, Node finish,int probability, int capacity, int edgeNumber)
	{
		this.startNode = start;
		this.finalNode = finish;
		this.probability = probability;
		this.capacity = capacity;
		this.name = edgeNumber;
		//edgeNumber++;
	}
}