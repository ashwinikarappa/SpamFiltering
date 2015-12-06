package nlp.seastar.semantic;

import java.util.ArrayList;
import java.util.Arrays;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordNetException;

public class Synonymy {
	private static final String WORDNET_DATABASE_DIR_VALUE = "dict";
	private static final String WORDNET_DATABASE_DIR_KEY = "wordnet.database.dir";
	private static final WordNetDatabase WORD_NET_DATABASE = WordNetDatabase.getFileInstance();

	public Synonymy() {
		System.setProperty(WORDNET_DATABASE_DIR_KEY, WORDNET_DATABASE_DIR_VALUE);
	}

	public static WordNetDatabase getWordNetDatabase() {
		return WORD_NET_DATABASE;
	}

	public ArrayList<Synset> getSynsets(String word) {
		try {
			return new ArrayList<Synset>(Arrays.asList(WORD_NET_DATABASE.getSynsets(word)));
		} catch (WordNetException exception) {
//			System.out.println("Synset Failed");
		}
		return null;
	}
}
