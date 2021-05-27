import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * A utility class for parsing and stemming text and text files into collections
 * of stemmed words. Also, creates and searches through the inverted index.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ResultsBuilder {
	
	/** 
	 * The InvertedIndex to be searched through
	 */
	InvertedIndex ind;
	
	/** 
	 * The Results contructor
	 *
	 * @param ind the InvertedIndex to be searched through
	 */
	public ResultsBuilder(InvertedIndex ind) {
		this.ind = ind;
	}
	
	/**
	 * Parses through lines in a file
	 *
	 * @param inputFile the file that is being read
	 * @param ind       the inverted index to be able to search through
	 * @param exact     if there is an exact flag provided in the arguments do an
	 *                  exact search, if not, partial search
	 * @throws IOException if an IO error occurs
	 */
	public void queryParser(Path inputFile, boolean exact) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {

			String curr = null;

			while ((curr = reader.readLine()) != null) {
				Set<String> stemmedLine = TextFileStemmer.uniqueStems(curr);
				curr = String.join(" ", stemmedLine);
				if (!curr.isBlank() || !curr.isEmpty()) {
					ind.search(curr, exact);
				}
			}
		}
	}
}
