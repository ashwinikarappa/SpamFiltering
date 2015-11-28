package nlp.seastar.driver;

import java.io.FileNotFoundException;
import java.io.IOException;

import nlp.seastar.baseline.Bayes;

public class SpamFilter {
	public static void main(String[] args) {
		String trainingDirectory = args[0];
		String testDirectory = args[1];
		System.out.print("\n\n#### NAIVE BAYES CLASSIFIER ####\n\n");
		System.out.print("\n **  Accuracy before removing the stop words **\n");
		try {
			Bayes bayes = new Bayes(trainingDirectory, testDirectory);
			bayes.train(true);
			bayes.test(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
