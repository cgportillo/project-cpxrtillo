import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates, adds, sorts to inverted index map.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class InvertedIndex {

	/**
	 * Stores mappings of parsed words found, path, and it's indices found in that
	 * path.
	 */
	private Map<String, Map<String, Set<Integer>>> map;

	/**
	 * Stores mappings of the file names and total number of words.
	 */
	private Map<String, Integer> wCountMap;

	/**
	 * Stores mappings of the words and their CompareSearch results.
	 */
	private Map<String, List<CompareSearch>> compareMap;

	/**
	 * Initializes the argument maps.
	 */
	public InvertedIndex() {

		this.map = new HashMap<>();
		this.wCountMap = new HashMap<>();
		this.compareMap = new HashMap<>();
	}

	/**
	 * Adds the parsed words, paths, and positions found in those paths into a map.
	 *
	 * @param word     the parsed word that was initially found in a text file
	 * @param path     the path in which the word was located
	 * @param position the index line number in which the word was found
	 */
	public void addToMap(String word, String path, int position) {

		if (!this.map.containsKey(word)) {
			this.map.put(word, new HashMap<String, Set<Integer>>());
		}

		/* If the word is only thing in the map, add new path. */
		if (!this.map.get(word).containsKey(path)) {
			this.map.get(word).put(path, new HashSet<Integer>());
		}

		/* Add word, path, and position to map. */
		this.map.get(word).get(path).add(position);

		/* Add path and position to count of words per file map. */
		this.wCountMap.put(path, position);
	}

	/**
	 * Adds all available keys and key sets to a map.
	 *
	 * @param o other InvertedIndex to be added to.
	 */
	public void addAll(InvertedIndex o) {

		for (String key : o.map.keySet()) {
			if (!this.map.containsKey(key)) {
				// get everything it's mapping too, not just one thing
				this.map.put(key, o.map.get(key));
			} else {
				// else, go through the whole map and check
				for (String location : o.map.get(key).keySet()) {
					if (!this.map.get(key).containsKey(location)) {
						this.map.get(key).put(location, o.map.get(key).get(location));
					} else {
						this.map.get(key).get(location).addAll(o.map.get(key).get(location));
					}
				}
			}
		}

		for (String key : o.wCountMap.keySet()) {
			if (!this.wCountMap.containsKey(key)) {
				this.wCountMap.put(key, o.wCountMap.get(key));
			} else {
				if (this.wCountMap.get(key) < o.wCountMap.get(key)) {
					this.wCountMap.put(key, o.wCountMap.get(key));
				}
			}
		}
	}

	/**
	 * Returns the argument map.
	 * 
	 * @return mapping of words and their locations in files
	 */
	public Map<String, Map<String, Set<Integer>>> getMap() {

		Map<String, Map<String, Set<Integer>>> sortedMap = new TreeMap<>(this.map);
		for (String s : sortedMap.keySet()) {
			Map<String, Set<Integer>> innerMap = new TreeMap<String, Set<Integer>>(sortedMap.get(s));
			for (String s1 : innerMap.keySet()) {
				Set<Integer> positions = new TreeSet<Integer>(innerMap.get(s1));
				innerMap.put(s1, positions);
			}
			sortedMap.put(s, innerMap);
		}
		return new TreeMap<>(sortedMap);
	}

	/**
	 * Retrieving map with path and total number of words.
	 * 
	 * @return mapping of paths and number of words in each path
	 */
	public Map<String, Integer> getCountMap() {

		Map<String, Integer> sortedCountMap = new TreeMap<>(this.wCountMap);
		return new TreeMap<>(sortedCountMap);
	}

	/**
	 * Retrieving map with the query and all instances of that word with regards to
	 * its location, score, and count.
	 * 
	 * @return mapping query and instances of where the word is found
	 */
	public Map<String, List<CompareSearch>> getCompareMap() {

		Map<String, List<CompareSearch>> sortedCompareMap = new TreeMap<>(this.compareMap);
		return new TreeMap<>(sortedCompareMap);
	}

	/**
	 * Initializes new ArrayList of CompareSearches. Checks if from the Driver,
	 * there is an exact flag If there is one, do an exact search, else, partial.
	 * Sorts the CompareSearch list.
	 * 
	 * @param line  word that is being queried
	 * @param exact check whether exact flag provided
	 * @return 
	 */
	public List<CompareSearch> search(String line, boolean exact) {

		List<CompareSearch> comparisons = new ArrayList<>();
		if (exact) {
			exactSearch(line, comparisons);
		} else {
			partialSearch(line, comparisons);
		}
		Collections.sort(comparisons);
		return comparisons;
	}

	/**
	 * Perform an exact search with a given query.
	 * 
	 * @param line  word that is being queried
	 * @param comps list of CompareSearch (location, count, and score) of a query
	 */
	public void exactSearch(String line, List<CompareSearch> comps) {

		if (!line.isBlank() || !line.isEmpty() && !this.compareMap.containsKey(line)) {
			for (String word : Arrays.asList(line.split(" "))) {
				if (this.map.containsKey(word)) {
					addToComps(word, comps);
				}
			}
		}
		this.compareMap.put(line, comps);
	}

	/**
	 * Perform a partial search with a given query.
	 * 
	 * @param line  word that is being queried
	 * @param comps list of CompareSearch (location, count, and score) of a query
	 */
	public void partialSearch(String line, List<CompareSearch> comps) {

		if (!line.isBlank() || !line.isEmpty() && !this.compareMap.containsKey(line)) {
			for (String query : Arrays.asList(line.split(" "))) {
				for (String word : this.map.keySet()) {
					if (word.startsWith(query)) {
						addToComps(word, comps);
					}
				}
			}
		}
		this.compareMap.put(line, comps);
	}

	/**
	 * Adds to comparison map in which
	 * 
	 * @param word  word that is being queried
	 * @param comps list of CompareSearch (location, count, and score) of a query
	 */
	public void addToComps(String word, List<CompareSearch> comps) {

		Collection<String> locations = getLocations(word);
		for (String loc : locations) {
			if (!checkLocations(comps, word, loc)) {
				int count = getPositions(word, loc).size();
				double totalwords = this.wCountMap.get(loc);
				comps.add(new CompareSearch(loc, count / totalwords, count));
			}
		}
	}

	/**
	 * Retrieves the locations in which the words are found.
	 * 
	 * @param word word that is being queried
	 * 
	 * @return a set of strings that are the locations of the InvertedIndex map.
	 */
	public Set<String> getLocations(String word) {

		if (this.map.containsKey(word)) {
			return Collections.unmodifiableSet(this.map.get(word).keySet());
		}

		return Collections.unmodifiableSet(new TreeSet<>());
	}

	/**
	 * Retrieves the positions in which the words are found.
	 * 
	 * @param word     word that is being queried
	 * @param location the location in which the word can be found
	 * 
	 * @return a set of integers that are the positions of the word in the
	 *         InvertedIndex map.
	 */
	public Set<Integer> getPositions(String word, String location) {

		if (this.map.containsKey(word)) {
			if (this.map.get(word).containsKey(location)) {
				return Collections.unmodifiableSet(this.map.get(word).get(location));
			}
		}
		return Collections.unmodifiableSet(new TreeSet<>());
	}

	/**
	 * Checks if the location is already in the list of CompareSearches.
	 * 
	 * @param comps    list of comparisons for the word
	 * @param word     word that is being queried
	 * @param location the location in which the word can be found
	 * 
	 * @return true if the location is in the comps list, false if it is not
	 */
	public boolean checkLocations(List<CompareSearch> comps, String word, String location) {

		for (CompareSearch comp : comps) {
			if (comp.getLocation().equals(location)) {
				updateSearch(word, comp, location);
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the CompareSearch object.
	 * 
	 * @param comp     CompareSearch object that is being updated
	 * @param word     word that is being queried
	 * @param location the location in which the word can be found
	 */
	public void updateSearch(String word, CompareSearch comp, String location) {

		int count = getPositions(word, location).size();
		double totalwords = wCountMap.get(location);
		CompareSearch.updateSearch(comp, count, totalwords);
	}
}
