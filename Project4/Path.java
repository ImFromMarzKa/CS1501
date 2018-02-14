//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)

public class Path{
	private PathNode first;
	private PathNode last;
	private int bandWidth;

	private class PathNode{
		private Neighbor nbr;
		private PathNode next;
	}

	public Path(){
		first = null;
		last = null;
		bandWidth = -1;
	}

	public Path(Neighbor n){
		first = new PathNode();
		first.nbr = n;
		first.next = null;
		bandWidth = n.getBandWidth();
		last = first;
	}

	public void add(Neighbor n){
		if(first == null){
			first = new PathNode();
			first.nbr = n;
			first.next = null;
			bandWidth = n.getBandWidth();
			last = first;
			return;
		}

		PathNode newPN = new PathNode();
		newPN.nbr = n;
		newPN.next = null;
		last.next = newPN;
		last = newPN;
		if(bandWidth > n.getBandWidth()){
			bandWidth = n.getBandWidth();
		}
	}

	//Removes everything in path after vertex number n
	public void remove(Neighbor n){
		//Edge Case: Vertex to remove is first vertex in path
		if(first.nbr.getVert() == n.getVert()){
			first = null;
			last = first;
			bandWidth = -1;
		}

		PathNode curr = first;
		PathNode prev = curr;
		//Traverse until vertex or end of path reached
		while(curr != null && curr.nbr.getVert() != n.getVert()){
			//Keep bandwidth updated
			if(bandWidth > curr.nbr.getBandWidth()){
				bandWidth = curr.nbr.getBandWidth();
			}
			prev = curr;
			curr = curr.next;
		}
		//Remove vertex from list
		if(curr != null){
			prev.next = null;
			last = prev;
		}
	}

	public boolean contains(Neighbor n){
		PathNode curr = first;
		while(curr != null){
			if(curr.nbr.getVert() == n.getVert()){
				return true;
			}
			curr = curr.next;
		}
		return false;
	}

	public int getBandWidth(){
		return bandWidth;
	}

	public void printPath(){
		System.out.print("Path: ");
		PathNode curr = first;
		while(curr != null){
			System.out.print(curr.nbr.getVert() + " ");
			curr = curr.next;
		}
		System.out.println();
	}
}