//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17

//Main driver program


import java.util.*;

public class CarTracker{
	
	//Create one heap sorted by price, and one sorted by mileage
	private static CarHeap priceHeap = new CarHeap(0);
	private static CarHeap milesHeap = new CarHeap(1);

	//Create one trie for each of the heaps
	private static CarTrie priceTrie = new CarTrie();
	private static CarTrie milesTrie = new CarTrie();

	//Hash table to sort cars by make/model
	private static CarHT mmGroups = new CarHT();

	private static Scanner sc = new Scanner(System.in);

	public static void main(String args[]){
		
		int in = 0;

		//Intro Greeting
		printGreeting();

		//8 = Quit
		while(in != 8){

			//Print options and get input
			printOptions();
			in = sc.nextInt();
			sc.nextLine(); //clear new line feed

			//Switch on input and call appropriate helper method
			switch(in){
				case 1:
					addCar();
					break;
				case 2:
					updateCar();
					break;
				case 3:
					removeCar();
					break;
				case 4:
					lowPrice();
					break;
				case 5:
					lowMiles();
					break;
				case 6:
					lowPriceMM();
					break;
				case 7:
					lowMilesMM();
					break;
				case 8:
					break;
				default:
					System.out.println("\n\n>>> The number you entered is invalid,\nplease enter a number between 1 and 8\n\n");
					break;
			}
		}
		System.exit(0);
	}

	//Helper Function to add a new car
	//Prompts user for:
	// VIN, make, model, price, miles, color
	private static void addCar(){
		double price;
		int miles;
		String vin = "", make = "", model = "", color = "";
		boolean valid = false;

		System.out.println("\n-----------------");
		System.out.println(">Please Enter Vehicle Information:");
		System.out.println("-----------------");

		//Loop until valid VIN entered
		while(!valid){
			System.out.println("VIN Number>>> ");
			vin = sc.nextLine().toUpperCase(); //convert to upper case

			if(!checkVin(vin)){
				System.out.println(">>>Invalid VIN! VIN's must be 17 characters and contain only 0-9 and A-Z\n");
			}
			else{
				valid = true;
			}
		}
		
		valid = false;
		//Loop until something entered for Make
		while(!valid){
			System.out.println("\nMake>>> ");
			make = sc.nextLine();
			if(make.length() == 0){
				System.out.println(">>>Make cannot be left blank!");
			}
			else{
				valid = true;
			}
		}

		valid = false;
		//Loop until something entered for Model
		while(!valid){
			System.out.println("\nModel>>> ");
			model = sc.nextLine();
			if(model.length() == 0){
				System.out.println(">>>Model cannot be left blank!");
			}
			else{
				valid = true;
			}
		}
		
		System.out.println("\nPrice>>> ");
		String priceStr = sc.nextLine();

		//Try to parse price
		try{
			price = Double.parseDouble(priceStr);
		}
		//Upon failure, default to 0
		catch(NumberFormatException nfe){
			System.out.println(">>>Price invalid! Default to 0");
			price = 0.0;
		}
		if(price < 0.0){
			System.out.println(">>>Price invalid! Default to 0");
			price = 0.0;
		}

		System.out.println("\nMileage>>> ");
		String mileStr = sc.nextLine();
		//Try to parse miles
		try{
			miles = Integer.parseInt(mileStr);
		}
		//Upon failure, default to 0
		catch(NumberFormatException nfe){
			System.out.println(">>>Mileage invalid! Default to 0");
			miles = 0;
		}
		if(miles < 0){
			System.out.println(">>>Mileage invalid! Default to 0");
			miles = 0;
		}

		System.out.println("\nColor>>> ");
		color = sc.nextLine();

		//Create new car with specified info
		Car newCar = new Car(vin, make, model, price, miles, color);

		//Add new car to both heaps, tries, and hash table
		priceHeap.addCar(newCar, priceTrie);
		milesHeap.addCar(newCar, milesTrie);
		mmGroups.insert(newCar);

		System.out.println("\n>>>Sucessfully added " + make + " " + model +" with VIN " + vin + "!\n\n");
	}

	//Helper Function to update a car
	//Prompts user for VIN
	//Updates what was requested
	private static void updateCar(){
		System.out.println("-----------------");
		System.out.println("Please Enter VIN of Vehicle to Update>>> ");
		String vin = sc.nextLine().toUpperCase(); //convert to upper case

		System.out.println("-----------------");

		//Check for valid VIN, if invalid return to main menu
		if(!checkVin(vin)){
			System.out.println(">>>Invalid VIN!\n");
			return;
		}

		System.out.println("Would you like to update: ");
		System.out.println("1) Price");
		System.out.println("2) Mileage");
		System.out.println("3) Color");
		System.out.println("4) Cancel");
		int up = sc.nextInt();
		sc.nextLine(); //Clear scanner

		if(up < 1 || up > 4){
			System.out.println(">>>Invalid option, returning to main menu\n");
			return;
		}
		if(up == 4){
			System.out.println(">>>Returning to main menu\n");
			return;
		}

		//Get heap indeces for car with VIN vin
		int priceInd = priceTrie.getIndex(vin);
		int milesInd = milesTrie.getIndex(vin);

		if(priceInd < 0 || milesInd < 0){
			System.out.println(">>>No vehicle with VIN " + vin + " was found in database\n");
			return;
		}

		Car c;

		//Switch on selection, and update accordingly
		switch(up){
			case 1:
				System.out.println("Please enter updated price>>> ");
				String priceStr = sc.nextLine();
				double price;
				try{
					price = Double.parseDouble(priceStr);
				}
				catch(NumberFormatException nfe){
					System.out.println(">>>Invalid price! Default to 0\n");
					price = 0.0;
				}

				//Get car from price heap at price index
				c = priceHeap.getCar(priceInd);

				//Update price of car
				c.setPrice(price);
				//Update car in price heap
				priceHeap.setCar(c, priceInd);
				//Heapify price heap
				priceHeap.checkUp(priceInd, priceTrie);
				//Update car in miles heap
				milesHeap.setCar(c, milesInd);
				//Update car in hash table
				mmGroups.updatePrice(c, price);

				System.out.println("\nSuccessfully updated price of " + c.getMake() + " " + c.getModel() + " with VIN " + vin + "!\n\n");
				break;

			case 2:
				System.out.println("Please enter updated mileage>>> ");
				String mileStr = sc.nextLine();
				int miles;
				try{
					miles = Integer.parseInt(mileStr);
				}
				catch(NumberFormatException nfe){
					System.out.println(">>>Invalid mileage! Default to 0\n");
					miles = 0;
				}

				//Get car from miles heap at milesInd
				c = milesHeap.getCar(milesInd);
				//Update mileage of car
				c.setMiles(miles);
				//Update car in miles heap
				milesHeap.setCar(c, milesInd);
				//Heapify miles heap
				milesHeap.checkUp(milesInd, milesTrie);
				//Update car in price heap
				priceHeap.setCar(c, priceInd);
				//Update car in hash table
				mmGroups.updateMiles(c, miles);

				System.out.println("\nSuccessfully updated mileage of " + c.getMake() + " " + c.getModel() + " with VIN " + vin + "!\n\n");
				break;

			default:
				System.out.println("Please enter updated color>>> ");
				String color = sc.nextLine();

				//Get car from price heap at priceInd
				c = priceHeap.getCar(priceInd);
				//Update color of car
				c.setColor(color);
				//Update car in price and miles heap
				priceHeap.setCar(c, priceInd);
				milesHeap.setCar(c, milesInd);
				//Update car in hash table
				mmGroups.updateColor(c, color);

				System.out.println("\nSuccessfully updated color of " + c.getMake() + " " + c.getModel() + " with VIN " + vin + "!\n\n");
				break;
		}
	}

	//Helper function to remove a car
	//Prompts for VIN, removes car with VIN from data structures
	private static void removeCar(){
		System.out.println("-----------------");
		System.out.println("Please Enter VIN of Vehicle to Remove>>> ");
		String vin = sc.nextLine().toUpperCase();
		System.out.println("-----------------");

		//Check for valid vin, if invalid return to main menu
		if(!checkVin(vin)){
			System.out.println(">>>Invalid VIN!\n");
			return;
		}

		//Get index of car with VIN from trie by price
		int ind = priceTrie.getIndex(vin);
		if(ind < 0){
			System.out.println(">>>No vehicle with VIN " + vin + " was found in database\n");
			return;
		}

		//Get car from price heap at ind
		Car c = priceHeap.getCar(ind);
		//Delete car from trie
		priceTrie.deleteCar(vin);
		//Delete car from price heap
		priceHeap.removeCar(ind, priceTrie);

		//Get index of car with VIN from trie by miles
		ind = milesTrie.getIndex(vin);
		//Delete car from trie
		milesTrie.deleteCar(vin);
		//Delete car from miles heap
		milesHeap.removeCar(ind, milesTrie);
		//Delete car from ahsh table
		mmGroups.remove(c);

		System.out.println("\nSuccessfully removed " + c.getMake() + " " + c.getModel() + " with VIN " + vin + " from consideration!\n");
		return;
	}

	//Helper method to get lowest priced car
	private static void lowPrice(){

		//Get highest priority car from prices heap
		Car c = priceHeap.getMin();

		if(c == null){
			System.out.println(">>>There are no cars in the database\n");
			return;
		}
		System.out.println("Lowest Priced Car:");
		printCar(c);
	}

	//Helper method to get car with lowest mileage
	private static void lowMiles(){

		//Get highest priority car from miles heap
		Car c = milesHeap.getMin();

		if(c == null){
			System.out.println(">>>There are no cars in the database\n");
			return;
		}
		System.out.println("Lowest Mileage Car:");
		printCar(c);
	}

	//Helper method to get lowest priced car of specified make and model
	private static void lowPriceMM(){
		System.out.println("-----------------");
		System.out.println("Please Enter Make of Car>>> ");
		String make = sc.nextLine();
		if(make.length() == 0){
			System.out.println(">>>Make cannot be blank!\n");
			return;
		}

		System.out.println("\nPlease Enter Model of Car>>> ");
		String model = sc.nextLine();
		if(model.length() == 0){
			System.out.println(">>>Model cannot be blank!\n");
			return;
		}

		System.out.println("-----------------");

		//Get list of cars with specified make and model from hash table
		CarList list = mmGroups.getCars(make, model);

		if(list == null){
			System.out.println(">>>There are no cars of that make and/or model in the database\n");
			return;
		}

		//Get minimum priced car from list
		Car c = list.getMin(0);
		if(c == null){
			System.out.println(">>>There are no cars of that make and/or model in the database\n");
			return;
		}

		System.out.println("Lowest price " + make + " " + model + ":");
		printCar(c);
	}

	//Helper method to get car with lowest miles of specified make and model
	private static void lowMilesMM(){
		System.out.println("-----------------");
		System.out.println("Please Enter Make of Car>>> ");
		String make = sc.nextLine();
		if(make.length() == 0){
			System.out.println(">>>Make cannot be blank!\n");
			return;
		}

		System.out.println("\nPlease Enter Model of Car>>> ");
		String model = sc.nextLine();
		if(model.length() == 0){
			System.out.println(">>>Model cannot be blank!\n");
			return;
		}

		System.out.println("-----------------");

		//Get list of car with specified make and model
		CarList list = mmGroups.getCars(make, model);

		if(list == null){
			System.out.println(">>>There are no cars of that make and/or model in the database\n");
			return;
		}

		//Get car with least miles from list
		Car c = list.getMin(1);
		if(c == null){
			System.out.println(">>>There are no cars of that make and/or model in the database\n");
			return;
		}

		System.out.println("Lowest mileage " + make + " " + model + ":");
		printCar(c);
	}

	//Helper method to print intro greeting
	private static void printGreeting(){
		System.out.println("Welcome to the car Buying database!");
		System.out.println("Some notes befroe you begin>>>");
		System.out.println("1) VIN's must be 17 characters long, containing only 0-9 and A-Z");
		System.out.println("2) Make and Model cannot be left blank");
		System.out.println("3) Prices must be entered as doubles, like this > 1000.00");
		System.out.println("4) Mileage must be entered as and integer");
		System.out.println("Let's begin!\n------------------------\n\n");
	}

	//Helper method to print options menu
	private static void printOptions(){
		System.out.println("Please Select an Action:");
		System.out.println("1) Add a car");
		System.out.println("2) Update a car");
		System.out.println("3) Remove a car");
		System.out.println("4) Get lowest priced car");
		System.out.println("5) Get lowest mileage car");
		System.out.println("6) Get lowest priced car by make and model");
		System.out.println("7) Get lowest mileage car by make and model");
		System.out.println("8) Quit\n");
	}

	//Helper method to print the information for a car
	private static void printCar(Car c){
		System.out.println("VIN: " + c.getVin());
		System.out.println("Make: " + c.getMake());
		System.out.println("Model: " + c.getModel());
		System.out.println("Price: " + c.getPrice());
		System.out.println("Mileage: " + c.getMiles());
		System.out.println("Color: " + c.getColor() + "\n");
	}

	//Helper method to check for valid vin
	private static boolean checkVin(String vin){
		if(vin.length() != 17){
			return false;
		}
		for(int i = 0; i < vin.length(); i++){
			char c = vin.charAt(i);
			//If character not in valid set, return false
			if(c < 48 || c > 90){
				return false;
			}
			if(c > 57 && c < 65){
				return false;
			}
		}
		return true;
	}
}
