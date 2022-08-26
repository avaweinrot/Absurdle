import java.io.FileNotFoundException;
import java.util.Scanner;

public class CheatleUI {

	public static void main(String[] args) throws FileNotFoundException, BadDictionaryException {

		Cheatle game = new Cheatle("WordleDictionary.txt", "WordleSolutionList.txt");
		game.beginGame();
		Scanner kb = new Scanner(System.in);
		System.out.println("Welcome to CHEATLE!");
		System.out.println("Type: /restart to restart the game");
		System.out.println("      /quit to quit the game");
		System.out.println("      /remaining to print a complete list of words remaining");
		System.out.println("      /numremaining to print the number of words remaining");
		System.out.println("      /eliminated to see which letters have been eliminated");
		System.out.println("Cheatle will respond to your guess with a sequence of !, ?, and *");
		System.out.println("* means the corresponding letter is not in the word");
		System.out.println("? means the corresponding letter is in the word, but not in the right place");
		System.out.println("! means the corresponding letter is in the correct place in the word");
		System.out.println("You are trying to guess a " + game.getWordLength() + "-letter word.");
		System.out.println("Good luck!");

		while(true) {
			String input = kb.next();
			switch(input) {
			case "/quit":
				kb.close();
				return;
			case "/restart":
				game.restart();
				break;
			case "/remaining":
				System.out.println(game.getWordsRemaining());
				break;
			case "/numremaining":
				System.out.println(game.getNumRemaining());
				break;				
			case "/eliminated":
				System.out.println(game.eliminatedLetters());
				break;
			default:
				if (!game.isAllowable(input)) {
					System.out.println("Word is not in dictionary.  Please guess again.");
				} else {
					String feedback = game.makeGuess(input);
					System.out.println(feedback);
					if (game.isWinningFeedback(feedback)) {
						System.out.println("You won in " + game.numberOfGuessesMade() + " guesses!");
						System.out.println("Would you like to play again? (Y/N)");
						String response = kb.next();
						if (response.toLowerCase().substring(0,1).equals("y")) {
							game.restart();
							break;
						} else {
							kb.close();
							return;
						}
					}
					System.out.print("Green: " + game.correctPlaceLetters() + "   ");
					System.out.println("Yellow: " + game.wrongPlaceLetters());
					System.out.println("Unguessed: " + game.unguessedLetters());
				}
				break;
			}

		}
	}
	

}
