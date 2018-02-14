//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)

public class NeighborsList{

	//First node in list
	Neighbor first;

	private static final int COPP_SPEED = 230000000; // (m/s)
	private static final int OPTI_SPEED = 200000000; // (m/s)

	//Constructor to create head of list
	// empty except for vertex number
	public NeighborsList(int num){
		first = new Neighbor(num);
	}

	public void add(int num, String type, int wid, int len){
		//Create new node
		Neighbor nbr = new Neighbor(num, type, wid, len);
		
		//Add new node to list, 
		// directly after first
		nbr.next = first.next;
		first.next = nbr;
	}

	public int getBandwidth(int num){
		if(!this.isNeighbor(num)){
			return -1;
		}

		Neighbor curr = first;
		while(curr.getVert() != num){
			curr = curr.next;
		}

		return curr.getBandWidth();
	}

	//Remove vertex number num from list
	public void remove(int num){
		Neighbor curr = first;
		Neighbor prev = curr;

		while(curr != null){
			if(curr.getVert() == num){
				prev.next = curr.next;
				return;
			}
			prev = curr;
			curr = curr.next;
		}
	}

	public void printList(){
		Neighbor curr = first.next;
		System.out.print("--\n" + first.getVert() + "|->");
		while(curr != null){
			System.out.print("|" + curr.getVert() + "|" + curr.getLatency() + "|");
			if(curr.next != null){
				System.out.print("->");
			}
			curr = curr.next;
		}
		System.out.println("\n--\n");
	}

	//Returns array of all vertices in list 
	public Neighbor[] toArray(){
		Neighbor arr[] = new Neighbor[getListLength() - 1];
		Neighbor curr = first.next;
		for(int i = 0; i < arr.length; i++){
			arr[i] = curr;
			curr = curr.next;
		}
		return arr;
	}

	public int getListLength(){
		Neighbor curr = first;
		int len = 0;
		while(curr != null){
			len++;
			curr = curr.next;
		}
		return len;
	}

	public boolean isNeighbor(int num){
		Neighbor curr = first;

		while(curr != null){
			if(curr.getVert() == num){
				return true;
			}
			curr = curr.next;
		}

		return false;
	}

	public Neighbor getNeighbor(int num){
		Neighbor curr = first;
		while(curr != null){
			if(curr.getVert() == num){
				return curr;
			}
			curr = curr.next;
		}

		return curr;
	}

	public boolean isEmpty(){
		return (first.next == null);
	}
}