//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17

//Class to store and manage information about cars:
//VIN, make, model, price, mileage, and color

import java.util.*;

public class Car{

	private String vin;
	private String make;
	private String model;
	private double price;
	private int miles;
	private String color;

	public Car(String v, String ma, String mo, double pr, int mi, String co){
		vin = v;
		make = ma;
		model = mo;
		price = pr;
		miles = mi;
		color = co;
	}

	public void setPrice(double pr){
		price = pr;
	}

	public void setMiles(int mi){
		miles = mi;
	}

	public void setColor(String co){
		color = co;
	}

	public String getVin(){
		return vin;
	}

	public String getMake(){
		return make;
	}

	public String getModel(){
		return model;
	}

	public double getPrice(){
		return price;
	}

	public int getMiles(){
		return miles;
	}

	public String getColor(){
		return color; 
	}
}