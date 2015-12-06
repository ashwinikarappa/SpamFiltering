package nlp.seastar.baseline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetException;
import nlp.seastar.lexical.LexicalFeatures;
import nlp.seastar.semantic.Synonymy;
import nlp.seastar.syntactic.SyntacticFeatures;

/**
 * Naive Bayes classification of emails into ham or spam
 * 
 * @author Ashwini
 * @author Sagar
 */
public class Bayes {
	private int numOfSpamEmails;
	private int numOfHamEmails;
	private int numOfSpamWords;
	private int numOfHamWords;
	private int numOfSpamRules;
	private int numOfHamRules;
	private double priorSpamProbablity;
	private double priorHamProbability;
	private String trainDirectoryName;
	private String testDirectoryName;
	private HashMap<String, Word> trainingDictionary;
	private LexicalFeatures lexical;
	private SyntacticFeatures syntactic;
	private HashMap<String, Word> syntacticRuleHashMap;
	private Synonymy synonymy;

	public Bayes() {
		this.trainingDictionary = new HashMap<String, Word>();
		this.syntacticRuleHashMap = new HashMap<String, Word>();
		this.numOfHamEmails = 0;
		this.numOfSpamEmails = 0;
		this.numOfHamWords = 0;
		this.numOfSpamWords = 0;
		this.priorHamProbability = 0.0;
		this.priorSpamProbablity = 0.0;
		this.lexical = new LexicalFeatures();
		this.syntactic = new SyntacticFeatures();
		this.synonymy = new Synonymy();
	}

	public Bayes(String trainDirectoryName, String testDirectoryName) {
		this.trainDirectoryName = trainDirectoryName;
		this.testDirectoryName = testDirectoryName;
		this.trainingDictionary = new HashMap<String, Word>();
		this.syntacticRuleHashMap = new HashMap<String, Word>();
		this.numOfHamEmails = 0;
		this.numOfSpamEmails = 0;
		this.numOfHamWords = 0;
		this.numOfSpamWords = 0;
		this.priorHamProbability = 0.0;
		this.priorSpamProbablity = 0.0;
		this.lexical = new LexicalFeatures();
		this.syntactic = new SyntacticFeatures();
		this.synonymy = new Synonymy();
	}

	/**
	 * @param removeStopWords
	 * @throws FileNotFoundException
	 */
	public void train(boolean removeStopWords) throws FileNotFoundException {
		if (removeStopWords)
			lexical.getStopWords();
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
							if (!removeStopWords || !lexical.isStopWord(words[wordIndex])) {
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
							if (!lexical.isStopWord(words[wordIndex])) {
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
				System.out.print(
						(double) (testSpamCount * 100) / (testSpamCount + testHamCount) + " %");
			else
				System.out.print(
						(double) (testHamCount * 100) / (testSpamCount + testHamCount) + " %");
		}
	}

	public void trainWithSwearWords() throws FileNotFoundException {
		lexical.getSwearWords();
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
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String words[] = (scanner.nextLine().split(" "));
					for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
						if (words[wordIndex].length() > 1) {
							Word word = new Word(isHam, words[wordIndex].trim());
							if (!lexical.isStopWord(words[wordIndex])) {
								word = trainingDictionary.get(word.getWord());
								if (isHam && lexical.isSwearWord(word.getWord()))
									word.incrementHamCount();
								else if (!isHam && (!lexical.isSwearWord(word.getWord())))
									word.incrementSpamCount();
							}
						}
					}
				}
				scanner.close();
			}
		}
	}

	public void testWithSwearWords() throws IOException {
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
								if (lexical.isSwearWord(words[wordIndex])) {
									spamWordCount++;
								} else {
									hamWordCount++;
								}
							}
							if (!lexical.isStopWord(words[wordIndex])) {
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
				System.out.print(
						(double) (testSpamCount * 100) / (testSpamCount + testHamCount) + " %");
			else
				System.out.print(
						(double) (testHamCount * 100) / (testSpamCount + testHamCount) + " %");
		}
	}

	public void trainWithSpamWords() throws FileNotFoundException {
		lexical.getSpamWords();
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
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.length() > 1) {
						Word word = new Word(isHam, line.trim());
						if (!lexical.isStopWord(word.getWord())) {
							word = trainingDictionary.get(word.getWord());
							if (isHam && lexical.isSpamWord(word.getWord())) {
								word.incrementHamCount();
								word.incrementHamCount();
							} else if (!isHam && (!lexical.isSpamWord(word.getWord()))) {
								word.incrementSpamCount();
								word.incrementSpamCount();
							}
						}
					}
				}
				scanner.close();
			}
		}
	}

	public void testWithSpamWords() throws IOException {
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
								if (lexical.isSpamWord(words[wordIndex])) {
									spamWordCount++;
								} else {
									hamWordCount++;
								}
							}
							if (!lexical.isStopWord(words[wordIndex])) {
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
				System.out.print(
						(double) (testSpamCount * 100) / (testSpamCount + testHamCount) + " %");
			else
				System.out.print(
						(double) (testHamCount * 100) / (testSpamCount + testHamCount) + " %");
		}
	}

	public void syntacticTrain() throws FileNotFoundException {
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
				ArrayList<String> tags = (ArrayList<String>) syntactic.lemmatize(file);
				ArrayList<String> ruleList = (ArrayList<String>) syntactic.makeRulelList(tags, 5);
				for (int i = 0; i < ruleList.size(); i++) {
					if (isHam)
						numOfHamRules++;
					else
						numOfSpamRules++;
					String rule = ruleList.get(i);
					if (syntacticRuleHashMap.containsKey(rule)) {
						Word word = syntacticRuleHashMap.get(rule);
						if (isHam)
							word.incrementHamCount();
						else
							word.incrementSpamCount();
					} else {
						Word word = new Word(isHam, rule);
						syntacticRuleHashMap.put(rule, word);
					}
				}
			}
		}
	}

	/**
	 * @throws IOException
	 */
	public void syntacticTest() throws IOException {
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
				ArrayList<String> testFileTags = (ArrayList<String>) syntactic.lemmatize(file);
				ArrayList<String> testRuleList = (ArrayList<String>) syntactic
						.makeRulelList(testFileTags, 5);
				for (int i = 0; i < testRuleList.size(); i++) {
					int spamRuleCount = 0;
					int hamRuleCount = 0;
					double ruleSpamProbability = 0.0;
					double ruleHamProbability = 0.0;
					if (syntacticRuleHashMap.containsKey(testRuleList.get(i))) {
						Word word = syntacticRuleHashMap.get(testRuleList.get(i));
						spamRuleCount = word.getSpamCount();
						hamRuleCount = word.getHamCount();
					}
					ruleSpamProbability = (double) (spamRuleCount + 1)
							/ (numOfSpamRules + syntacticRuleHashMap.size());
					ruleHamProbability = (double) (hamRuleCount + 1)
							/ (numOfHamRules + syntacticRuleHashMap.size());
					hamProbability += Math.log(ruleHamProbability);
					spamProbability += Math.log(ruleSpamProbability);
				}
				hamProbability += Math.log(priorHamProbability);
				spamProbability += Math.log(priorSpamProbablity);
				if (hamProbability < spamProbability)
					testSpamCount++;
				else
					testHamCount++;
			}
			System.out.print("\n\n Classified " + testSpamCount + " as Spam\n Classified  "
					+ testHamCount + " as Ham\n Accuracy = ");
			if (directory.getName().equals("spam"))
				System.out.print(
						(double) (testSpamCount * 100) / (testSpamCount + testHamCount) + " %");
			else
				System.out.print(
						(double) (testHamCount * 100) / (testSpamCount + testHamCount) + " %");
		}
	}

	// public void synonymyTrain() throws FileNotFoundException {
	// File trainDirectory = new File(trainDirectoryName);
	// File[] directories = trainDirectory.listFiles();
	// boolean isHam = false;
	// for (File directory : directories) {
	// if (directory.getName().equals("ham"))
	// isHam = true;
	// else
	// isHam = false;
	// File directoryPath = new File(trainDirectoryName + "\\" + directory.getName());
	// File[] files = directoryPath.listFiles();
	// for (File file : files) {
	// Scanner scanner = new Scanner(file);
	// while (scanner.hasNextLine()) {
	// String line = scanner.nextLine().trim();
	// for (String wordString : line.split(" ")) {
	// if (wordString.length() > 1) {
	// Word word = new Word(isHam, wordString);
	// if (!lexical.isStopWord(word.getWord())) {
	// word = trainingDictionary.get(word.getWord());
	// try {
	// ArrayList<Synset> synsets = synonymy.getSynsets(word.getWord());
	// for (Synset synset : synsets) {
	// String[] wordForms = synset.getWordForms();
	// for (String wordForm : wordForms) {
	// Word newWord = new Word(isHam, wordForm);
	// newWord.setHamCount(word.getHamCount());
	// newWord.setSpamCount(word.getSpamCount());
	// trainingDictionary.put(wordForm, newWord);
	// }
	// }
	// } catch (WordNetException exception) {
	// }
	// }
	// }
	// }
	// }
	// scanner.close();
	// }
	// }
	// }
	public void synonymyTest() throws IOException {
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
							if (!lexical.isStopWord(words[wordIndex])
									&& !trainingDictionary.containsKey(words[wordIndex].trim())) {
								ArrayList<Synset> synsets = synonymy.getSynsets(words[wordIndex]);
								if (synsets != null) {
									for (Synset synset : synsets) {
										String[] wordForms = synset.getWordForms();
										boolean breakLoop = false;
										for (String wordForm : wordForms) {
											if (trainingDictionary.containsKey(wordForm)) {
												Word word = trainingDictionary.get(wordForm);
												spamWordCount = word.getSpamCount();
												hamWordCount = word.getHamCount();
												breakLoop = true;
												break;
											}
										}
										if(breakLoop)
											break;
									}
								}
							}
							if (!lexical.isStopWord(words[wordIndex])) {
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
				System.out.print(
						(double) (testSpamCount * 100) / (testSpamCount + testHamCount) + " %");
			else
				System.out.print(
						(double) (testHamCount * 100) / (testSpamCount + testHamCount) + " %");
		}
	}
}
