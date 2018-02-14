//Riley Marzka
//CS1501
//Project 1 (Password Cracker)
//Due: 2/10/17

import java.util.LinkedList;

public class PassDLB{

	//Initialize empty root node
	private Node root;
	private int n = 0; //number of keys

	//Empty constructor
	public PassDLB(){
		root = new Node();
	}

	//Adds string and value pair to DLB
	public void add(String key, double _val){
		//Start at root
		Node curr = root;
		int len = key.length();
		char c;

		//Traverse key, adding one character at a time
		for(int i = 0; i < len; i++){
			c = key.charAt(i);
			Node newNode = addChild(curr, c);
			curr = newNode;
		}

		//Set value of last node
		n++;
		curr.val = _val;
	}

	//Add child node with specified character
	private Node addChild(Node parent, char c){
		//Check if parent already has a child
		if(parent.child == null){
			//Parent does not have a child, so add a child and return it
			parent.child = new Node();
			parent.child.ch = c;
			return parent.child;
		}
		else{
			//Parent already has a child, so add a sibling and return it
			return addSibling(parent.child, c);
		}
	}

	//add sibling node wiht specified character
	private Node addSibling(Node fSib, char c){
		//Check if there is a node on this level
		if(fSib == null){
			//No node on this level, so create new node to be first sibling and return it
			fSib = new Node();
			fSib.ch = c;
			return fSib;
		}
		else{
			//Traverse to end of sibling list
			Node nSib = fSib;
			while(nSib.sib != null){
				//If a sibling exists with same character, then we need not add another
				if(nSib.ch == c){
					return nSib;
				}
				nSib = nSib.sib;
			}

			//At this point nSib is the las node on current level
			//So add a new node to the end of the sibling list and return
			nSib.sib = new Node();
			nSib.sib.ch = c;
			return nSib.sib;
		}
	}

	//Return total number of keys in trie
	public int getNumKeys(){
		return n;
	}

	//Returns value of given key
	public double getValue(String key){
		//Begin at root
		Node curr = root;
		int len = key.length();
		char c;
		//Travers trie, searching for one char at a time
		for(int i = 0; i < len; i++){
			c = key.charAt(i);
			curr = getChild(curr, c);
			//Key is not in trie
			if(curr == null){
				return -2;
			}
		}

		//Key is in trie
		//returns -1 if prefix
		//returns val if full key
		return curr.val;
	}

	//Search DLB for key
	public int search(String key){
		//Begin at root
		Node curr = root;
		int len = key.length();
		char c;
		//Traverse trie, searching for one char at a time
		for(int i = 0; i < len; i++){
			c = key.charAt(i);
			curr = getChild(curr, c);
			//Key is not a word or a prefix
			if(curr == null){
				return 0;
			}
		}

		//Key is a prefix, but not a word
		if(curr.val == -1){
			return 1;
		}
		//Key is a word, but not a prefix
		else if(curr.child == null){
			return 2;
		}
		//Key is a word and a prefix
		else{
			return 3;
		}
	}


	//Searches for node with ch = c in children of parent
	private Node getChild(Node parent, char c){
		return getSibling(parent.child, c);
	}

	//Searches for node with ch = c on current level
	private Node getSibling(Node fSib, char c){
		//Traverse to last node on current level
		Node curr = fSib;
		while(curr != null){
			//Check if node contains c
			if(curr.ch == c){break;}
			//If not, got to next sibling
			curr = curr.sib;
		}

		return curr;
	}

	//Return String[] of size numPwds, containing the set of valid passwords which
	//share a common longest prefix with the request String
	public String[] longPrefix(String request, int numPwds){
		//Start at root
		Node curr = root;
		Node prev = curr;

		StringBuilder longPref = new StringBuilder(request);
		String[] pwds = new String[numPwds];
		LinkedList<String> results = new LinkedList<String>();

		int pwdInd = 0;
		int len;
		char c;

		//Loop until array is full
		while(pwdInd < numPwds){
			len = longPref.length();

			//Check one character at a time to see if it is in the trie
			for(int i = 0; i < len; i++){
				prev = curr;
				c = longPref.charAt(i);
				curr = getChild(curr, c);
				if(curr == null){
					//If it is not in the trie, then delete the characters
					//in the stringbuilder from that point on
					longPref.delete(i, len);
					curr = prev;
					//Exit loop
					break;
				}
			}

			//collect all the valid password with same longest root
			collect(curr.child, longPref, results);

			//Fill String[] with passwords
			pwdInd = 0;
			for(int i = 0; i < results.size(); i++){
				if(pwdInd == 10){
					break;
				}
				pwds[pwdInd++] = results.get(i);
			}
			results.clear();
			curr = root;
			//Delete the last character in the stirngbuilder
			longPref = longPref.deleteCharAt(longPref.length()-1);
		}

		return pwds;
	}

	//Recursive method to collect all valid password which share the longest prefix
	//Places them in linkedlist
	private void collect(Node node, StringBuilder prefix, LinkedList<String> results){
		//Base Case 1
		if(node == null){
			return;
		}
		//Append character in current node to stringbuilder
		prefix.append(node.ch);
		//Base Case 2
		//If the node is the end of a key
		if(node.val != -1){
			//Add to linked list
			results.add(prefix.toString());
		}
		//Recurse for children
		collect(node.child, prefix, results);
		//Remove last character
		prefix.deleteCharAt(prefix.length()-1);
		//recurse for siblings
		collect(node.sib, prefix, results);
	}

	//Prints trie (for troubleshooting)
	public void printTrie(){
		Node curr = root.child;
		printSubTrie(curr);
	}

	private void printSubTrie(Node parent){
		StringBuilder sb = new StringBuilder();
		Node curr = parent;
		while(curr != null){
			sb = sb.append(curr.ch);
			if(curr.val > -1){
				System.out.println(sb.toString());
			}
			if(curr.sib != null){
				StringBuilder tb = new StringBuilder();
				int len;
				if(curr.val == -1 || curr.sib.child == null){
					len = sb.length()-1;
				}
				else{
					len = sb.length();
				}
				for(int i = 0; i < len; i++){
					tb = tb.append(sb.charAt(i));
				}
				printSibling(curr.sib, tb);
			}
			curr = curr.child;
		}
	}

	private void printSibling(Node parent, StringBuilder sb){
		Node curr = parent;
		while(curr != null){
			sb = sb.append(curr.ch);
			if(curr.val > -1){
				System.out.println(sb.toString());
			}
			if(curr.sib != null){
				StringBuilder tb = new StringBuilder();
				int len;
				if(curr.val == -1 || curr.sib.child == null){
					len = sb.length()-1;
				}
				else{
					len = sb.length();
				}
				for(int i = 0; i < len; i++){
					tb = tb.append(sb.charAt(i));
				}
				printSibling(curr.sib, tb);
			}
			curr = curr.child;
		}
	}

	private class Node{
		Node sib;
		Node child;
		char ch;
		double val = -1;
	}
}