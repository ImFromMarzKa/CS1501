//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)

/*Version Notes:
	* File parses
	* Menu prints
	* Menu Options:
		* 1) Fully Functional
		* 2) Fully Functional
		* 3) Should be Functional
				Need to account for case of immediate neighbors
		* 4) Should be Functional
		* 5) Should be Functional
		* 6) Program exits
	* CLEANUP AND FINISH COMMENTING, THEN SUBMIT
*/

/*Description:
	* Program analyzes a given graph representing a computer network 
		according to several specified metrics
	* Vertices represent switches in the network
	* Edges represent either fiber optic or copper cables run between 
		switches
	* Program operates entirely via consol interface menu
	
	* Graph is provided via file specified in command line argument 
		with following format:
		* 1st Line: Int representing number of vertices
		* Subsequent Lines: [int_1 int_2 string int_3 int_4]
			* int_1 & int_2 specify endpoints of edge
			* string describes cable type ("optical" || "copper")
			* int_3 specifies bandwidth of cable (Mb/s)
			* int_4 specifies length of edge in meters
	* Assumes all cables are full duplex (connections go both ways)
		* Same flow in both directions simultaneously 
	* Graph is internally represented as adjacency list
	* After graph is loaded, user is presented with 6-option menu:
		1) Lowest Latency Path
		2) Copper-Only Connected
		3) Max Amount of Data
		4) Min Ave Latency Spanning Tree
		5) Any Failing Vertice Pairs
		6) Quit
*/

/*Command Line Arguments:
	* java NetworkAnalysis [data_filename]
*/

import java.io.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.lang.Math;

public class NetworkAnalysis{

	private static NeighborsList adjList[];
	private static Scanner sc;
	private static int numVerts;
	private static final int MAX_INT =    999999999;
	private static final int COPP_SPEED = 230000000; // (m/s)
	private static final int OPTI_SPEED = 200000000; // (m/s)

	public static void main(String args[]) throws IOException{
		//Check for command line argument
		if(args.length > 0){
			populateLists(args[0]);
		}
		else{
			System.out.println("Usage: java NetworkAnalysis [data_filename]");
			System.exit(1);
		}

	// //DEBUG:
	// 	System.out.println("Adjacency Lists for this file:");
	// 	printAdjLists();
	// 	System.out.println("--------------------------------\n\n");
	// ////////

		System.out.println("Welcome to the Network Analysis Center!\n");
		int in = 0;
		sc = new Scanner(System.in);

		//Loop until "quit"
		while(in != 6){
			printMenu();
			in = sc.nextInt();

			//Call function corresponding to selected menu option
			switch(in){
				case 1:
					lowLatency();
					break;
				case 2:
					copperConnected();
					break;
				case 3:
					maxData();
					break;
				case 4:
					minAveLateSpanTree();
					break;
				case 5:
					failingVertPairs();
					break;
				case 6:
					System.out.println("Thanks for using the Network Analysis Center.\nGoodbye!");
					System.exit(0);
					break;
				default:
					System.out.println("Invalid option! Please enter a number 1-6");
					in = 0;
					break;
			}
		}
	}


	/*Method to find the lowest latency path between any two points
	 and give the bandwidth available along that path
	 	* Prompts user for 2 vertices
	 	* Calls function findLowLatency(), passing the desired
	 		vertices as arguments
		* Outputs bandwidth available along resultant path
	*/
	private static void lowLatency(){
		boolean valid = false;
		int v1, v2;
		do{
			System.out.println("\nPlease enter the vertex at which to start > ");
			v1 = sc.nextInt();
			System.out.println("Please enter the vertex at which to end > ");
			v2 = sc.nextInt();
			//Check for valid vertices 
			if(v1 < 0 || v1 >= adjList.length || v2 < 0 || v2 >= adjList.length){
				System.out.println("Valid vertices are between 0 and " + (adjList.length - 1));
			}
			else if(v1 == v2){
				System.out.println("Cannot start and end at same vertex");
			}
			else{
				valid = true;
			}
		}while(!valid);

		int bw = findLowLatency(v1, v2);
		System.out.println("Bandwidth along path from " + v1 + " to " + v2 + " = " + bw + "\n");
	}

	/*Function to perform Dijkstra's Algorithm
		* Finds path from start to end with lowest latency
		* Prints the path
		* Returns the minimum bandwidth

		*Parameters:
			* start = vertex at which to start
			* end = vertex at which to end
		* Returns:
			* integer representing min bandwidth 
				along path from start to end
	*/
	private static int findLowLatency(int start, int end){
		//3 arrays indexed by vertices
		double late[] = new double[numVerts];
		int via[] = new int[numVerts];
		boolean visited[] = new boolean[numVerts];

		//Initialize all values of late[] to MAX_INT 
		// except start
		for(int i = 0; i < numVerts; i++){
			if(i != start){
				late[i] = (double)MAX_INT;
			}
			visited[i] = false;
			via[i] = -1;
		}

		late[start] = 0.0;
		visited[start] = true;

		//curr = start
		NeighborsList currList = adjList[start];
		Neighbor currN = currList.first.next;
		double fromStart = 0;
		int minLateInd = start; //Tracks which vertex to try next
		int currInd = start; //Tracks current vertex

		while(!visited[end]){	
			//Traverse each list in the Adjacency List Array
			while(currN != null){
				
				//For each unvisited neighbor:
				if(!visited[currN.getVert()]){
					currInd = currN.getVert();
					fromStart = currN.getLatency() + late[currList.first.getVert()];

					//Checks current latency against
					//	stored latency for index of current vertex
					if(late[currInd] > fromStart){
						late[currInd] = fromStart;
						via[currInd] = currList.first.getVert();
					}

					//Tracks closest unknown vertex
					if(minLateInd == currList.first.getVert() || late[minLateInd] > late[currInd]){
						minLateInd = currInd;
					}
				}

				if(currN.getVert() == end){
					//Reached end of path
					break;
				}
				currN = currN.next;
			}



			//Repeat with closest unvisited neighbor
			currList = adjList[minLateInd];
			//Set visited bit of curr
			visited[currList.first.getVert()] = true;
			currN = currList.first.next;
		}

		//Tables complete: find path
		int[] path = new int[numVerts];
		int ind = end; //Begin at last vertex and build backwards
		int front = 0;
		path[numVerts - 1] = end;
		int minBand = MAX_INT;
		for(int i = (numVerts - 2); i >= 0; i--){
			int prev = ind;
			ind = via[ind];
			path[i] = ind;
			
			int currBand = adjList[ind].getBandwidth(prev);
			if(minBand > currBand){
				minBand = currBand;
			}

			if(ind == start){
				front = i;
				break;
			}
		}

		System.out.println("\nLowest latency path from " + start + " to " + end + ":");
		for(int i = front; i < numVerts; i++){
			System.out.print(path[i]);
			if(i < numVerts - 1){
				System.out.print("->");
			}
		}
		System.out.println("\n");
		return minBand;
	}

	//Wrapper function to call recursive DFS function
	private static void copperConnected(){
		//Initialize visited array to false
		boolean vis[] = new boolean[numVerts + 1];
		for(int i = 0; i < numVerts + 1; i++){
			vis[i] = false;
		}
		//Mark vertex 0 as visited
		vis[0] = true;

		boolean _vis[] = copperConnected(adjList[0], 1, vis);
		//Graph is copper connected
		if(_vis[numVerts]){
			System.out.println("\nThe Graph IS copper-only connected!\n");
		}
		else{
			System.out.println("\nThe Graph is NOT copper-only connected!");
			System.out.println("The following vertices cannot be reached using copper connections:");
			for(int i = 0; i < numVerts; i++){
				if(!_vis[i]){
					System.out.print(i + " ");
				}
			}
			System.out.println("\n");
		}
		return;
	}

	/*Recursive method to perform DFS
		* For each node, visit first unseen copper-connected neighbor
		* Backtrack at dead ends
			* nodes with no unseen neighbors
			* nodes with no copper-connected neighbors
		* Try next unseen neighbor after backtracking

		* Recursive Cases:
			* Found unvisited copper-connected neighbor
		* Base Cases:
			* All vertices have been visited
			* No remaining unvisited copper-connected neighbors

		* Parameters:
			* currList = current neighbors list to traverse
			* numVisited = number of visited vertices
			* visited = array to hold visited bits of vertices
				* visited[numVerts] = true if graph is copper-connected
		* Returns:
			* boolean array, the last entry of which indicated
				copper-connectedness
	*/
	private static boolean[] copperConnected(NeighborsList currList, int numVisited, boolean visited[]){

		int _numVisited = numVisited;
		boolean _visited[] = visited;

		//Base Case:
		// If all vertices visited, set last value of visited to true
		// Return visited
		if(_numVisited == numVerts){
			_visited[numVerts] = true;
			return _visited;
		}

		//Begin at first neighbor of current vertex
		Neighbor currN = currList.first.next;
		while(currN != null){

			//Check for unvisited copper-connected neighbor
			if(!_visited[currN.getVert()] && currN.getCableType().equals("copper")){

				//Mark neighbor as visited
				_visited[currN.getVert()] = true;
				_numVisited++;

				//Recurse with list rooted at current neighbor
				_visited = copperConnected(adjList[currN.getVert()], _numVisited, _visited);
				if(_visited[numVerts]){
					return _visited;
				}
				//else{backtrack}
			}
			//Try next unseen neighbor
			currN = currN.next;
		}

		//Base Case:
		// All connections tried
		if(_numVisited == numVerts){
			_visited[numVerts] = true;
		}
		return _visited;
	}

	/*Find max amount of data that can be transferred 
	   from one vertex to another
		* Prompts user for 2 vertices
		* Output max amount of data between vertices 
	*/
	private static void maxData(){
		boolean valid = false;
		int v1 = -1, v2 = -2;
		while(!valid){
			System.out.println("\nPlease enter first vertex > ");
			v1 = sc.nextInt();
			System.out.println("Please enter second vertex > ");
			v2 = sc.nextInt();

			if(v1 == v2){
				System.out.println("Vertices must be unique!");
			}
			else if(v1 < 0 || v1 >= numVerts || v2 < 0 || v2 >= numVerts){
				System.out.println("Valid vertice numbers are between 0 and " + numVerts + "!");
			}
			else{
				valid = true;
			}
		}

		NeighborsList currList = adjList[v1];
		//Queue of unvisited neighbors of current vertex
		BFSQueue q = new BFSQueue(); 
		//Stores possible paths from start to end
		Path p = new Path();

		int band = maxData(currList, p, v2, 0);

		System.out.println("\nThe maximum amount of data that can be transfered from " + v1 + " to " + v2 + ":");
		System.out.println(band + "\n");
	}

	//Recursive method to find max data
	// Performs Breadth First Search
	private static int maxData(NeighborsList currList, Path p, int end, int maxBand){
		//Add all neighbors in currList to queue
		BFSQueue q = new BFSQueue();
		Neighbor curr = currList.first.next;
		while(curr != null){
			q.add(curr.getVert());
			curr = curr.next;
		}
	// //DEBUG:
	// 	System.out.println("Recursing for currList: " + currList.first.vert);
	// 	q.printQueue();
	// 	p.printPath();
	// 	System.out.println();
	// ////////


		while(!q.isEmpty()){
			//Pop next vertex to visit from queue
			int toVisit = q.pop();
			Neighbor nbr = currList.getNeighbor(toVisit);
			//Special Case: end is immediate neighbor of root
			if(toVisit == end && currList.first.getVert() == 0){
				maxBand = Math.max(maxBand, nbr.getBandWidth());
				nbr = nbr.next;
				if(nbr == null){
					return maxBand;
				}
			}
			if(!p.contains(nbr)){
				//Add vertex to path
				p.add(nbr);

				//Check if reached end
				if(nbr.getVert() == end){
					return Math.max(maxBand, p.getBandWidth());
				}

				//Recurse on neighbors of nbr
				int pathBand = maxData(adjList[nbr.getVert()], p, end, maxBand);
				//Compare bandwidths
				if(pathBand > maxBand){
					maxBand = pathBand;
				}
				//Remove vertex crom path
				p.remove(nbr);
			}

		}

		return maxBand;
	}

	/*Modified Prim's Algorithm
		* edgeTo[] serves as parent and best edge arrays
		* 
	*/
	private static void minAveLateSpanTree(){
		SpanEdge[] edgeTo = new SpanEdge[numVerts];
		boolean[] visited = new boolean[numVerts];

		//Populates edgeTo array with parent -1 and latency MAX_INT
		for(int i = 0; i < numVerts; i++){
			edgeTo[i] = new SpanEdge(-1, MAX_INT);
			visited[i] = false;
		}

		//Set values for start
		edgeTo[0].setLatency(0.0);
		visited[0] = true;
		int numVisited = 1;
		int ind = 0;

		//Loop until all vertices have been visited
		while(numVisited < numVerts){
			//Traverse next list in adjacency matrix
			Neighbor currN = adjList[ind].first.next;
			int minLateInd = currN.getVert();
			while(currN != null){
				//Try each unvisited vertex
				if(!visited[currN.getVert()]){
					//Update edgeTo array if necessary
					if(edgeTo[currN.getVert()].getLatency() > currN.getLatency()){
						edgeTo[currN.getVert()] = new SpanEdge(ind, currN.getLatency());
						//Update minimum latency index if necessary
						if(edgeTo[minLateInd].getLatency() > currN.getLatency()){
							minLateInd = currN.getVert();
						}
					}
				} 
				currN = currN.next;
			}
			//Travel to next vertex
			ind = minLateInd;
			visited[ind] = true;
			numVisited++;
		}

		//Print spanning tree
		printTree(edgeTo);
	}

	/*Helper Function to print spanning tree
	*/
	private static void printTree(SpanEdge edges[]){
		System.out.println("Your minimum average latency spanning tree for this graph is:");
		for(int i = 1; i < edges.length; i++){
			System.out.println("(" + edges[i].getParent() + "," + i + ")");
		}
		System.out.println();
	}


	//Helper to call wrapper with all possible vertice pairs
	private static void failingVertPairs(){
		//Try every possible set of vertices
		boolean failed = false;
		int i, j = 0;
		for(i = 0; i < numVerts; i++){
			for(j = i + 1; j < numVerts; j++){
				failed = checkVerts(i, j);
				//If any 2 vertices fail, we are done
				if(failed){
					break;
				}
			}
			if(failed){
				break;
			}
		}

		if(failed){
			System.out.println("\nRemoving vertices " + i + " and " + j + " cause the graph to fail!\nOther pairs may exist as well!\n");

		}		
		else{
			System.out.println("\nThere are no failing vertice pairs in this graph!\n");
		}
	}

	/*Wrapper to call recursive method
		* Parameters:
			* v1 & v2 = exclusionary vertices
		* Returns:
			* True if all vertices were visited
			* False if not all vertices were visited
	*/
	private static boolean checkVerts(int v1, int v2){
		//visited[numverts] will represent all the vertices being seen
		boolean vis[] = new boolean[numVerts + 1];
		for(int i = 0; i < numVerts + 1; i++){
			vis[i] = false;
		}
		//Set the visited values of the excluded vertices to true
		vis[v1] = true;
		vis[v2] = true;
		int start = 0;
		//Special case, 0 and/or 1 are excluded
		while(start == v1 || start == v2){
			start++;
		}
		//Set visited value of starting vertex to true
		vis[start] = true;

		//Call recursive function
		vis = failingVerts(adjList[start], vis);
		return !vis[numVerts]; //If all vertices were visited, return true
								// else, return false
	}

	/*Recursive Method to perform DFS
		* Modified DFS
		* Parameters:
			* currList = list to traverse
			* visited[] = array of visited values for each vertex
		* Returns:
			* array of visited values for each vertex 
			* last value of array represents all vertices being visited
	*/
	private static boolean[] failingVerts(NeighborsList currList, boolean visited[]){
		boolean _visited[] = visited;

	// //DEBUG:
	// 	System.out.println("Current Vertex = " + currList.first.getVert());
	// 	System.out.print("visited = ");
	// 	printArray(_visited);
	// ////////


		//Base Case:
		// If all vertices visited, set last value of visited[] to true
		// Return visited[]
		boolean all = true;
		for(int i = 0; i < numVerts; i++){
			if(!_visited[i]){
				all = false;
				break;
			}
		}
		if(all){
			_visited[numVerts] = true;
			return _visited;
		} 

		//Begin at first neighbor of current vertex
		Neighbor currN = currList.first.next;
		while(currN != null){

			//Check for unvisited neighbor
			if(!_visited[currN.getVert()]){
				_visited[currN.getVert()] = true;

				//Recurse for list rooted at current neighbor
				_visited = failingVerts(adjList[currN.getVert()], _visited);
				if(_visited[numVerts]){
					return _visited;
				}
			}

			//Try next unseen neighbor
			currN = currN.next;
		}

		//Base Case: All connections tried
		all = true;
		for(int i = 0; i < numVerts; i++){
			if(!_visited[i]){
				all = false;
				break;
			}
		}
		if(all){
			_visited[numVerts] = true;
		} 
		return _visited;
	}


	//Helper method to print menu options
	private static void printMenu(){
		System.out.println("Please enter number for what you would like to find:");
		System.out.println("1) Lowest Latency Path");
		System.out.println("2) Copper-Only Connected");
		System.out.println("3) Max Amount of Data");
		System.out.println("4) Min Ave Latency Spanning Tree");
		System.out.println("5) Failing Vertice Pairs");
		System.out.println("6) Quit");
	}

	//Helper method to pupulate adjacency list from file
	private static void populateLists(String filename) throws IOException{
		BufferedReader dRead = null;
		File data = new File(filename);

		//Check for valid file
		if(!data.isFile()){
			System.out.println("Invalid filename");
			System.exit(1);
		}

		try{
			//Create new buffered reader on input file
			dRead = new BufferedReader(new FileReader(data));
		}
		catch(FileNotFoundException fne){
			System.out.println("Failed to open file " + filename);
			System.exit(1);
		}


		//Read first line (# vertices)
		String line = dRead.readLine();
		
		//Parse number of vertices to integer
		try{
			numVerts = Integer.parseInt(line);
		}
		catch(NumberFormatException nfe){
			System.out.println("Number of veritces bad format");
			System.exit(1);
		}

		//Create new adjacency list indexed from 0 to v-1
		adjList = new NeighborsList[numVerts];
		for(int i = 0; i < numVerts; i++){
			adjList[i] = new NeighborsList(i);
		}

		//Populate adjacency list with all vertice pairs
		int vert1 = -1, vert2 = -1, band = -1, len = -1;
		String type = null;
		while((line = dRead.readLine()) != null){
			String[] split = line.split(" ");
			try{
				//Parse line for connection info
				vert1 = Integer.parseInt(split[0]);
				vert2 = Integer.parseInt(split[1]);
				type = split[2];
				band = Integer.parseInt(split[3]);
				len = Integer.parseInt(split[4]);
			}
			catch(NumberFormatException nfe){
				System.out.println("Error on parsing something from file");
				System.exit(1);
			}

			// //Ensure valid info
			// assert (vert1 > 0 && vert1 < numVerts && vert2 > 0 && vert2 < numVerts);
			// assert (band >= 0 && len > 0);

			//Add each vertex to the neighbor list of the other 
			adjList[vert1].add(vert2, type, band, len);
			adjList[vert2].add(vert1, type, band, len);
		}
	}

//DEBUGGING METHODS: 
	private static void printArray(int arr[]){
		System.out.print("[");
		for(int i = 0; i < arr.length; i++){
			System.out.print(arr[i]);
			if(i < arr.length - 1){
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}
	private static void printArray(boolean arr[]){
		System.out.print("[");
		for(int i = 0; i < arr.length; i++){
			System.out.print(arr[i]);
			if(i < arr.length - 1){
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}

	private static void printAdjLists(){
		for(int i = 0; i < numVerts; i++){
			adjList[i].printList();
		}
	}
///////////
}