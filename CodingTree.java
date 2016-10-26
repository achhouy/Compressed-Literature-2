import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * TCSS 342
 * Assignment 4 Compressed Literature 2
 */

/**
 * This is the CodingTree data structure that uses Huffman's Algorithm to compress
 * String files.
 * 
 * @author Arrunn Chhouy
 * @version 1.0
 */
public class CodingTree {
	public static final int RANGE = 8;
	
	/** 
	  * A data member that is a map of characters in the messages to binary codes 
	  * created by your tree 
	  */
	private MyHashTable<String, String> codes;
	
	// A map of the character frequency
	private MyHashTable<String, Integer> wordFrequency;
	
	// Contains all the words in the string
	private List<String> words;

	// A PriorityQueue  
	private PriorityQueue<TreeNode> queue;
	
	// Contains the main tree
	private TreeNode mainTree;
	
	
	// A string of the entire encoding
	private StringBuilder coding;
		
	/**
	 * Constructs a coding tree that initializes all the fields
	 * 
	 * @param message a String
	 * @throws FileNotFoundException
	 */
	public CodingTree(String message) throws FileNotFoundException {
		codes = new MyHashTable<String, String>(32768);
		wordFrequency = new MyHashTable<String, Integer>(32768);
		words = new ArrayList<String>();
		queue = new PriorityQueue<TreeNode>();
		coding = new StringBuilder();
		File outFile = new File("output.txt");
		PrintStream output = new PrintStream(outFile);
		breakString(message);
		frequency();
		buildTree();
		bitMap(mainTree, "");
		checkMapCoding(output);
		encode();
	}
	
	/**
	 * Breaks the string into word tokens
	 * 
	 * @param text a String
	 */
	private void breakString(String text) {
		String word = "";
		for(char c : text.toCharArray()) {
			if(!isWord(c)) {
				if(word.length() > 0) {
					// Add the word to the list
					words.add(word);
				} 
				// Reset the word into a blank String
				word = "" + c;
				if(word.length() > 0) {
					words.add(word);
				}
				word = "";
			} else {
				word += c;
			}
		}
		if(word.length() > 0) {
			words.add(word);
		}	
	}
	
	/**
	 * Checks to see if character is a letter, number, apostrophe, hyphen
	 * @param c a char
	 * @return a boolean
	 */
	private boolean isWord(char c){
		return (Character.isLetter(c))||(Character.isDigit(c))||(c=='\'')||(c=='-');
	}

	
	/**
	 * Counting the frequency of each character in the text file
	 */
	private void frequency() {
//		for(int i = 0; i < words.size(); i++) {
//			if(words.get(i) != null) {
//				if(!wordFrequency.containsKey(words.get(i))) {
//					wordFrequency.put(words.get(i), 1);
//				} else {
//					int count = wordFrequency.get(words.get(i));
//					count++;
//					wordFrequency.put(words.get(i), count);
//				}
//			}
//		}
		for(String word : words) {
			if(!wordFrequency.containsKey(word)) {
				wordFrequency.put(word, 1);
			} else {
				int count = wordFrequency.get(word);
				count++;
				wordFrequency.put(word, count);
			}
		}
	}
	
	/**
	 * Creates a tree with all the character using the Huffman's Algorithm
	 */
	private void buildTree() {
		Set<String> entry = wordFrequency.entrySet();

		for(String word : entry) {
			TreeNode newTree = new TreeNode(word, null, null,
					wordFrequency.get(word)) ;
			queue.offer(newTree);
		}

		TreeNode firstMin;
		TreeNode secondMin;
		while(queue.size() > 1) {
			firstMin = queue.poll();
			secondMin = queue.poll();

			int combineWeight = firstMin.getWeight() + secondMin.getWeight();
			TreeNode root = new TreeNode(null, firstMin, secondMin, combineWeight);
			queue.offer(root);
		}
		mainTree = queue.poll();
	}
	
	/**
	 * Finds the bit coding for each word
	 */
	private void bitMap(TreeNode node, String code) {
		// If it is a leaf then store the code into the map
		if(node.isLeaf()) {
			codes.put(node.getData(), code);
		} else {
			// Traverse through the left side
			bitMap(node.getLeft(), code + 0);
			
			// Traverse through the right side
			bitMap(node.getRight(), code + 1);
		}
	}
	
	/**
	 * Checks the value of the map coding to ensure it is working properly
	 * This is used for testing.
	 */
	public void checkMapCoding(PrintStream output) {
		Set<String> entry = codes.entrySet();
		int count = 0;
		for(String word : entry) {
			output.println("" + count + " Key: " + word + " Value: " + codes.get(word));
			count++;
		}
	}
	
	/**
	 * Encodes the String with the Map coding
	 */
	private void encode() {
		for(int i = 0; i < words.size(); i++) {
			coding.append(codes.get(words.get(i)));
		}
	}
	
	/**
	 * 
	 * 
	 * @param out the printstream
	 * @throws IOException
	 */
	public void outPut(PrintStream out) throws IOException {
		String partial;
		int part;
		int rounds = coding.length() / RANGE;
		for(int i = 0; i < rounds * RANGE;  i += RANGE) {
			partial = coding.substring(i, i + RANGE);
			part = Integer.parseInt(partial, 2);
			Byte b = (byte)part;
			out.write(b);
		}
		partial = coding.substring(rounds * RANGE, coding.length());
	}
	
	public MyHashTable<String, String> getCodeMap() {
		return codes;
	}
	
	public String decode(String bits, MyHashTable<String, String> codes) {
		StringBuilder decodedMessage = new StringBuilder();
		Map<String, String> codesReversed = new HashMap<String, String>();
		
		//reverse map so we can parse the bits with codes as our keys
		for (String word : codes.entrySet()) {
			String code = codes.get(word);
			codesReversed.put(code, word);
		}
		
		StringBuilder subEncoded = new StringBuilder();
		String wordTemp;
		
		for (int i = 0; i < bits.length(); i++) {
			subEncoded.append(bits.charAt(i));
			wordTemp = codesReversed.get(subEncoded.toString());
			if (wordTemp != null) {
				decodedMessage.append(wordTemp);
				subEncoded.setLength(0);	//clears the bits
			}
		}
		return decodedMessage.toString();
	}
	
	/**
	 * A TreeNode class.
	 * 
	 * @author Arrunn Chhouy
	 * @author Matthew Wu
	 * @version 1.0
	 */
	public class TreeNode implements Comparable<TreeNode> {
		// Holds the data in the left of the tree
		private TreeNode myLeft;
		
		// Holds the data in the right of the tree
		private TreeNode myRight;
		
		// The character in the node
		private String myData;
		
		// The weight of the letter.
		private int myWeight;
		
		/**
		 * A constructor of the TreeNode that initializes the fields
		 * 
		 * @param data of the Character
		 * @param left is TreeNode
		 * @param right is TreeNode
		 * @param weight is the frequency
		 */
		public TreeNode(String data, TreeNode left, TreeNode right, int weight) {
			myData = data;
			myLeft = left;
			myRight = right;
			myWeight = weight;
		}
		
		/**
		 * Checks to see if the TreeNode is a leaf
		 * 
		 * @return a boolean
		 */
		public boolean isLeaf() {
			return (myLeft == null && myRight == null);
		}
		
		/**
		 * Returns the character data
		 * 
		 * @return a Character
		 */
		public String getData() {
			return myData;
		}
		
		/**
		 * Gets the frequency of the character
		 * 
		 * @return an int.
		 */
		public int getWeight() {
			return myWeight;
		}
		
		/**
		 * Gets the left node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getLeft() {
			return myLeft;
		}
		
		/**
		 * Gets the right node of this TreeNode
		 * 
		 * @return a TreeNode
		 */
		public TreeNode getRight() {
			return myRight;
		}
		
		/**
		 * Compares with another TreeNode to see which TreeNode 
		 * is larger
		 * 
		 * @return an int
		 */
		@Override
		public int compareTo(TreeNode other) {
			TreeNode node = other;
			int compare = 0;
			if(myWeight > node.getWeight()) {
				compare = 1;
			} else if(myWeight < node.getWeight()) {
				compare = -1;
			}
			return compare;
		}
		
		/**
		 * A string representation of the TreeNode
		 * 
		 * @return a String
		 */
		public String toString() {
			return "Character: " + myData +" Weight: "+ myWeight;
			
		}
	}
}
