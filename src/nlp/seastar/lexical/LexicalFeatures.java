package nlp.seastar.lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author sagar
 *
 */
public class LexicalFeatures {
	private List<String> stopWords;
	private static final String STOP_WORDS_FILE_NAME = "stopwords.txt";
	private List<String> swearWords;
	private static final String SWEAR_WORDS_FILE_NAME = "swearwords.txt";
	private List<String> spamPhrases;
	private static final String SPAM_PHRASES_FILE_NAME = "spamwords.txt";

	public LexicalFeatures() {
		this.stopWords = new ArrayList<String>();
		this.swearWords = new ArrayList<String>();
		this.spamPhrases = new ArrayList<String>();
	}

	/**
	 * Takes the stopwords file and generates a list of stop words
	 * 
	 * @throws FileNotFoundException
	 */
	public void getStopWords() throws FileNotFoundException {
		File file = new File(STOP_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stopWords.add(line.trim());
		}
		scanner.close();
	}

	/**
	 * Check if the word is frequently used stop word
	 * 
	 * @param word
	 * @return
	 */
	public boolean isStopWord(String word) {
		if (stopWords != null && !stopWords.isEmpty() && stopWords.contains(word))
			return true;
		return false;
	}

	/**
	 * Takes the swearwords file and generates a list of swear words
	 * 
	 * @throws FileNotFoundException
	 */
	public void getSwearWords() throws FileNotFoundException {
		File file = new File(SWEAR_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			swearWords.add(line.trim());
		}
		scanner.close();
	}

	/**
	 * Check if the word is a swear word
	 * 
	 * @param word
	 * @return
	 */
	public boolean isSwearWord(String word) {
		if (swearWords != null && !swearWords.isEmpty() && swearWords.contains(word))
			return true;
		return false;
	}

	/**
	 * Takes the spamwords file and generates a list of spam phrases
	 * 
	 * @throws FileNotFoundException
	 */
	public void getSpamWords() throws FileNotFoundException {
		File file = new File(SPAM_PHRASES_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			spamPhrases.add(line.trim());
		}
		scanner.close();
	}

	/**
	 * Check if the spam phrase is present in the spam dictionary
	 * 
	 * @param line
	 * @return
	 */
	public boolean isSpamWord(String line) {
		if (spamPhrases != null && !spamPhrases.isEmpty()) {
			for (String spamPhrase : spamPhrases) {
				if (line.contains(spamPhrase))
					return true;
			}
		}
		return false;
	}
}
