package nlp.seastar.lexical;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LexicalFeatures {
	private List<String> stopWords;
	private static final String STOP_WORDS_FILE_NAME = "stopwords.txt";
	private List<String> swearWords;
	private static final String SWEAR_WORDS_FILE_NAME = "swearwords.txt";
	private List<String> spamWords;
	private static final String SPAM_WORDS_FILE_NAME = "spamwords.txt";

	public LexicalFeatures() {
		this.stopWords = new ArrayList<String>();
		this.swearWords = new ArrayList<String>();
		this.spamWords = new ArrayList<String>();
	}

	/**
	 * Takes the stopwords file and generates a list of stop words
	 * 
	 * @param fileName
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

	public boolean isStopWord(String word) {
		if (stopWords != null && !stopWords.isEmpty() && stopWords.contains(word))
			return true;
		return false;
	}

	public void getSwearWords() throws FileNotFoundException {
		File file = new File(SWEAR_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			swearWords.add(line.trim());
		}
		scanner.close();
	}

	public boolean isSwearWord(String word) {
		if (swearWords != null && !swearWords.isEmpty() && swearWords.contains(word))
			return true;
		return false;
	}

	public void getSpamWords() throws FileNotFoundException {
		File file = new File(SPAM_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			spamWords.add(line.trim());
		}
		scanner.close();
	}
	
	public boolean isSpamWord(String line) {
		if (spamWords != null && !spamWords.isEmpty()) {
			for (String spamWord : spamWords) {
				if(line.contains(spamWord))
					return true;
			}
		}
		return false;
	}
}
