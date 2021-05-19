import net.datastructures.Tree;

import javax.xml.stream.events.Characters;
import java.io.*;
import java.util.*;

public class HuffmanEncoding {

    /**
     * Creates map of each character to the frequency of that character in the file
     * @param pathName is name of the file without ".txt"
     * //@throws Exception
     */

    public static Map<Character, Integer> FrequencyMap(String pathName) throws Exception{
        Map<Character, Integer> FrequencyMap= new TreeMap<Character, Integer>();            //create new map to hold characters and frequencies
        BufferedReader input;                                                               // Introduce Buffered Reader input
        try {
            input = new BufferedReader(new FileReader(pathName+".txt"));             // try to open the file
        }
        catch (FileNotFoundException e) {
            System.err.println("Can not open the file.\n" + e.getMessage());
            return FrequencyMap;                                                 // catch error if file cannot be opened
        }
        try{                                                                      // create a string from the inputted file
            int character;
            while((character = input.read()) != -1) {
                char c = (char)character;                                       // Iterate through each character using input.read()
                if (FrequencyMap.containsKey(c)) {
                    FrequencyMap.put(c, FrequencyMap.get(c) + 1);                               // if the character is in the map, increase the frequency value by 1
                }
                else {
                    FrequencyMap.put(c, 1);                                                     // If character is not in the map yet, add it with value 1
                }
            }

        }
        catch(IOException e){
            System.err.println("IOError while reading. \n"+ e.getMessage());                    // catch error if reading was unsuccessful
        }
        try {
            input.close();
        }
        catch(IOException e){
            System.err.println("Can not close file \n"+ e.getMessage());
        }
        return FrequencyMap;                                                                // return the map
    }

    /**
     * implements comparator to compare frequencies of characters
     * builds priority queue of binary trees
     * returns priority queue
     * //
     */
    public static PriorityQueue<BinaryTree<TreeElement>> TreestoPQ(Map<Character, Integer> characters){
        Comparator<BinaryTree<TreeElement>> freqCompare = new TreeComparator();                                 // Use TreeComparator to compare trees based on the frequencies of characters
        PriorityQueue<BinaryTree<TreeElement>> pq = new PriorityQueue<BinaryTree<TreeElement>>(freqCompare);    // create a new priority queue that utilizes the compare method of TreeComparator
        for(Character key : characters.keySet()){
            pq.add(new BinaryTree(new TreeElement(key, characters.get(key))));                                   // Create a new Binary Tree out of each character in the map and add it to the priority queue

        }
        return pq;                                                                                               // return priority queue
    }

    /**
     * Creates binary tree with least frequent characters deepest in the tree
     * only leafs contain characters
     */
    public static BinaryTree<TreeElement> TreeCreation(PriorityQueue<BinaryTree<TreeElement>> pq){

        while (pq.size() > 1){                                                                                              // keep combining subtrees until there is only one Binary tree root
            BinaryTree<TreeElement> t1 = pq.poll();                                                                         // take the two minimum trees from the priority queue
            BinaryTree<TreeElement> t2 = pq.poll();
            TreeElement R = new TreeElement(null, t1.data.getFrequency() + t2.data.getFrequency());      // create a new TreeElement and set its character to null and its frequency to the sum of t1 and t2s frequencies
            BinaryTree<TreeElement> T = new BinaryTree<TreeElement>(R, t1, t2);                                             // new tree with TreeElement R's data and t1 and t2 set as the children
            pq.add(T);                                                                                                      // add tree back into priority queue
        }

        // check if empty file causes empty priority queue
        if (pq.size() <= 0){
            return null;
        }

        else {
            return pq.poll();                                                                                              // return the last item in priority queue i.e. tree with all characters as leaves
        }
    }

    /**
     * Traverses the tree with a helper function to build string code
     * Builds a map that maps each character to its strong code (combination of 0's and 1's)
     * //
     */
    public static Map<Character, String> CodeRetrieval(BinaryTree<TreeElement> HuffmanTree){
        Map<Character, String> CodeMap= new TreeMap<Character, String>();                                   // creates a new map that will map characters to their unique string code
        if (HuffmanTree!= null){                                                                            // check that tree is not null
            Visit(HuffmanTree, "", CodeMap);                                                     // call helper function to traverse the tree
        }

        return CodeMap;                                                                                    // return the map that contains the characters and their unique string codes
    }

    /**
     * Recursive helper function to build string code for each character
     */
    public static void Visit(BinaryTree<TreeElement> Node, String BinaryCode, Map<Character, String> CodeMap){
        if (Node.isLeaf()){
            CodeMap.put(Node.data.getCharacter(), BinaryCode);                                             // If the node is a leaf, add it to the CodeMap along with its specific string combination of 0's and 1's
        }
        else {
            if (Node.hasLeft()) {                                                                          // If the node has a left child, add a zero to the string and recursively call Visit on its left child
                BinaryCode +="0";
                Visit(Node.getLeft(), BinaryCode, CodeMap);
                BinaryCode = BinaryCode.substring(0, BinaryCode.length()-1);                               // remove the 0 that was added to the string when it went into the left child since it's about to go into the right child from the same node it entered left child from

            }

            if (Node.hasRight()) {                                                                         // If the node has a right child, add a 1 to it's unique code and recursively call visit on right child
                BinaryCode +="1";
                Visit(Node.getRight(), BinaryCode, CodeMap);


            }

        }
    }

    /**
     * Compresses a file of words into bits
     * Uses code map to get code string for each character in the file
     * Throws IOException
     */
    public static void Compression(String pathName, Map<Character, String> CodeMap) throws IOException {

        BufferedReader input = new BufferedReader(new FileReader(pathName+".txt"));             // open Buffered reader to enable reading from a file
        BufferedBitWriter bitOutput = new BufferedBitWriter(pathName + "_compressed.txt");     // Open buffered bit writer to enable writing compressed bits to a file
        try {
            int cInt;                                       // Read next character's integer representation
            while ((cInt = input.read()) != -1) {           // while there are characters to read
                char c = (char) cInt;                       // cast value to character
                String bits = CodeMap.get(c);               // get the String code value from map at the character just read
                for (int i = 0; i < bits.length(); i++) {       // for the length of the string code
                    bitOutput.writeBit(bits.charAt(i) == '1');     // Write bit according to true/false boolean of string code

                }
            }
        }
        catch(IOException e) {
            System.err.println("Could not read and convert original file to bit file.\n" + e.getMessage());     // catch exception if file could not be read
        }

        finally{                                                                            // use finally to close the files so they are closed no matter what
            input.close();
            bitOutput.close();                                                              // close both files
            }

    }

    /**
     * Takes a compressed file and decompresses it
     * Searches through the created tree following string code to find each character
     * //@throws IOException
     */
    public static void Decompression(String pathName, BinaryTree<TreeElement> HuffmanTree) throws IOException {
        BufferedBitReader bitInput = new BufferedBitReader(pathName+"_compressed.txt");                         // Open buffered bit reader to read compressed file
        BufferedWriter Output = new BufferedWriter(new FileWriter(pathName+"_decompressed.txt"));               // open Buffered writer to write translated bits
        BinaryTree<TreeElement> HuffmanTree1 = HuffmanTree;                                                              // create copy of tree that will be used for setting back to original tree after going down a path

        if (HuffmanTree1 != null && HuffmanTree1.isLeaf()){                                                              // check if tree is one character or repeating one character
            int i = 0;
            while (i < HuffmanTree1.data.getFrequency()){                                                                // get frequency of that character
                Output.write(HuffmanTree1.data.getCharacter());                                                          // write character number of times of its frequency
                i+=1;
            }
        }

        try{
            while (bitInput.hasNext()) {                                                                                  // while there are still bits to read
                boolean bit = bitInput.readBit();                                                                         // use read bit to cycle through bits
                if (bit) {
                    HuffmanTree = HuffmanTree.getRight();                                                                 // if bit value true, get right child
                }
                if (!bit) {
                    HuffmanTree = HuffmanTree.getLeft();                                                                  // if bit value is false, get left child
                }
                if (HuffmanTree.isLeaf()) {
                    Output.write(HuffmanTree.data.getCharacter());                                                        // If the tree is at a leaf, get the character from that leaf
                    HuffmanTree = HuffmanTree1;                                                                           // reset tree to its original setting
                }

            }
        }
        catch(IOException e){
            System.err.println("Could not read and convert compressed file to decompressed file"+e.getMessage());                   // catch error if there was error reading and decompressing
        }
        finally {
            bitInput.close();                                                               // use finally to close file, need it to close regardless of whether there was an error
            Output.close();
        }

    }
    /**
     * Main method where test cases are called
     * //@throws Exception
     */
    public static void main(String[] args) throws Exception{

        // Test Case 1: Checking function of each method on small text file
        // 1 Checking frequency map construction
        String pathName = "Inputs/hello";
        Map<Character, Integer> freqMap = FrequencyMap(pathName);
        //System.out.println(FrequencyMap(pathName));

        //2 Checking priority queue function
        PriorityQueue<BinaryTree<TreeElement>> pq = TreestoPQ(freqMap);
//        System.out.println(pq.poll());                                                                // used these to check priority queue was extracting min in correct order
//        System.out.println(pq.poll());
//        System.out.println(pq.poll());
//        System.out.println(pq.poll());

        //3 Checking Tree Creation Function
        BinaryTree<TreeElement> HuffmanTree = TreeCreation(pq);
        //System.out.println(HuffmanTree);

        //4 Testing CodeMap Function
        Map<Character, String> CodeMap = CodeRetrieval(HuffmanTree);
        //System.out.println(CodeMap);

        //5 Testing Compression Function
        Compression(pathName, CodeMap);

        //6 Testing Decompression Function
        Decompression(pathName, HuffmanTree);

        // Test Case 2: Checking compression and decompression on an empty file
        String pathName2 = "Inputs/empty";
        BinaryTree<TreeElement> HuffmanTree2 = TreeCreation(TreestoPQ(FrequencyMap(pathName2)));
        Compression(pathName2, CodeRetrieval(HuffmanTree2));
        Decompression(pathName2, HuffmanTree2);

        // Test Case 3: Checking compression and decompression on a file containing only one character
        String pathName3 = "Inputs/singlecharacter";
        BinaryTree<TreeElement> HuffmanTree3 = TreeCreation(TreestoPQ(FrequencyMap(pathName3)));
        Compression(pathName3, CodeRetrieval(HuffmanTree3));
        Decompression(pathName3, HuffmanTree3);

        // Test Case 4: Checking compression and decompression on a file containing one repeating character
        String pathName4 = "Inputs/repeatingcharacter";
        BinaryTree<TreeElement> HuffmanTree4 = TreeCreation(TreestoPQ(FrequencyMap(pathName4)));
        Compression(pathName4, CodeRetrieval(HuffmanTree4));
        Decompression(pathName4, HuffmanTree4);

        // Test Case 5: Checking compression and decompression on US Constitution
        String pathName5 = "Inputs/USConstitution";
        BinaryTree<TreeElement> HuffmanTree5 = TreeCreation(TreestoPQ(FrequencyMap(pathName5)));
        Compression(pathName5, CodeRetrieval(HuffmanTree5));
        Decompression(pathName5, HuffmanTree5);

        // Test Case 6: Checking compression and decompression on WarAndPeace text
        String pathName6 = "Inputs/WarAndPeace";
        BinaryTree<TreeElement> HuffmanTree6 = TreeCreation(TreestoPQ(FrequencyMap(pathName6)));
        Compression(pathName6, CodeRetrieval(HuffmanTree6));
        Decompression(pathName6, HuffmanTree6);

        // The size of the compressed WarAndPeace File is 1,811,745 bytes (1.8 MB on disk)






    }












}
