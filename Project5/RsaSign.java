//Riley Marzka
//CS1501
//Assignment 5 (RSA/Crypto)
//Due: 4/21/17 (Fri)

/*Signs files and verifies signatures:
	*Accepts command-line arguments
		* A flag to specificy whether to sign or to verify (s or v)
		* Name of the file to sign or verify
		* java RsaSign [s/v] filename.txt
	*If called to sign:
		* Generate SHA-256 hash of the contents of the specified file
		* Decrypt this hash value using the private key stored to privkey.rsa
			* Raise hash to d-th power mod n
		* Write out the signature to a file named as the original with an extra.sig
			* filename.txt.sig
	*If called to verify:	
		* Read contents of original file (filename.txt)
		* Generate SHA-256 hash of the contents of the original file
		* Read the signed hash of the original file from the corresponding .sig file (filename.txt.sig)
		* Encrypt this value with the key from pubkey.rsa
			* Raise to the e-th power mod n
		* Compare the hash value that was generated from filename.txt to the one that was recovered 
			from the signature
		* Print a message to the console indicating whether the signature is valid 
*/

import java.math.BigInteger;
import java.io.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;

public class RsaSign{


	public static void main(String[] args){

		//Sanity check number of commandline args
		if(args.length != 2){
			System.out.print("Invalid number of arguments.\nUsage: java RsaSign [s/v] filename.txt");
			System.exit(1);
		}

		char function = args[0].charAt(0);
		String filename = args[1];

		//Check function argument & call function accordingly
		if(function == 's'){
			sign(filename);
		}
		else if(function == 'v'){
			verify(filename);
		}
		else{
			System.out.println("Invalid function argument.\nUsage: java RsaSign [s/v] filename.txt");
			System.exit(1);
		}

		System.exit(0);
	}

	//Signs a file 
	private static void sign(String filename){

	//Generate SHA-256 hash of contents of specified file
		BigInteger hash = generateHash(filename);

	//"Decrypt" the hash value using private key
		//open RSA file
		File priv = new File("privkey.rsa");

		//read private key from RSA file
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(priv));
		} 
		catch(FileNotFoundException e){
			System.out.println("Error: privkey.rsa not in current directory");
			System.exit(1);
		}

		String dString = null;
		String nString = null;
		try{
			dString = br.readLine();
			nString = br.readLine();
		}
		catch(IOException e){
			System.out.println("Error on reading private key from privkey.rsa");
			System.exit(1);
		}
		if(dString == null || nString == null){
			System.out.println("privkey.rsa improporly encoded");
			System.exit(1);
		}

		//create big ints for key
		BigInteger d = new BigInteger(dString);
		BigInteger n = new BigInteger(nString);

		//"decrypt" hash
		BigInteger decHash = hash.modPow(d, n);

	//Write out signature to file
		filename = filename.concat(".sig");
		File sig = new File(filename);
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(sig));
			bw.write(decHash.toString());
			bw.close();
		}
		catch(IOException e){
			System.out.println("Error on writing to .sig file");
			System.exit(1);
		}

	}

	//Verifies a signed file
	private static void verify(String filename){
	
	//Generate SHA-256 hash of contents of specified file
		BigInteger hash = generateHash(filename);

	//Read signed hash
		filename = filename.concat(".sig");
		File sig = new File(filename);

		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(sig));
		}
		catch(FileNotFoundException e){
			System.out.println("Error: .sig file not in current directory");
			System.exit(1);
		}

		String hashStr = null;
		try{
			hashStr = br.readLine();
		}
		catch(IOException e){
			System.out.println("Error on reading signed hash from .sig file");
			System.exit(1);
		}
		if(hashStr == null){
			System.out.println(".sig file empty");
			System.exit(1);
		}

		BigInteger signedHash = new BigInteger(hashStr);

	//"Encrypt" signed hash with public key
		File pub = new File("pubkey.rsa");
		try{
			br = new BufferedReader(new FileReader(pub));
		}
		catch(FileNotFoundException e){
			System.out.println("Error: pubkey.rsa not in current directory");
			System.exit(1);
		}

		String eString = null;
		String nString = null;
		try{
			eString = br.readLine();
			nString = br.readLine();
		}
		catch(IOException e){
			System.out.println("Error on reading public key from pubkey.rsa");
			System.exit(1);
		}
		if(eString == null || nString == null){
			System.out.println("pubkey.rsa improporly encoded");
			System.exit(1);
		}

		BigInteger e = new BigInteger(eString);
		BigInteger n = new BigInteger(nString);

		signedHash = signedHash.modPow(e, n);

	//Check against generated hash and print out 
		if(hash.equals(signedHash)){
			System.out.println("The signature is valid!!!\n Goodbye!");
		}
		else{
			System.out.println("The signature is not valid!\nBe careful out there!");
		}
	}

	//Helper method to generate hash for the contents of specified file
	private static BigInteger generateHash(String filename){
		byte[] hashArr = null;
		//Lazily catch exceptions
		try{
			//read in the file to hash
			Path path = Paths.get(filename);
			byte[] data = Files.readAllBytes(path);

			//create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			//process file
			md.update(data);
			//generate hash of file
			hashArr = md.digest();
		} 
		catch(Exception e){
			System.out.println(e.toString());
		}

		return new BigInteger(hashArr);
	}
}