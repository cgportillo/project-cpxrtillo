import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

/**
 * A thread-safe search results builder.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class InvertedIndexBuilder {
	
	/**
	 * The InvertedIndex to be built.
	 */
	InvertedIndex ind;
	
	
	/**
	 * Instantiate the InvertedIndex to be built.
	 *
	 * @param ind the InvertedIndex to be built
	 */
	public InvertedIndexBuilder(InvertedIndex ind) {
		this.ind = ind;
	}
	
	/**
	 * Sends paths to be parsed through to be added to the Inverted Index.
	 *
	 * @param argPath the path to be read from
	 * @throws IOException 
	 */
	public void getFile(Path argPath) throws IOException {

		List<Path> paths = TextFileFinder.list(argPath);
		for (Path path : paths) {
			queryFileParse(path, ind);
		}
	}

	/**
	 * Sends stems, positions, and path to Inverted Index map.
	 *
	 * @param path the path to be read from
	 * @param ind     the class that holds the map to create an Inverted Index, add
	 *                to this map
	 * @throws IOException 
	 */
	public void queryFileParse(Path path, InvertedIndex ind) throws IOException {
		
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String curr;
			int position = 0;
			while ((curr = reader.readLine()) != null) {
				String[] parsed = TextParser.parse(curr);
				TreeSet<String> stems = null;
				for (String word : parsed) {
					stems = TextFileStemmer.uniqueStems(word);
					position++;
					for (String stem : stems) {
						ind.addToMap(stem, path.toString(), position);
					}
				}
			}
		}
	}
}
