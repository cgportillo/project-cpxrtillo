/**
 * Stores a single search result and implements the Comparable interface.
 * Includes the sort criteria:the location, total word count of the location,
 * and number of times the query occurs at that location.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class CompareSearch implements Comparable<CompareSearch> {

	/**
	 * Initialize path of CompareSearch object
	 */
	private String location;

	/**
	 * Initialize score of CompareSearch object
	 */
	private double score;

	/**
	 * Initialize count of CompareSearch object
	 */
	private int count;

	/**
	 * Creates CompareSearch objects which contain locations, score and count for
	 * each word.
	 * 
	 * @param location path of where word is found
	 * 
	 * @param score    average of how many times this word is used in the file
	 * @param count    how many times this word is found in the file
	 */
	public CompareSearch(String location, double score, int count) {
		this.location = location;
		this.score = score;
		this.count = count;
	}

	/**
	 * Compares CompareSearch objects to provide correct ordering.
	 * 
	 * @param o other CompareSearch object to compare against
	 * 
	 * @return 1 if object should be placed after the one being compared to -1 if
	 *         object should be placed before the one being compared to
	 */
	@Override
	public int compareTo(CompareSearch o) {

		if (this.score - o.score == 0) {
			if (this.count - o.count == 0) {
				return this.location.compareTo(o.location);
			} else if (this.count - o.count < 0) {
				return 1;
			} else {
				return -1;
			}
		} else if (this.score - o.score < 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * @return count of CompareSearch object.
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @return count of CompareSearch object.
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @return count of CompareSearch object.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Updates the search comparison object's count and score.
	 * 
	 * @param comp  the CompareSearch object to be updated
	 * @param count the total count of the word in the file
	 * @param wordCount the total count of the word in all files
	 */
	public static void updateSearch(CompareSearch comp, int count, double wordCount) {
		comp.count += count;
		comp.score = comp.count / wordCount;
	}
}
