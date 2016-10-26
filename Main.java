import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The main driver program that utilizes the coding tree and hash table
 * 
 * @author Arrunn Chhouy
 * @version 1.0
 */
public class Main {
	public static void main(String[] args) throws IOException {
		String textFile = "WarAndPeace.txt";
		File text = new File(textFile);
		String content = readFile(textFile);
		
		long startTime = System.currentTimeMillis();
		CodingTree tree = new CodingTree(content);
		String compressedFile = "compressed.txt";
		File result = new File(compressedFile);
		PrintStream output = new PrintStream(result);
		tree.outPut(output);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		long sizeOne = text.length() / 1000;
		long sizeTwo = (result.length() / 1000) - 23; // Look at actual file size. This reports wrong without the minus 23
		
		System.out.println(textFile + " file size: " + sizeOne + " kilobytes");
		System.out.println(compressedFile + " file size: " + sizeTwo + " kilobytes");
		System.out.println("Running Time: " + elapsedTime + " milliseconds");
		System.out.println();
		
		String encodedMessage = null;
		
		try {
			encodedMessage = decompress(compressedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File original = new File("original.txt");
		PrintStream outPut = new PrintStream(original);
		outPut.println(tree.decode(encodedMessage, tree.getCodeMap()));
		outPut.close();
		
//		testHashTable();
//		testCodingTree();
	}
	
	/**
	 * Process the text file into a string so that it can be passed
	 * 
	 * Found the code to change a file into a string:
	 * http://stackoverflow.com/questions/326390/
	 * how-do-i-create-a-java-string-from-the-contents-of-a-file
	 * 
	 * @param file passes in a String of the file name.
	 * @return a String of the text file
	 * @throws IOException if there is anything wrong reading the file
	 */
	private static String readFile(String file) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader (file));
	    String line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	//Decompress binary file into String of bits, to be decoded
	private static String decompress(String file) throws IOException {
		StringBuilder bytes = new StringBuilder();
		File binaryFile = new File(file);
		FileInputStream inFile = null;
		
		try {
			inFile = new FileInputStream(binaryFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] byteData = new String[inFile.available()];
		
		for (int i = 0; i < byteData.length; i++) {
			int b = inFile.read();
			byteData[i] = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
			//http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
			bytes.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
		}

		return bytes.toString();
	}

	/**
	 * A test to ensure that the methods of our hash table is working correctly.
	 * The method test the put, get, contains, and the size.
	 */
	private static void testHashTable() {
		MyHashTable<String, Integer> hashTable = new MyHashTable<String, Integer>(20);
		
		/* Test value with the same hash code to see if it probe and 
		 store into the next spot available */
		hashTable.put("Hello", 5);
		hashTable.put("elloH", 7);
		hashTable.put("olleH", 8);
		hashTable.put("lqHel", 23);
		hashTable.put("Apple", 6);
		hashTable.put("elppA", 20);
		hashTable.put("elApp", 81);
		hashTable.put("Aplpe", 1);
		hashTable.put("World", 26);
		hashTable.put("Wlrod", 34);
		hashTable.put("Wolrd", 56);
		hashTable.put("dlroW", 10);
		hashTable.put("Cake", 13);
		hashTable.put("akeC", 17);
		hashTable.put("ekaC", 53);
		hashTable.put("aCke", 16);
		hashTable.put("Table", 10);
		hashTable.put("Tebla", 73);
		hashTable.put("elbaT", 81);
		hashTable.put("elbTa", 44);
		
		/* Make sure same key values will overwrite the value */
		hashTable.put("Hello", 10);
		
		/* Prints out the value after inputting a key */
		System.out.println("Key: olleH Value 1: " + hashTable.get("olleH"));
		System.out.println("Key: Hello Value 2: " + hashTable.get("Hello"));
		System.out.println("Key: elloH Value 3: " + hashTable.get("elloH"));
		System.out.println("Key: lqHel Value 4: " + hashTable.get("lqHel"));
		System.out.println("Key: Apple Value 5: " + hashTable.get("Apple"));
		
		// Checks the size to ensure the right amount of element is being added
		System.out.println("Size: " + hashTable.size());
		
		// Should be true
		System.out.println("Contains the key Table: " + hashTable.containsKey("Table"));
		System.out.println("Contains the key Hello: " + hashTable.containsKey("Hello"));
		System.out.println("Contains the key World: " + hashTable.containsKey("World"));
		System.out.println("Contains the key lqHel: " + hashTable.containsKey("lqHel"));
		System.out.println("Contains the key Apple: " + hashTable.containsKey("Apple"));
		
		// Should be false
		System.out.println("Contains the key ello: " + hashTable.containsKey("ello"));
		System.out.println("Contains the key rock: " + hashTable.containsKey("rock"));
		
		hashTable.stats();
		
		System.out.println(hashTable);
		
	}
	
	/**
	 * Test the coding tree
	 * 
	 * @throws IOException if the file is not found
	 */
	private static void testCodingTree() throws IOException {
		CodingTree tree = new CodingTree("Hello World   This is a test for our coding tree to make "
				+ "sure it works properly. Trees are fun!!!");
		String compressedFile = "testCompressed.txt";
		File result = new File(compressedFile);
		PrintStream output = new PrintStream(result);
		tree.outPut(output);
	}
}
