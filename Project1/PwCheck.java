//Riley Marzka
//CS1501
//Project 1 (Password Cracker)
//Due: 2/10/17

import java.io.*;
import java.lang.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class PwCheck{

	public static void main(String[] args) throws IOException{
		//Check command line argument
		//and call appropriate method
		if(args[0].compareTo("-find") == 0){
			find();
		}

		else if(args[0].compareTo("-check") == 0){
			//Verify that program has been run with -find already
			File verify = new File("all_passwords.txt");
			if(verify.isFile()){
				check();
			}
			else{
				System.out.println("Must run program with command line argument '-find'\nBefore running with '-check");
				System.exit(0);
			}
		}
		else{
			System.out.println("Invalid command line argument");
			System.exit(0);
		}	
	}

	//Check Method:
	//Populates symbol table with all valid passwords
	//Prompts user to input passwords until they wish to stop
	//If password valid, program tells user how long it took to crack
	//If password invalid, program gives user 10 passwords with longest common prefix
	//with password entered
	private static void check() throws IOException{
		//Create new PassDLB to store passwords and times
		PassDLB allPwds = new PassDLB();

		//Read one line at a time with BufferedReader
		BufferedReader br = new BufferedReader(new FileReader("all_passwords.txt"));
		String line = null;
		while((line = br.readLine()) != null){
			//Split each line into a String[] around comma
			String[] split = line.split(",");

			//split[0] = password
			//split[1] = time
			//Add each key (password) and value (time) to symbol table
			allPwds.add(split[0], Double.parseDouble(split[1]));
		}
		br.close();

		//Create a scanner for receiving user input
		Scanner sc = new Scanner(System.in);
		String input = "Initialize";
		double value;
		int searchRet;

		//Print Welcome Message
		System.out.println("Welcome to the password checker!\nEnter passwords and I will determine whether or not they are valid.\n(Enter 'quit' when finished)\nLet's begin>>>\n");
		
		//Loop until user chooses to 'quit'
		while(true){
			System.out.println("Please enter a password>>>");
			input = sc.nextLine();
			
			//Change input to lower case
			input = input.toLowerCase(Locale.US);

			if(input.compareTo("quit") == 0){
				break;
			}

			//Search for key in allPwds
			value = allPwds.getValue(input);
			//If negative value, password not valid
			if(value < 0){
				//Fill string[] with 10 passwords with common prefix
				String[] alts = allPwds.longPrefix(input, 10);
				System.out.println("\n\nSorry, that password is invalid!\nLet me give you some alternative passwords\nwhich share the longest prefix with what you entered>>>\n");
				//Print passwords and time to solve
				for(int i = 0; i < 10; i++){
					System.out.println("Password: " + alts[i] + "	Solved in: " + allPwds.getValue(alts[i]) + "ms");
				}
				System.out.println("\n");
			}
			//If positive value, then valid password
			else{
				System.out.println("\nThat is a valid password!\nIt took me " + value + " milliseconds to crack that password.\n\n");
			}
		}
	}

	//Find Method:
	//Populates DLB with strings that cannot be in passwords
	//Generates list of all possible passwords, and writes them to all_passwords
	private static void find() throws IOException{
		/*Step 1: 
		Populate a DLB trie with strings that cannot 
		be contained within use passwords*/
		DLBtrie dicDLB = new DLBtrie();

		BufferedReader br = new BufferedReader(new FileReader("dictionary.txt"));
		String line = null;
		//Read one line at a time until the end of the file
		while((line = br.readLine()) != null){
			//Check length of string
			//Prune anything that is > 5 characters
			int len = line.length();
			if(len <= 5){
				//If length within bounds, then add string to DLB
				dicDLB.add(line);
			}
		}
		br.close();

		/*Step 2:
		Use exhaustive search and pruning rules
		to find all valid passwords
		Notes:
			-ASCII Ranges:
				Letters (1-3): 97-104; 106-122 (a = 96 i = 105 which cannot be used)
				Numbers (1-2): 48; 50-51; 53-57 (1 = 49 4 = 52 which cannot be used)
				Symbols (1-2): 33; 36; 42; 64; 94; 95*/

		//Create array to store all valid characters
		int valLet = 24; //letter indices are 0-23
		int lastLet = valLet - 1;

		int valNum = 8;	//number indices are 24-31
		int lastNum = valLet + valNum - 1;

		int valSym = 6;	//symbol indices are 30-35
		int len = valLet + valNum + valSym; //total length of array
		int lastSym = len - 1;

		int ascii = 98;
		char[] allChars = new char[len];
		//Fill in letters
		for(int i = 0; i <= lastLet; i++){
			//Skip 'i'
			if(ascii == 105){ascii++;}
			allChars[i] = (char)ascii++;
		}

		ascii = 48;
		//Fill in numbers
		for(int i = lastLet + 1; i <= lastNum; i++){
			//Skip '1' and '4'
			if(ascii == 49 || ascii == 52){ascii++;}
			allChars[i] = (char)ascii++;
		}

		//Fill in symbols
		int[] symAscii = new int[] {33, 36, 42, 64, 94, 95};
		int j = 0;
		for(int i = lastNum + 1; i <= lastSym; i++){
			allChars[i] = (char)symAscii[j++];
		}

		//Generate list of all possible passwords
		long numPw = 0;
		int numLets, numNums, numSyms, length, lastInd, subInd, p = 0;
		boolean valid, found;
		char[] subPass, pass = new char[5];
		String str = null;
		File allPwd = new File("all_passwords.txt");
		PrintWriter wr = new PrintWriter(allPwd);
		long startTime = System.nanoTime(), elapsed;
		double milliSecs = 0;
		for(char a: allChars){
			for(char b: allChars){
				for(char c: allChars){
					for(char d: allChars){
						for(char e: allChars){
							pass[0] = a;
							pass[1] = b;
							pass[2] = c;
							pass[3] = d;
							pass[4] = e;

							//Count number of letters, numbers, and symbols
							//prune accordingly
							numLets = 0;
							numNums = 0;
							numSyms = 0;
							for(char f: pass){
								if(f <= 'z' && f >= 'b'){
									numLets++;
								}
								else if(f <= '9' && f >= '0'){
									numNums++;
								}
								else{
									numSyms++;
								}
							}
							//if less than  1, or more than 3 letters > invalid
							if(numLets < 1 || numLets > 3){
								valid = false;
							}
							//if less than 1, or more than 2 numbers > invalid
							else if(numNums < 1 || numNums > 2){
								valid = false;
							}
							//if less than 1, or more than 2 symbols > invalid
							else if(numSyms < 1 || numSyms > 2){
								valid = false;
							}
							else{
								//Search dictionary for full password
								if(dicDLB.search(pass) > 1){
									valid = false;
								}
								else{
									//Search dictionary for substrings of lengths 4, 3, and 2
									found = false;
									//Start with length 4 substrings
									length = 4;
									lastInd = 1;

									//Loop until either found, or searched all substrings
									while(!found && length > 1 && lastInd < 5){
										subPass = new char[length];
										for(int i = 0; i <= lastInd; i++){
											subInd = 0;
											for(j = i; j < i + length; j++){
												subPass[subInd++] = pass[j];	
											}
											if(dicDLB.search(subPass) > 1){
												found = true;
												break;
											}
										}
										lastInd++;
										length--;
									}

									//If a substring was found in dictionary > invalid
									if(found){
										valid = false;
									}
									//else > password is valid
									else{
										valid = true;
									}
								}
							}

							if(valid){
								//Calculate time to crack
								elapsed = System.nanoTime() - startTime;
								milliSecs = (double)elapsed / (double)1000000;
								str = new String(pass);
								numPw++;
								//Write to file
								wr.println(str + "," + String.valueOf(milliSecs));
							}
						}
					}
				}
			}
		}
		wr.close();
		milliSecs = (double)(System.nanoTime() - startTime)/1000000.0;
		System.out.println("Found " + numPw + " passwords in " + milliSecs + " milliseconds!\n");
	}
}