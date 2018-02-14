//Riley Marzka
//CS1501
//Project4 (Graphs)
//Due: 4/3/17 (Mon)

public class BFSQueue{
	private QNode first;
	private QNode last;

	private class QNode{
		private int vertex;
		private QNode next;
	}

	public BFSQueue(){
		first = new QNode();
		first.vertex = -1;
		first.next = null;
		last = first;
	}

	public BFSQueue(int num){
		first = new QNode();
		first.vertex = num;
		first.next = null;
		last = first;
	}

	public void add(int num){
		if(first.vertex == -1){
			first.vertex = num;
			return;
		}

		QNode newNode = new QNode();
		newNode.vertex = num;
		newNode.next = null;
		last.next = newNode;
		last = newNode;
		return;
	}

	public int pop(){
		QNode toPop = first;
		first = first.next;
		return toPop.vertex;
	}

	public boolean isEmpty(){
		return (first == null);
	}

	public boolean contains(int num){
		System.out.print("Queue: ");
		QNode curr = first;
		while(curr != null){
			if(curr.vertex == num){
				return true;
			}
			curr = curr.next;
		}
		return false;
	}

	public void printQueue(){
		QNode curr = first;
		while(curr != null){
			System.out.print(curr.vertex + " ");
			curr = curr.next;
		}
		System.out.println();
	}
}