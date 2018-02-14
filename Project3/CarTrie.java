//Riley Marzka
//CS1501
//Project 3 (Indexed PQ)
//Due: 3/18/17

//R-Way trie of Cars to be used as indirection table for CarHeap
//Indexed by VIN

import java.util.*;

public class CarTrie{
	private static final int R = 36; //Alphabet size fo VIN

	private Node root; //Root of trie
	private int n; //Number keys

	//Value is index of car within CarHeap
	private class Node{
		private int heapIndex;
		private Node[] next = new Node[R];
	}

	public CarTrie(){
	}

	//Is the VIN present in trie?
	public boolean contains(String vin){
		return getIndex(vin) != -1;
	}

	//Get the index of the car with the specified VIN
	public int getIndex(String vin){
		Node x = get(root, vin, 0);
		if(x == null){return -1;}
		return (int) x.heapIndex;
	}

	//Helper function to get the node that contains the value (index) 
	//of VIN vin
	private Node get(Node node, String vin, int d){
		if(node == null){return null;}
		if(d == vin.length()){return node;}
		char c = vin.charAt(d);
		int ind = charToInd(c);
		return get(node.next[ind], vin, d+1);
	}

	//Update a car with VIN vin with value index
	public void updateCar(String vin, int index){
		if(index == -1){delete(root, vin, 0);}
		else{root = add(root, vin, index, 0);}
	}

	//Helper funtion to add a new VIN to the trie
	private Node add(Node node, String vin, int index, int d){
		if(node == null){node = new Node();}
		if(d == vin.length()){
			if(node.heapIndex == -1){n++;}
			node.heapIndex = index;
			return node;
		}
		char c = vin.charAt(d);
		int ind = charToInd(c);
		node.next[ind] = add(node.next[ind], vin, index, d+1);
		return node;
	}

	//Delete car with specified VIN
	public void deleteCar(String vin){
		root = delete(root, vin, 0);
	}

	//Helper function to delete a VIN
	private Node delete(Node node, String vin, int d){
		if(node == null){return null;}
		if(d == vin.length()){
			if(node.heapIndex != -1){n--;}
			node.heapIndex = -1;
		}
		else{
			char c = vin.charAt(d);
			int ind = charToInd(c);
			node.next[ind] = delete(node.next[ind], vin, d+1);
		}

		//Remove subtree rooted at x if it is empty
		if(node.heapIndex != -1){return node;}
		for(int i = 0; i < R; i++){
			if(node.next[i] != null){return null;}
		}
		return null;
	}

	//Returns the size of the trie
	public int size(){
		return n;
	}

	//Returns true if trie is of size 0, false otherwise
	public boolean isEmpty(){
		return size() == 0;
	}

	//Converts a character to an index in array 
	private int charToInd(char c){
		if(c >= 48 && c <= 57){
			return c - 48;
		}
		else if(c >= 65 && c <= 90){
			return c - 55;
		}
		else{
			throw new IllegalArgumentException("VIN contains invalid character");
		}
	}
}