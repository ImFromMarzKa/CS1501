//Riley Marzka
//CS1501
//Assignment 5 (RSA/Crypto)
//Due: 4/21/17 (Fri)

/*Program to generate RSA keypairs:
	* Pick p and q (2 random prime numbers)
	* Calculate n as p*q
	* Calculate tochent(n) as (p-1)*(q-1)
	* Choose an e such that 1 < e < t(n) and gcd(e, t(n)) = 1
	* Determine d such that d = e^(-1) mod t(n)
	* Save e and n to pubkey.rsa and d and n to privkey.rsa
 ** MAY NOT USE BIG INTEGER CLASS
 	* Store integers to memory as raw integers represented as byte arrays
*/

import java.math.BigInteger;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class RsaKeyGen{

	public static void main(String[] args) throws IOException{

		//Pick primes
		 BigInteger p = BigInteger.probablePrime(256, new Random());
		 BigInteger q = BigInteger.probablePrime(256, new Random());

		//Calculate n = p * q
		 BigInteger n = p.multiply(q);

		//Calculate tochent(n) = (p-1) * (q-1)
		BigInteger pS = p.subtract(BigInteger.ONE);
		BigInteger qS = q.subtract(BigInteger.ONE);
		BigInteger tN = pS.multiply(qS);

		//Choose a value for e
		BigInteger e = chooseE(tN);

		//Check that e was calculated
		if(e.equals(new BigInteger("-1"))){
			System.exit(1); //Exit on error
		}

		//Determine d = e^(-1) mod t(n)
		// BigInteger d = detD(e, tN);
		BigInteger d = e.modInverse(tN);


		//Save values to files;
		File pub = new File("pubkey.rsa");
		File priv = new File("privkey.rsa");
		BufferedWriter w;

		
		//Write e and n to pubkey.ras, separated by a new line
		w = new BufferedWriter(new FileWriter(pub));
		w.write(e.toString());
		w.newLine();
		w.write(n.toString());
		w.close();

		//Write d and n to privkey.rsa, separated by a new line
		w = new BufferedWriter(new FileWriter(priv));
		w.write(d.toString());
		w.newLine();
		w.write(n.toString());
		w.close();

		System.exit(0);
	}



	/*Helper to choose a value for e
		* Parameters:
			* toch = tochent(n)
		* Returns:
			* e on success
			* -1 on failure
	*/
	private static BigInteger chooseE(BigInteger toch){
		//Begin with e = 11 and go from there
		BigInteger e = new BigInteger("11");

		//Continue searching for an e such that:
		//	GCD(toch, e) = 1
		//	1 < e < toch
		while(!e.gcd(toch).equals(BigInteger.ONE) && e.equals(e.min(toch)) && !e.equals(toch)){
			e = e.nextProbablePrime();
		}

		//Check that e was calculated
		if(!e.equals(e.min(toch)) || e.equals(toch)){
			System.out.println("\n>>>Could not calculate e!");
			e = new BigInteger("-1");
		}

		return e;
	}
}