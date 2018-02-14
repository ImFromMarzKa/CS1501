//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17

//Min priority queue for Cars

public class CarHeap{
	private Car[] cHeap;
	private int mode; //0 for price, 1 for mileage
	private int next;

	//Create Car[] of initial size 50
	//md = 0 -> Sorted by price
	//md = 1 -> Sorted byy miles
	public CarHeap(int md){
		cHeap = new Car[50];
		mode = md;	
		next = 0;
	}

	//Returns highest priority item in queue
	public Car getMin(){
		return cHeap[0];
	}

	//Adds a new car to heap and indirection table, trie
	public void addCar(Car c, CarTrie trie){
		if(next == cHeap.length){
			cHeap = resize(cHeap);
		}

		cHeap[next] = c;
		//Heapify
		swimCar(trie);
		next++;
	}

	//Removes car at index from PQ and indirection table
	public void removeCar(int index, CarTrie trie){
		next--;
		cHeap[index] = cHeap[next];

		if(index == next){
			trie.deleteCar(cHeap[index].getVin());
			cHeap[index] = null;
			return;
		}
		else{
			cHeap[next] = null;

			//Heapify
			if(mode == 0){
				sinkByPrice(index, trie);
			}
			else{
				sinkByMiles(index, trie);
			}
		}
	}

	//Returns sorting mode
	//0 = price
	//1 = miles
	public int getMode(){
		return mode;
	}

	//Place car at index ind
	public void setCar(Car car, int ind){
		cHeap[ind] = car;
	}

	//Returns car at index ind
	public Car getCar(int ind){
		return cHeap[ind];
	}

	//Heapify heap upon update
	public void checkUp(int ind, CarTrie trie){
		int pInd = (int)Math.floor((ind - 1) / 2);

		if(mode == 0){
			swimByPrice(pInd, ind, trie);
			sinkByPrice(ind, trie);
		}
		else{
			swimByMiles(pInd, ind, trie);
			sinkByMiles(ind, trie);
		}
	}

	//Helper method to swim car up heap
	private void swimCar(CarTrie trie){
		int pInd = (int)Math.floor((next - 1) / 2.0);
		if(mode == 0){
			swimByPrice(pInd, next, trie);
		}
		else{
			swimByMiles(pInd, next, trie);
		}
	}

	//Helper method to swim by price
	private void swimByPrice(int parent, int insertInd, CarTrie trie){
		if(parent < 0){
			trie.updateCar(cHeap[0].getVin(), 0);
			return;
		}

		double parPrice = cHeap[parent].getPrice();
		double nextPrice = cHeap[insertInd].getPrice();

		if(parPrice > nextPrice){
			Car temp = cHeap[parent];
			cHeap[parent] = cHeap[insertInd];
			cHeap[insertInd] = temp;

			trie.updateCar(cHeap[parent].getVin(), parent);
			trie.updateCar(cHeap[insertInd].getVin(), insertInd);

			insertInd = parent;
			parent = (int)Math.floor((insertInd - 1) / 2.0);

			swimByPrice(parent, insertInd, trie);
		} 
		else{
			trie.updateCar(cHeap[insertInd].getVin(), insertInd);
		}
	}

	//Helper function to swim by miles
	private void swimByMiles(int parent, int insertInd, CarTrie trie){
		if(parent < 0){
			trie.updateCar(cHeap[0].getVin(), 0);
			return;
		}

		double parMiles = cHeap[parent].getMiles();
		double nextMiles = cHeap[insertInd].getMiles();

		if(parMiles > nextMiles){
			Car temp = cHeap[parent];
			cHeap[parent] = cHeap[insertInd];
			cHeap[insertInd] = temp;

			trie.updateCar(cHeap[parent].getVin(), parent);
			trie.updateCar(cHeap[insertInd].getVin(), insertInd);

			insertInd = parent;
			parent = (int)Math.floor((insertInd - 1) / 2.0);

			swimByMiles(parent, insertInd, trie);
		} 
		else{
			trie.updateCar(cHeap[insertInd].getVin(), insertInd);
		}
	}

	//Helper function to sink by price
	private void sinkByPrice(int pInd, CarTrie trie){
		int leftInd = 2 * pInd + 1;
		int rightInd = 2 * pInd + 2;
		int compInd;
		Car leftCar, rightCar;

		if(leftInd >= cHeap.length){
			leftCar = null;
		}
		else{
			leftCar = cHeap[leftInd];
		}

		if(rightInd >= cHeap.length){
			rightCar = null;
		}
		else{
			rightCar = cHeap[rightInd];
		}

		if(leftCar == null && rightCar == null){
			trie.updateCar(cHeap[pInd].getVin(), pInd);
			return;
		}
		else if(leftCar != null && rightCar == null){
			compInd = leftInd;
		}
		else if(leftCar == null && rightCar != null){
			compInd = rightInd;
		}
		else{
			double leftPrice = cHeap[leftInd].getPrice();
			double rightPrice = cHeap[rightInd].getPrice();

			if(leftPrice <= rightPrice){
				compInd = leftInd;
			}
			else{
				compInd = rightInd;
			}
		}

		double parPrice = cHeap[pInd].getPrice();
		double chiPrice = cHeap[compInd].getPrice();

		if(parPrice > chiPrice){
			Car temp = cHeap[compInd];
			cHeap[compInd] = cHeap[pInd];
			cHeap[pInd] = temp;

			trie.updateCar(cHeap[compInd].getVin(), compInd);
			trie.updateCar(cHeap[pInd].getVin(), pInd);

			sinkByPrice(compInd, trie);
		}
		else{
			trie.updateCar(cHeap[pInd].getVin(), pInd);
		}
	}

	//Helper function to sink by miles
	private void sinkByMiles(int pInd, CarTrie trie){
		int leftInd = 2 * pInd + 1;
		int rightInd = 2 * pInd + 2;
		int compInd;
		Car leftCar, rightCar;

		if(leftInd >= cHeap.length){
			leftCar = null;
		}
		else{
			leftCar = cHeap[leftInd];
		}

		if(rightInd >= cHeap.length){
			rightCar = null;
		}
		else{
			rightCar = cHeap[rightInd];
		}

		if(leftCar == null && rightCar == null){
			trie.updateCar(cHeap[pInd].getVin(), pInd);
			return;
		}
		else if(leftCar != null && rightCar == null){
			compInd = leftInd;
		}
		else if(leftCar == null && rightCar != null){
			compInd = rightInd;
		}
		else{
			double leftMiles = cHeap[leftInd].getMiles();
			double rightMiles = cHeap[rightInd].getMiles();

			if(leftMiles <= rightMiles){
				compInd = leftInd;
			}
			else{
				compInd = rightInd;
			}
		}

		double parMiles = cHeap[pInd].getMiles();
		double chiMiles = cHeap[compInd].getMiles();

		if(parMiles > chiMiles){
			Car temp = cHeap[compInd];
			cHeap[compInd] = cHeap[pInd];
			cHeap[pInd] = temp;

			trie.updateCar(cHeap[compInd].getVin(), compInd);
			trie.updateCar(cHeap[pInd].getVin(), pInd);

			sinkByMiles(compInd, trie);
		}
		else{
			trie.updateCar(cHeap[pInd].getVin(), pInd);
		}
	}

	//Helper method to resize PQ as needed
	private Car[] resize(Car[] old){
		int newSize = old.length * 2;

		Car[] newHeap = new Car[newSize];

		for(int i = 0; i < old.length; i++){
			newHeap[i] = old[i];
		}

		return newHeap;
	}

	public String dumpHeap(){
		String heap = "";

		if(next == 0){
			heap = "Heap is empty";
		}
		else{
			if(mode == 0){
				for(int i = 0; i < next; i++){
					heap += ("$" + cHeap[i].getPrice() + " > " + cHeap[i].getMake() + " > " + cHeap[i].getModel() + "\n");
				}
			}
			else{
				for(int i = 0; i < next; i++){
					heap += ("-" + cHeap[i].getMiles() + " > " + cHeap[i].getMake() + " > " + cHeap[i].getModel() + "\n");
				}
			}
		}

		return heap;
	}
}