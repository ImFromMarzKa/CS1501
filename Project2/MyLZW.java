//Riley Marzka
//CS1501
//Project2 (LZW)
//2/26/17

/******************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - < input.txt   (compress)
 *  Execution:    java MyLZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   http://algs4.cs.princeton.edu/55compression/abraLZW.txt
 *                http://algs4.cs.princeton.edu/55compression/ababLZW.txt
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.TST;

import java.lang.*;

/**
 *  The {@code MyLZW} class provides static methods for compressing
 *  and expanding a binary input using LZW compression over the 8-bit extended
 *  ASCII alphabet with 12-bit codewords.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/55compress">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MyLZW {
    private static final int Min = 9;       //Min codeword size
    private static final int Max = 16;      //Max codeword size

    private static int W = Min;         // codeword width
    private static int R = 256;        // number of input chars

    // Do not instantiate.
    private MyLZW() { }

    /**
     * Reads a sequence of 8-bit bytes from standard input; compresses
     * them using LZW compression with 12-bit codewords; and writes the results
     * to standard output.
     */
    public static void compress(char mode) {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        //For monitoring
        int compBits = 0; // Integer to keep track of number of compressed bits
        int procBits = 0; // Integer to keep track of uncompressed bits
        float firstRatio = 0;
        float currRatio = 0;
        boolean isMonitoring = false;

        //Write compression mode to output
        switch(mode){
            case 'n': // 0 = Do Nothing Mode
                BinaryStdOut.write(0, 2);
                break;
            case 'r': // 1 = Reset Mode
                BinaryStdOut.write(1, 2);
                break;
            case 'm': // 2 = Monitor Mode
                BinaryStdOut.write(2, 2);
                break;
        }

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.

            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            compBits += W; //Update compressed bits

            int t = s.length();
            procBits += t * 8; //Update prcessed bits

           

            //If all codewords of previous size have been used
            if(code == getNumCws() && W < Max){
                //Increase size of codewords
                W++;
            }

            else if(code == getNumCws() && mode != 'n'){
                //Reset Mode
                if(mode == 'r'){
                    //Reset codebook to initial state
                    st = new TST<Integer>();
                    for(int i = 0; i < R; i++){
                        st.put("" + (char)i, i);
                    }
                    code = R + 1;

                    //Reset Width
                    W = Min;
                }

                //Monitor Mode
                else if(mode == 'm' && !isMonitoring){
                    isMonitoring = true;
                    firstRatio = (float)procBits/(float)compBits;
                }
            }

            //Check compression ratio if we need to 
            if(mode == 'm' && isMonitoring){
                currRatio = (float)procBits/(float)compBits;
                
                //If Ratio of Ratios is over threshold
                if((firstRatio / currRatio) > 1.1){
                    //Reset codebook to initial state
                    st = new TST<Integer>();
                    for(int i = 0; i < R; i++){
                        st.put("" + (char)i, i);
                    }
                    code = R + 1;

                    //Reset Width
                    W = Min;

                    isMonitoring = false;
                }
            }

            if (t < input.length() && code < getNumCws()){    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);

        //Reset size of codewords
        W = Min;

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bit encoded using LZW compression with
     * 12-bit codewords from standard input; expands them; and writes
     * the results to standard output.
     */
    public static void expand() {
        
        int i; // next available codeword value

        String[] st = new String[getNumCws()];

        //For monitoring
        int procBits = 0;
        int compBits = 0;
        float firstRatio = 0;
        float currRatio = 0;
        boolean isMonitoring = false;

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++){
            st[i] = "" + (char) i;
        }
        st[i++] = "";                        // (unused) lookahead for EOF

        //Get the compression mode
        int compMode = BinaryStdIn.readInt(2);
        if(compMode < 0 || compMode > 2){
            System.err.println("Error reading compression mode");
            System.exit(1);
        }

        int codeword = BinaryStdIn.readInt(W);
        compBits += W; //update compressed bits

        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {

            BinaryStdOut.write(val);
            procBits += val.length()*8; //update processed bits

            //If end of current level
            if(i == getNumCws() && W < Max){
                //increase codeword size
                W++;

                //Make codebook bigger
                String[] temp = new String[getNumCws()];
                for(int j = 0; j < st.length; j++){
                    temp[j] = st[j];
                }
                st = temp;
            }

            //Check if Codebook full and react accoring to mode
            else if(i == getNumCws() && compMode != 0){
                //Reset mode
                if(compMode == 1){
                    W = Min;

                    //Reset codebook to initial state
                    st = new String[getNumCws()];
                    for(i = 0; i < R; i++){
                        st[i] = "" + (char)i;
                    }
                    st[i++] = "";
                }

                //Monitor Mode
                else if(compMode == 2 && !isMonitoring){
                    isMonitoring = true;
                    firstRatio = (float)procBits / (float)compBits;
                }
            }

            //Check compression ratio if need be
            if(compMode == 2 && isMonitoring){
                currRatio = (float)procBits / (float)compBits;

                //If Ratio of Ratios over threshold
                if((firstRatio/currRatio) > 1.1){
                    //reset codebook
                    W = Min;
                    st = new String[getNumCws()];
                    for(i = 0; i < R; i++){
                        st[i] = "" + (char)i;
                    }
                    st[i++] = "";

                    isMonitoring = false;
                }
            }

            

            codeword = BinaryStdIn.readInt(W);
            compBits += W; //update compressed bits

            if (codeword == R) break;
            String s = st[codeword];

            if (i == codeword){
                s = val + val.charAt(0);   // special case hack
            } 

            if (i < getNumCws()){
                st[i++] = val + s.charAt(0);
            }

            val = s;
        }
        BinaryStdOut.close();
    }

    //Returns max number of codewords at current size
    private static int getNumCws(){
        return (int) Math.pow(2, W);
    }

    /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" an {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if(args[0].equals("-")){
            compress(args[1].charAt(0));
        }  
        else if (args[0].equals("+")){
            expand();
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
