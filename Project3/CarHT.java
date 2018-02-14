//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17

//Hash table implemented with separate chaining
//Used to quickly grab list of cars with specified make and model

public class CarHT{

	private CarList[] cars;
	private int m;
	private int n;

	//Creates new CarHT of size 911
	public CarHT(){
		m = 911; //Large prime
		cars = new CarList[m];
		n = 0;
	}

	//Insert a new car into hash table
	public void insert(Car c){
		int hashInd = getHashInd(c);
		put(c, hashInd);
	}

	//Remove a car from the hash table
	public void remove(Car c){
		int hashInd = getHashInd(c);
		delete(c, hashInd);
	}

	//Update price of car c to pr
	//Return updated car
	public Car updatePrice(Car c, double pr){
		Car car = c;
		int hashInd = getHashInd(car);
		car.setPrice(pr);
		CarList list = cars[hashInd];
		list.update(car);
		return car;
	}

	//Update mileage of car c to mi
	//Return updated car
	public Car updateMiles(Car c, int mi){
		Car car = c;
		int hashInd = getHashInd(car);
		car.setMiles(mi);
		CarList list = cars[hashInd];
		list.update(car);
		return car;
	}

	//Update color of car c to co
	//Return updated car
	public Car updateColor(Car c, String co){
		Car car = c;
		int hashInd = getHashInd(car);
		car.setColor(co);
		CarList list = cars[hashInd];
		list.update(car);
		return car;
	}

	//Return list of car with specified make and model
	public CarList getCars(String make, String model){
		int hashInd = getHashInd(make, model);
		return cars[hashInd];
	}

	//Helper method to get the hash index for car c
	//Hashed by make and model
	private int getHashInd(Car c){
		String make = c.getMake();
		String model = c.getModel();

		make = make.substring(0, 1);

		while(model.length() < 5){
			model = model.concat(" ");
		}

		model = model.substring(0, 5);

		String name = make.concat(model);

		long hash = 0;

		for(int i = 0; i < name.length(); i++){
			hash *= 256;
			hash += (int)name.charAt(i);
		}

		return (int)(hash % (long)m);
	}

	//Helper funtion to get hash index using make and model
	//Same as above
	private int getHashInd(String make, String model){
		make = make.substring(0, 1);

		while(model.length() < 5){
			model = model.concat(" ");
		}

		model = model.substring(0, 5);

		String name = make.concat(model);

		long hash = 0;

		for(int i = 0; i < name.length(); i++){
			hash *= 256;
			hash += (int)name.charAt(i);
		}

		return (int)(hash % (long)m);
	}

	//Helper function to put a new car into hash table
	private void put(Car c, int i){
		if(cars[i] == null){
			cars[i] = new CarList();
		}

		CarList list = cars[i];
		list.add(c);
		cars[i] = list;
	}

	//Helper function to delete a car form hash table
	private void delete(Car c, int i){
		CarList list = cars[i];
		list.remove(c);
		cars[i] = list;
	}
}