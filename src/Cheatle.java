import java.util.TreeSet;

import javax.xml.stream.events.Characters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*; 

public class Cheatle {
private TreeSet<String> solutions; 
private HashSet<String> dictionary; 
private TreeSet<String> remainingSolutions; 
private TreeSet<Character> knownLetters; 
private TreeSet<Character> unknownLetters;
private TreeSet<Character> absentLetters;
private TreeSet<Character> possibleLetters; 
private int numGuesses;
private TreeSet<Character> alphabet;

	//Reads the dictionaryFile and puts all the allowed guesses into a data structure,
	//and also reads the solutionFile and puts all the possible solutions into a data structure,
	//also adding all the possible solutions to the allowed guesses.
	//Throws a BadDictionaryException if not every word in the dictionary & solutions are of the same length 
	public Cheatle(String dictionaryFile, String solutionFile) throws FileNotFoundException, BadDictionaryException {
		dictionary = new HashSet<String>();
		solutions = new TreeSet<String>(); 
		Scanner scan1 = new Scanner (new File(dictionaryFile));
		Scanner scan2 = new Scanner (new File(solutionFile)); 
		String d = scan1.next();
		dictionary.add(d); 
		while(scan1.hasNext()) {
			String word = scan1.next(); 
			if (word.length() != d.length())
			{
				throw new BadDictionaryException(); 
			}
			dictionary.add(word);
		}
		while(scan2.hasNext())
		{
			String word = scan2.next();
			if (word.length() != d.length())
			{
				throw new BadDictionaryException(); 
			}
			solutions.add(word); 
			dictionary.add(word); 
		}
		alphabet = new TreeSet<Character>();
		for (String word: solutions)
		{
			for (int k = 0; k < word.length(); k++)
			{
				char letter = word.charAt(k);
				alphabet.add(letter);
			}
		}
	}


	//Returns the length of the words in the list of words
	public int getWordLength() {
		return solutions.first().length(); 

	}

	private boolean contains(String s, char letter)
	{
		if (s.indexOf(letter) == -1)
		{
			return false;
		}
		return true; 
	}
	
	//Returns the complete alphabet of chars that are used in any word in the solution list,
	//IN ORDER as a String
	public String getAlphabet() {
		String list = "";
		for (char letter: alphabet)
		{
			list += letter; 
		}
		return list; 

	}


	//Begins a game of Cheatle, initializing any private instance variables necessary for
	// a single game.
	public void beginGame() {
		remainingSolutions = new TreeSet<String>(); 
		for (String word: solutions)
		{
			remainingSolutions.add(word); 
		}
		knownLetters = new TreeSet<Character>();
		unknownLetters = new TreeSet<Character>();
		absentLetters = new TreeSet<Character>();
		possibleLetters = new TreeSet<Character>(); 
		for (char letter: alphabet)
		{
			possibleLetters.add(letter); 
		}
		numGuesses = 0; 

	}

	//Checks to see if the guess is in the dictionary of words.
	//Does NOT check to see whether the guess is one of the REMAINING words.
	public boolean isAllowable(String guess) {
		if (dictionary.contains(guess))
		{
			return true;
		}
		return false; 

	}

	//Given a guess, returns a String of '*', '?', and '!'
	//that gives feedback about the corresponding letters in that guess:
	// * means that letter is not in the word
	// ? means that letter is in the word, but not in that place
	// ! means that letter is in that exact place in the word
	// Because this is CHEATLE, not Wordle, you are to return the feedback
	// that leaves the LARGEST possible number of words remaining!!!
	//makeGuess should also UPDATE the list of remaining words
	// and update the list of letters where we KNOW where they are,
	// the list of letters that are definitely in the word but we don't know where they are,
	// the list of letters that are not in the word,
	// and the list of letters that are still possibilities
	public String makeGuess(String guess) {
		String maximumFeedback = findMaximum(guess); 
		updateVariables(guess, maximumFeedback); 
		numGuesses++; 
		return maximumFeedback; 
	}
	
	private String findMaximum (String guess)
	{
		if (remainingSolutions.size()== 1 && guess.equals(remainingSolutions.first()))
		{
			String feedback = "";
			for (int k = 0; k < guess.length(); k++)
			{
				feedback += "!";
			}
			return feedback; 
		}
		HashMap<String, ArrayList<String>> solutionsFeedback = new HashMap<String, ArrayList<String>>();
		int maximum = 0; 
		String feedbackToReturn = ""; 
		for (String word: remainingSolutions)
		{
			String feedback = generateFeedback(guess, word);
			if (!solutionsFeedback.containsKey(feedback))
			{
				ArrayList<String> correspondingWords = new ArrayList<String>();
				correspondingWords.add(word); 
				int count = 1; 
				if (count > maximum)
				{
					maximum = count; 
					feedbackToReturn = feedback; 
				}
				solutionsFeedback.put(feedback, correspondingWords); 
			}
			else
			{
				ArrayList<String> correspondingWords = solutionsFeedback.get(feedback);
				correspondingWords.add(word);
				int count = correspondingWords.size(); 
				if (count > maximum)
				{
					maximum = count; 
					feedbackToReturn = feedback; 
				}
				solutionsFeedback.replace(feedback, correspondingWords); 
			}	
		}
		return feedbackToReturn; 
	}
	
	public void updateVariables(String guess, String feedback)
	{
		// update remainingSolutions
		TreeSet<String> remaining = new TreeSet<String>(); 
		for (String word: remainingSolutions)
		{
			String generated = generateFeedback(guess, word); 
			if (generated.equals(feedback))
			{
				remaining.add(word);
			}
		}
		remainingSolutions = remaining; 
		
		// update knownLetters
		TreeSet<Character> known = new TreeSet<Character>();
		for (char letter: knownLetters)
		{
			known.add(letter); 
		}
		for (int k = 0; k < feedback.length(); k++)
		{
			if (feedback.charAt(k) == '!')
			{
				known.add(guess.charAt(k)); 
				if (unknownLetters.contains(guess.charAt(k)))
				{
					unknownLetters.remove(guess.charAt(k)); 
				}
			}
		}
		knownLetters = known; 
		
		// update unknownLetters
		TreeSet<Character> unknown = new TreeSet<Character>();
		for (char letter: unknownLetters)
		{
			unknown.add(letter); 
		}
		for (int k = 0; k < feedback.length(); k++)
		{
			if (feedback.charAt(k) == '?')
			{
				unknown.add(guess.charAt(k)); 
			}
			if (knownLetters.contains(guess.charAt(k)))
			{
				unknown.remove(guess.charAt(k)); 
			}
		}
		unknownLetters = unknown; 
		
		// update absentLetters
		TreeSet<Character> absent = new TreeSet<Character>();
		for (char letter: absentLetters)
		{
			absent.add(letter); 
		}
		for (int k = 0; k < feedback.length(); k++)
		{
			if (feedback.charAt(k) == '*')
			{
				absent.add(guess.charAt(k)); 
			}
		}
		absentLetters = absent; 
		
		// update possibleLetters 
		for (char kLetter: knownLetters)
		{
			possibleLetters.remove(kLetter);
		}
		for (char uLetter: unknownLetters)
		{
			possibleLetters.remove(uLetter);
		}
		for (char aLetter: absentLetters)
		{
			possibleLetters.remove(aLetter);
		}
	}
	
	public static String generateFeedback(String guess, String solution)
	{
		Character[] feedback = new Character[guess.length()]; 
		Character[] solutionCopy = new Character[solution.length()]; 
		for (int i = 0; i < solution.length(); i++)
		{
			solutionCopy[i] = solution.charAt(i); 
		}
		for (int i = 0; i < guess.length(); i++)
		{
			feedback[i] = guess.charAt(i); 
		}
		for (int k = 0; k < feedback.length; k++)
		{
			for (int i = 0; i < solutionCopy.length; i++)
			{
				if (feedback[k] == solutionCopy[i])
				{
					if (k == i)
					{
						feedback[k] = '!'; 
						solutionCopy[i] = null; 
					}
				}
			}
		}
		for (int k = 0; k < feedback.length; k++)
		{
			for (int i = 0; i < solutionCopy.length; i++)
			{
				if (feedback[k] == solutionCopy[i])
				{
					feedback[k] = '?'; 
					solutionCopy[i] = null; 
					
				}
			}
		}
		for (int k = 0; k < feedback.length; k++)
		{
			if (feedback[k] != '!')
			{
				if (feedback[k] != '?')
				{
					feedback[k] = '*'; 
					solutionCopy[k] = null; 
				}
			}
		}
		String feedbackString = "";
		for (int k = 0; k < feedback.length; k++)
		{
			feedbackString += feedback[k]; 
		}
		return feedbackString; 
		}

	//Returns a String of all letters that have received a ! feedback
	// IN ORDER
	public String correctPlaceLetters() {
		String known = "";
		for (char letter: knownLetters)
		{
			known += letter; 
		}
		return known; 

	}

	//Returns a String of all letters that have received a ? feedback
	// IN ORDER
	public String wrongPlaceLetters() {
		String unknown = "";
		for (char letter: unknownLetters)
		{
			unknown += letter; 
		}
		return unknown; 

	}

	//Returns a String of all letters that have received a * feedback
	// IN ORDER
	public String eliminatedLetters() {
		String absent = "";
		for (char letter: absentLetters)
		{
			absent += letter; 
		}
		return absent; 
	}

	//Returns a String of all unguessed letters
	// IN ORDER 
	public String unguessedLetters() {
		String possible = "";
		for (char letter: possibleLetters)
		{
			possible += letter; 
		}
		return possible; 

	}


	//Returns true if the feedback string is the winning one,
	//i.e. if it is all !s
	public boolean isWinningFeedback(String feedback) {
		for (int k = 0; k < feedback.length(); k++)
		{
			if (feedback.charAt(k) != '!')
			{
				return false; 
			}
		}
		return true; 

	}

	//Returns a String of all the remaining possible words, with one word per line,
	// IN ORDER
	public String getWordsRemaining() {
		String remaining = ""; 
		for (String word: remainingSolutions)
		{
			remaining += word + "\n";
		}
		return remaining; 

	}
	
	//Returns the number of possible words remaining
	public int getNumRemaining() {
		return remainingSolutions.size();
	}

	//Returns the number of guesses made in this game
	public int numberOfGuessesMade() {
		return numGuesses; 

	}

	//Ends the current game and starts a new game.
	public void restart() 
	{
		this.beginGame(); 
	}

}
