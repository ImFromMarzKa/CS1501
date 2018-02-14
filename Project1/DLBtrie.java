//Riley Marzka
//CS1501
//Project 1 (Password Cracker)
//Due: 2/10/17

import java.util.*;

public class DLBtrie{

	//Points to empty root node with no siblings or children
	Node rootN;

	//Constructor to initialize root
	public DLBtrie(){
		rootN = new Node();
	}

	//Add new string to DLB
	public void add(String str){
		//Begin at root
		Node currN = rootN;
		int len = str.length();

		//Traverse string, adding one character at a time
		for(int i = 0; i < len; i++){
			char c = str.charAt(i);
			Node newNode = addChild(currN, c);
			currN = newNode;

			//Check if character is replacable
			if(isReplacable(c)){
				//If it is, then add all possible replacement strings to DLB
				StringBuilder sb = new StringBuilder();

				if(c == 't'){sb = sb.append("7");}

				else if(c == 'a'){sb = sb.append("4");}

				else if(c == 'o'){sb = sb.append("0");}

				else if(c == 'e'){sb = sb.append("3");}

				else if(c == 'i' || c == 'l'){sb = sb.append("1");}

				else{sb = sb.append("$");}

				for(int j = i+1; j < len; j++){
					sb = sb.append(str.charAt(j));
				}
				addReplacement(sb, currN);
			}
		}
		//Set the 'end' value of last node added to true
		currN.end = true;
	}

	//Recursive method to add replacement strings
	private void addReplacement(StringBuilder str, Node start){
		//Begin at start
		Node currN = start;
		// boolean added = false;
		int len = str.length();

		//Traverse stringbuilder, adding one character at a time
		for(int i = 0; i < len; i++){
			char c = str.charAt(i);
			Node newNode;
			if(i == 0){
				newNode = addSibling(currN, c);
			}
			else{
				newNode = addChild(currN, c);
			}
			currN = newNode;

			//Check if character is replacable
			if(isReplacable(c)){
				//If it is, then add replacement strings
				StringBuilder sb = new StringBuilder();

				if(c == 't'){sb = sb.append("7");}

				else if(c == 'a'){sb = sb.append("4");}

				else if(c == 'o'){sb = sb.append("0");}

				else if(c == 'e'){sb = sb.append("3");}

				else if(c == 'i' || c == 'l'){sb = sb.append("1");}

				else{sb = sb.append("$");}

				for(int j = i+1; j < len; j++){
					sb = sb.append(str.charAt(j));
				}
				addReplacement(sb, currN);
			}
		}
		//Set 'end' value of last node to true
		currN.end = true;
	}

	//Prints Tree (for troubleshooting)
	public void printTree(){
		Node currN = rootN.child;
		printSubTree(currN);
	}

	//Prints subtree
	private void printSubTree(Node pNode){
		StringBuilder sb = new StringBuilder();
		Node currN = pNode;
		while(currN != null){
			sb = sb.append(currN.value);
			if(currN.end){
				int len = sb.length();
				for(int i = 0; i < len; i++){
					System.out.print(sb.charAt(i));
				}
				System.out.println();
			}
			if(currN.sibling != null){
				StringBuilder tb = new StringBuilder();
				int len;
				if(!currN.end){
					len = sb.length()-1;
				}
				else{
					if(currN.sibling.child == null){
						len = sb.length()-1;
					}
					else{len = sb.length();}
				}
				for(int i = 0; i < len; i++){
					tb = tb.append(sb.charAt(i));
				}
				printSibling(currN.sibling, tb);
			}
			currN = currN.child;
		}
	}

	//Prints sibling subtree
	private void printSibling(Node pNode, StringBuilder sb){
		Node currN = pNode;
		while(currN != null){
			sb = sb.append(currN.value);
			if(currN.end){
				int len = sb.length();
				for(int i = 0; i < len; i++){
					System.out.print(sb.charAt(i));
				}
				System.out.println();
			}
			if(currN.sibling != null){
				StringBuilder tb = new StringBuilder();
				int len;
				if(!currN.end){
					len = sb.length()-1;
				}
				else{
					if(currN.sibling.child == null){
						len = sb.length()-1;
					}
					else{len = sb.length();}
				}
				for(int i = 0; i < len; i++){
					tb = tb.append(sb.charAt(i));
				}
				printSibling(currN.sibling, tb);
			}
			currN = currN.child;
		}
	}

	//Search DLB for a String key
	public int search(String key){
		//Begin at root
		Node currN = rootN;
		int len = key.length();
		//Traverse tree searching for one char at a time
		for(int i = 0; i < len; i++){
			char c = key.charAt(i);
			currN = getChild(currN, c);
			//Key is not a word or a prefix
			if(currN == null){
				return 0;
			}
		}
		//Key is a prefix, but not a word
		if(currN.end == false){
			return 1;
		}
		//Key is a word, but not a prefix
		else if(currN.child == null){
			return 2;
		}
		//Key is a word and a prefix
		else{
			return 3;
		}
	}

	//Search DLB for a char[] key
	public int search(char[] key){
		//Begin at root
		Node currN = rootN;
		int len = key.length;
		char c;
		//Traverse tree searching for one char at a time
		for(int i = 0; i < len; i++){
			c = key[i];
			currN = getChild(currN, c);
			//Key is not a word or a prefix
			if(currN == null){
				return 0;
			}
		}
		//Key is a prefix, but not a word
		if(currN.end == false){
			return 1;
		}
		//Key is a word, but not a prefix
		else if(currN.child == null){
			return 2;
		}
		//Key is a word and a prefix
		else{
			return 3;
		}
	}

	//Adds a child to a parent node
	private Node addChild(Node pNode, char c){
		//Check is parent already has a child
		if(pNode.child == null){
			//Parent does not have a child, so add a child and return it
			pNode.child = new Node();
			pNode.child.value = c;
			return pNode.child;
		}
		else{
			//Parent already has a child, so add a sibling to the child and return it
			return addSibling(pNode.child, c);
		}
	}

	//Adds a sibling to the current level
	private Node addSibling(Node firstSib, char c){
		//Check if there is a node on this level
		if(firstSib == null){
			//No node on this level, so create new node to be the first sibling
			firstSib = new Node();
			firstSib.value = c;
			return firstSib;
		}
		else{
			//Traverse to end of sibling list
			Node nextSib = firstSib;
			while(nextSib.sibling != null){
				//If a sibling already contains the character,
				//then we need not add another with the same value
				if(nextSib.value == c){
					return nextSib;
				}
				nextSib = nextSib.sibling;
			}
			//At this point nextSib is the last node on the current level
			//so we add a new node to the end of the list and return
			nextSib.sibling = new Node();
			nextSib.sibling.value = c;
			return nextSib.sibling;
		}
	}

	//Searches for node with character c in children
	private Node getChild(Node pNode, char c){
		return getSibling(pNode.child, c);
	}

	//Searches for node with character c on current level
	private Node getSibling(Node firstSib, char c){
		//Traverse to last node on current level
		Node currN = firstSib;
		while(currN != null){
			//Check if node contains desired character
			if(currN.value == c) break;
			//If not, go to next
			currN = currN.sibling;
		}
		return currN;
	}

	//Return true if the character has replacement,
	//else returns false
	private boolean isReplacable(char c){
		if(c == 't' || c== 'a' || c == 'o' || c == 'e' || c == 'i' || c == 'l' || c == 's'){
			return true;
		}
		else{return false;}
	}

	//node in DLB
	private class Node{
		Node sibling;
		Node child;
		char value;
		boolean end = false;
	}
}