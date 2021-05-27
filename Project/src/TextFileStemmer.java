import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 *
 */
public class TextFileStemmer {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * 
	 * @return a sorted set of unique cleaned and stemmed words
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> stemmedWordsHash = new TreeSet<>();
		String[] parsedLine = TextParser.parse(line);

		for (String s : parsedLine) {
			CharSequence newLine = stemmer.stem(s);
			stemmedWordsHash.add(newLine.toString());
		}

		return stemmedWordsHash;
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * 
	 * @return a sorted set of unique cleaned and stemmed words
	 */
	public static TreeSet<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}
}
