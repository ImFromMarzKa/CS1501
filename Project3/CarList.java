//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17


//Linked list class for separate chaining in CarHT

public class CarList{

	private Node first;

	private class Node{
		private Car car;
		private Node next;
	}

	public CarList(){
		first = null;
	} 

	//Add a new car to end of linked lsit
	public void add(Car c){
		if(first == null){
			first = new Node();
			first.car = c;
			first.next = null;
			return;
		}

		Node node = first;
		Node prev = first;
		while(node != null){
			if(node.car.getVin().equals(c.getVin())){
				return;
			}
			prev = node;
			node = node.next;
		}

		node = new Node();
		node.car = c;
		node.next = null;
		prev.next = node;
	}

	//Remove a specified car from linked list
	public void remove(Car c){
		if(first == null){
			return;
		}

		Node node = first;
		Node prev = node;
		while(node != null){
			if(node.car.getVin().equals(c.getVin())){
				prev.next = node.next;
				node = null;
				return;
			}
			prev = node;
			node = node.next;
		}
		return;
	}

	//Update a specified car in linked list
	public void update(Car c){
		if(first == null){
			return;
		}

		Node node = first;
		while(node != null){
			if(node.car.getVin().equals(c.getVin())){
				node.car = c;
				return;
			}
			node = node.next;
		}
		return;
	}

	//Get minimum car in linked list based on specified meter, met
	public Car getMin(int met){
		if(met == 0){
			return getMinPrice();
		}
		else{
			return getMinMiles();
		}
	}

	//Helper function to get min priced car in list
	private Car getMinPrice(){
		Node node = first;
		Car min = first.car;
		System.out.println("\n\n------------------------\nGetting lowest priced car");
		

		while(node != null){
			System.out.println(">>>Price of min car: " + min.getPrice());
			System.out.println(">>>Price of node car: " + node.car.getPrice());
			if(node.car.getPrice() < min.getPrice()){
				min = node.car;
			}
			node = node.next;
		}

		System.out.println("------------------------\n\n");
		return min;
	}

	//Helper function to get min miles car from list
	private Car getMinMiles(){
		Node node = first;
		Car min = first.car;

		while(node != null){
			if(node.car.getMiles() < min.getMiles()){
				min = node.car;
			}
			node = node.next;
		}
		return min;
	}

	public void printList(){
		Node node = first;
		System.out.println(">>>Printing car list:");
		while(node != null){
			Car c = node.car;
			System.out.println("VIN: " + c.getVin());
			System.out.println("Make: " + c.getMake());
			System.out.println("Model: " + c.getModel());
			System.out.println("Price: " + c.getPrice());
			System.out.println("Mileage: " + c.getMiles());
			System.out.println("Color: " + c.getColor() + "\n");
			System.out.println("------>");
			node = node.next;
		}
	}
}