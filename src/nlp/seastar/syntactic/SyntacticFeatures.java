package nlp.seastar.syntactic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


// import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SyntacticFeatures {
	private StanfordCoreNLP pipeline;

	public SyntacticFeatures() {
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}
	public List<String> makeRulelList(ArrayList<String> tags, int tagsPerRule){
		List<String> ruleList = new ArrayList<String>();
		for(int i=0;i<=tags.size()-5;i++){
			StringBuilder rule = new StringBuilder();
			for(int j=i;j<i+5;j++){
				rule.append(tags.get(j));
			}
			ruleList.add(rule.toString());
		}
		return ruleList;
	}
	public List<String> lemmatize(File file) {
		List<String> lemmas = new ArrayList<String>();
		try {
			Scanner scanner;
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line =line.replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", "");
				line= line.replaceAll("[\\uD83D\\uFFFD\\uFE0F\\u203C\\u3010\\u3011\\u300A\\u166D\\u200C\\u202A\\u202C\\u2049\\u20E3\\u300B\\u300C\\u3030\\u065F\\u0099\\u0F3A\\u0F3B\\uF610\\uFFFC]", "");
				Annotation document = new Annotation(line);
				this.pipeline.annotate(document);
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						// lemmas.add(token.get(LemmaAnnotation.class));
						lemmas.add(token.tag());
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lemmas;
	}
/*	public static void main(String[] args) {
		SyntacticFeatures t = new SyntacticFeatures();
		List<String> lemmas = t.lemmatize(new File("enron1\\ham\\0001.1999-12-10.farmer.ham.txt"));
		for (String s : lemmas) {
			System.out.println(s + " ");
		}
	}*/
}
