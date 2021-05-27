import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A thread-safe search results builder.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ThreadSafeResultsBuilder extends ResultsBuilder {

	/**
	 * ThreadSafeIndex to be created.
	 */
	private final ThreadSafeInvertedIndex ind;

	/**
	 * Amount of threads to be used.
	 */
	final int threads;

	/**
	 * Queue that will allow tasks to work through.
	 */
	private WorkQueue queue;

	/**
	 * Sets the threads and thread-safe inverted index to the passed in values.
	 * 
	 * @param ind     the thread-safe inverted index to be created
	 * @param threads amount of threads to be used.
	 */
	public ThreadSafeResultsBuilder(ThreadSafeInvertedIndex ind, int threads) {
		super(ind);
		this.threads = threads;
		this.ind = ind;
		this.queue = new WorkQueue(threads);
	}

	/**
	 * Parses through lines in a file and executes tasks.
	 *
	 * @param inputFile the file that is being read
	 * @param exact     if there is an exact flag provided in the arguments do an
	 *                  exact search, if not, partial search
	 * @throws IOException if an IO error occurs
	 */
	public void queryParser(Path inputFile, boolean exact) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {

			String curr = null;

			while ((curr = reader.readLine()) != null) {
				TaskSearch p = new TaskSearch(curr, exact);
				queue.execute(p);
			}
			try {
				queue.finish();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			queue.shutdown();
		}
	}

	/**
	 * Executes line search tasks.
	 *
	 * @param line  line to be executed
	 * @param exact if there is an exact flag provided in the arguments do an exact
	 *              search, if not, partial search
	 */
	public void parseSearch(String line, boolean exact) {
		queue.execute(new TaskSearch(line, exact));
	}

	/**
	 * Get search results.
	 *
	 * @param line line to be parsed, stemmed, and create results for.

	 * @return search results for the query
	 */
	public List<CompareSearch> getSearches(String line) {
		TreeSet<String> stemmed = TextFileStemmer.uniqueStems(line);
		if (stemmed.isEmpty()) {
			return Collections.unmodifiableList(Collections.emptyList());
		}
		line = String.join(" ", stemmed);
		Map<String, List<CompareSearch>> searchMap = ind.getCompareMap();
		if (searchMap.containsKey(line)) {
			return Collections.unmodifiableList(searchMap.get(line));
		}
		return Collections.unmodifiableList(Collections.emptyList());
	}

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected, will
	 * exit instead of grabbing new work from the queue. These threads will continue
	 * running in the background until a shutdown is requested. Searches through the
	 * thread-safe inverted index.
	 */
	private class TaskSearch implements Runnable {

		/**
		 * the line to be read and passed in.
		 */
		private String line;

		/**
		 * Check to see if exact search.
		 */
		boolean exact;

		/**
		 * Initializes this task.
		 *
		 * @param line  the line to be read and passed in.
		 * @param exact check to see if exact search.
		 */
		public TaskSearch(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		/**
		 * Give the threads their work to do through the WorkQueue. Search through the
		 * ThreadSafeInvertedIndex map.
		 */
		@Override
		public void run() {
			synchronized (ind) {
				Set<String> stemmedLine = TextFileStemmer.uniqueStems(line);
				line = String.join(" ", stemmedLine);
				if (!line.isBlank() || !line.isEmpty()) {
					ind.search(line, exact);
				}
			}
		}
	}
}
