package nlp.seastar.driver;

import java.io.FileNotFoundException;
import java.io.IOException;

import nlp.seastar.baseline.Bayes;

public class SpamFilter {
	public static void main(String[] args) {
		String trainingDirectory = args[0];
		String testDirectory = args[1];
		try {
			System.out.print("\n\n#### NAIVE BAYES CLASSIFIER ####\n\n");
			Bayes bayes = new Bayes(trainingDirectory, testDirectory);
			bayes.train(true);
			bayes.test();
			// System.out.print("\n\n#### After handling swear words ####\n\n");
			// bayes.trainWithSwearWords();
			// bayes.testWithSwearWords();
			System.out.print("\n\n#### After handling common spam words ####\n\n");
			bayes.trainWithSpamWords();
			bayes.testWithSpamWords();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
