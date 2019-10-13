package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		TrieNode root = new TrieNode(null,null,null);
		
		
		for (int i = 0; i<allWords.length; i++) {
			String word = allWords[i];
			TrieNode wordNode = new TrieNode(new Indexes(i,(short)0,(short)(word.length()-1)), null, null);
			
			if (root.firstChild == null) {
				root.firstChild = wordNode;
				
				//print(root, allWords);
				//System.out.println("---------------");
				
				continue;
			}
			
			traverseAndInsert(wordNode, root.firstChild, allWords);
			
			//print(root, allWords);
			//System.out.println("---------------");
		}
		
		return root;
	}
	
	private static void traverseAndInsert(TrieNode wordNode, TrieNode headOfLevel, String[] allWords) {
		TrieNode crnt = headOfLevel;
		TrieNode prev = null;
		
		while (crnt!=null) {
			
			Indexes matchedWordNode = match(wordNode.substr, crnt.substr, allWords); //match in terms of wordNode
			Indexes unmatchedWordNode = getUnmatched(matchedWordNode, wordNode.substr);
			Indexes matchedCrnt = match(crnt.substr, wordNode.substr, allWords); //match in terms of crnt
			Indexes unmatchedCrnt = getUnmatched(matchedCrnt, crnt.substr);
			
			if (matchedWordNode.startIndex <= matchedWordNode.endIndex) { //if wordNode and crnt match
				wordNode.substr = unmatchedWordNode; //cut off the matched part of wordNode
				
				if (unmatchedCrnt.startIndex<=unmatchedCrnt.endIndex) { //if part and not the whole crnt matches
					crnt.substr = matchedCrnt;
					
					TrieNode newFirstChild = new TrieNode (unmatchedCrnt, null, wordNode);
					newFirstChild.firstChild = crnt.firstChild;
					crnt.firstChild= newFirstChild;
					
					return;
				}
				else { //the whole crnt does match
					traverseAndInsert(wordNode, crnt.firstChild, allWords);
					return;
				}
			}
			
			if (prev == null) {
				prev = crnt;
			}
			else {
				prev = prev.sibling;
			}
			crnt = crnt.sibling;
		}
		
		//went through loop without matching, so insert at end
		//there shouldn't be a NullPointer because prev shouldn't be null here
		prev.sibling = wordNode;
		
	}
	
	//returns matched in terms of substr1
	//startIndex will be greater than endIndex if there is no match
	private static Indexes match(Indexes substr1, Indexes substr2, String[] allWords) {
		
		Indexes matched = new Indexes(substr1.wordIndex, substr1.startIndex, (short)(substr1.startIndex-1));
		
		String str1 = allWords[substr1.wordIndex];
		String str2 = allWords[substr2.wordIndex];
		int str1Place = substr1.startIndex;
		int str2Place = substr2.startIndex;
		
		boolean matches = true;
		
		while (matches && !(str1Place == substr1.endIndex) && !(str2Place == substr2.endIndex)) {
			if ( !(str1.charAt(str1Place) == str2.charAt(str2Place)) ) {
				matches = false;
				break;
			}
			
			str1Place++;
			str2Place++;
		}
		
		//bring it back to letter that matched, if current letters mismatch
		//can result in startIndex-1 meaning there was no match
		if ( !(str1.charAt(str1Place) == str2.charAt(str2Place)) ){
			str1Place--;
			str2Place--;
		}
		
		matched.endIndex = (short)str1Place;
		
		return matched;
	}
	
	//startIndex will be greater than endIndex if there is no unmatched (i.e. part = full)
	private static Indexes getUnmatched(Indexes part, Indexes full) {
		
		Indexes unmatched = new Indexes(full.wordIndex, (short)(part.endIndex+1), (short)full.endIndex);
		
		return unmatched;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		
		//find furthest node that matches prefix
		TrieNode prefixNode = traverseAndFind(root.firstChild, prefix, allWords);
		
		if (prefixNode == null) {
			return null;
		}
		
		//traverse to all of leaf nodes stemming from that node
		return listsOfChildren(prefixNode);

	}
	
	private static TrieNode traverseAndFind(TrieNode headOfLevel, String prefix, String[] allWords) {
		
		TrieNode crnt = headOfLevel;
		
		while (crnt != null) {
			
			String match = match(prefix, crnt, allWords);
			
			if (match.length() > 0) { //if there is a match
				
				if (match.length() < prefix.length()) {
					prefix = prefix.substring(match.length());
					return traverseAndFind(crnt.firstChild, prefix, allWords);
				}
				return crnt;
				
			}
			
			crnt = crnt.sibling;
			
		}
		
		
		return null;
	}
	
	//returns the part of prefix that matches with node
	private static String match(String prefix, TrieNode node, String[] allWords) {
		
		String match = "";
		String strAtNode = allWords[node.substr.wordIndex].substring(node.substr.startIndex, node.substr.endIndex+1);
		boolean matches = true;
		
		for (int i = 0; i<prefix.length() && i< strAtNode.length(); i++) {
			if (prefix.charAt(i) == strAtNode.charAt(i)) {
				match+=prefix.substring(i, i+1);
				matches = true;
			}
			else {
				matches = false;
			}
			
			if (!matches) {
				break;
			}
			
		}
		
		
		return match;
		
	}
	
	private static ArrayList<TrieNode> listsOfChildren(TrieNode root) {
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		TrieNode crnt = root.firstChild;
		
		if (crnt == null) {
			list.add(root);
			return list;
		}
		
		
		while (crnt != null) {
			
			ArrayList<TrieNode> tempList = listsOfChildren(crnt);
				
			for (TrieNode node: tempList) {
				list.add(node);
			}
				
			
			crnt = crnt.sibling;
		}
		
		return list;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex].substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
