package nlp.seastar.baseline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Naive Bayes classification of emails into ham or spam
 * 
 * @author Ashwini
 */
public class Bayes {
	private int numOfSpamEmails;
	private int numOfHamEmails;
	private int numOfSpamWords;
	private int numOfHamWords;
	private double priorSpamProbablity;
	private double priorHamProbability;
	private String trainDirectoryName;
	private String testDirectoryName;
	private HashMap<String, Word> trainingDictionary;
	private List<String> stopWords;
	private static final String STOP_WORDS_FILE_NAME = "stopwords.txt";
	private List<String> swearWords;
	private static final String SWEAR_WORDS_FILE_NAME = "swearwords.txt";
	private static final Path WRONG_HAM = Paths.get("wrongHam");
	private static final Path WRONG_SPAM = Paths.get("wrongSpam");

	public Bayes() {
		this.stopWords = new ArrayList<String>();
		this.swearWords = new ArrayList<String>();
		this.trainingDictionary = new HashMap<String, Word>();
		this.numOfHamEmails = 0;
		this.numOfSpamEmails = 0;
		this.numOfHamWords = 0;
		this.numOfSpamWords = 0;
		this.priorHamProbability = 0.0;
		this.priorSpamProbablity = 0.0;
		try {
			Files.createDirectory(WRONG_HAM);
			Files.createDirectory(WRONG_SPAM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bayes(String trainDirectoryName, String testDirectoryName) {
		this.trainDirectoryName = trainDirectoryName;
		this.testDirectoryName = testDirectoryName;
		this.stopWords = new ArrayList<String>();
		this.swearWords = new ArrayList<String>();
		this.trainingDictionary = new HashMap<String, Word>();
		this.numOfHamEmails = 0;
		this.numOfSpamEmails = 0;
		this.numOfHamWords = 0;
		this.numOfSpamWords = 0;
		this.priorHamProbability = 0.0;
		this.priorSpamProbablity = 0.0;
	}

	/**
	 * Takes the stopwords file and generates a list of stop words
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	private void getStopWords() throws FileNotFoundException {
		File file = new File(STOP_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stopWords.add(line.trim());
		}
		scanner.close();
	}

	private void getSwearWords() throws FileNotFoundException {
		File file = new File(SWEAR_WORDS_FILE_NAME);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			swearWords.add(line.trim());
		}
		scanner.close();
	}

	/**
	 * @param removeStopWords
	 * @throws FileNotFoundException
	 */
	public void train(boolean removeStopWords) throws FileNotFoundException {
		if (removeStopWords)
			getStopWords();
		File trainDirectory = new File(trainDirectoryName);
		File[] directories = trainDirectory.listFiles();
		boolean isHam = false;
		for (File directory : directories) {
			if (directory.getName().equals("ham"))
				isHam = true;
			else
				isHam = false;
			File directoryPath = new File(trainDirectoryName + "\\" + directory.getName());
			File[] files = directoryPath.listFiles();
			for (File file : files) {
				if (isHam)
					numOfHamEmails++;
				else
					numOfSpamEmails++;
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String words[] = (scanner.nextLine().split(" "));
					for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
						if (words[wordIndex].length() > 1) {
							Word word = new Word(isHam, words[wordIndex].trim());
							if (!removeStopWords || stopWords == null || stopWords.isEmpty()
									|| !stopWords.contains(word.getWord())) {
								if (isHam)
									numOfHamWords++;
								else
									numOfSpamWords++;
								if (trainingDictionary.containsKey(word.getWord())) {
									word = trainingDictionary.get(word.getWord());
									if (isHam)
										word.incrementHamCount();
									else
										word.incrementSpamCount();
								} else {
									trainingDictionary.put(word.getWord(), word);
								}
							}
						}
					}
				}
				scanner.close();
			}
		}
		priorHamProbability = (double) numOfHamEmails / (numOfSpamEmails + numOfHamEmails);
		priorSpamProbablity = (double) numOfSpamEmails / (numOfSpamEmails + numOfHamEmails);
	}

	/**
	 * @throws IOException
	 */
	public void test() throws IOException {
		File testDirectory = new File(testDirectoryName);
		File[] directories = testDirectory.listFiles();
		for (File directory : directories) {
			int testHamCount = 0;
			int testSpamCount = 0;
			File directoryPath = new File(testDirectoryName + "\\" + directory.getName());
			File[] files = directoryPath.listFiles();
			System.out.print("\nTest result for directory: " + directory.getName());
			for (File file : files) {
				double hamProbability = 0.0;
				double spamProbability = 0.0;
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String words[] = (scanner.nextLine()).split(" ");
					for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
						int spamWordCount = 0;
						int hamWordCount = 0;
						if (words[wordIndex].length() > 1) {
							if (trainingDictionary.containsKey(words[wordIndex].trim())) {
								Word word = trainingDictionary.get(words[wordIndex].trim());
								spamWordCount = word.getSpamCount();
								hamWordCount = word.getHamCount();
							}
							double wordSpamProbability = 0.0;
							double wordHamProbability = 0.0;
							wordSpamProbability = (double) (spamWordCount + 1)
									/ (numOfSpamWords + trainingDictionary.size());
							wordHamProbability = (double) (hamWordCount + 1)
									/ (numOfHamWords + trainingDictionary.size());
							hamProbability += Math.log(wordHamProbability);
							spamProbability += Math.log(wordSpamProbability);
						}
					}
				}
				hamProbability += Math.log(priorHamProbability);
				spamProbability += Math.log(priorSpamProbablity);
				if (hamProbability < spamProbability) {
					testSpamCount++;
					// if (directory.getName().equals("ham"))
					// Files.copy(file.toPath(), Paths.get(WRONG_SPAM + "\\" + file.getName()));
				} else {
					testHamCount++;
					// if (directory.getName().equals("spam"))
					// Files.copy(file.toPath(), Paths.get(WRONG_HAM + "\\" + file.getName()));
				}
				scanner.close();
			}
			System.out.print("\n\n Classified " + testSpamCount + " as Spam\n Classified  "
					+ testHamCount + " as Ham\n Accuracy = ");
			if (directory.getName().equals("spam"))
				System.out.print((double) (testSpamCount * 100) / (testSpamCount + testHamCount)
						+ " %");
			else
				System.out.print((double) (testHamCount * 100) / (testSpamCount + testHamCount)
						+ " %");
		}
	}
	
	public void testWithSwearWords() throws IOException {
		getSwearWords();
		File testDirectory = new File(testDirectoryName);
		File[] directories = testDirectory.listFiles();
		for (File directory : directories) {
			int testHamCount = 0;
			int testSpamCount = 0;
			File directoryPath = new File(testDirectoryName + "\\" + directory.getName());
			File[] files = directoryPath.listFiles();
			System.out.print("\nTest result for directory: " + directory.getName());
			for (File file : files) {
				double hamProbability = 0.0;
				double spamProbability = 0.0;
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String words[] = (scanner.nextLine()).split(" ");
					for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
						int spamWordCount = 0;
						int hamWordCount = 0;
						if (words[wordIndex].length() > 1) {
							if (trainingDictionary.containsKey(words[wordIndex].trim())) {
								Word word = trainingDictionary.get(words[wordIndex].trim());
								spamWordCount = word.getSpamCount();
								hamWordCount = word.getHamCount();
								if (swearWords == null || swearWords.isEmpty()
										|| !swearWords.contains(words[wordIndex])) {
									spamWordCount++;
								}
							}
							double wordSpamProbability = 0.0;
							double wordHamProbability = 0.0;
							wordSpamProbability = (double) (spamWordCount + 1)
									/ (numOfSpamWords + trainingDictionary.size());
							wordHamProbability = (double) (hamWordCount + 1)
									/ (numOfHamWords + trainingDictionary.size());
							hamProbability += Math.log(wordHamProbability);
							spamProbability += Math.log(wordSpamProbability);
						}
					}
				}

				hamProbability += Math.log(priorHamProbability);
				spamProbability += Math.log(priorSpamProbablity);
				if (hamProbability < spamProbability) {
					testSpamCount++;
					// if (directory.getName().equals("ham"))
					// Files.copy(file.toPath(), Paths.get(WRONG_SPAM + "\\" + file.getName()));
				} else {
					testHamCount++;
					// if (directory.getName().equals("spam"))
					// Files.copy(file.toPath(), Paths.get(WRONG_HAM + "\\" + file.getName()));
				}
				scanner.close();
			}
			System.out.print("\n\n Classified " + testSpamCount + " as Spam\n Classified  "
					+ testHamCount + " as Ham\n Accuracy = ");
			if (directory.getName().equals("spam"))
				System.out.print((double) (testSpamCount * 100) / (testSpamCount + testHamCount)
						+ " %");
			else
				System.out.print((double) (testHamCount * 100) / (testSpamCount + testHamCount)
						+ " %");
		}
	}
}
