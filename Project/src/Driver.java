import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		Instant start = Instant.now();
		ArgumentParser arg = new ArgumentParser(args);
		ThreadSafeInvertedIndex threadedIndex = new ThreadSafeInvertedIndex();
		ThreadSafeResultsBuilder threadedResults = null;
		ThreadSafeInvertedIndexBuilder threadSafeIndexBuilder = null;
		WebCrawler webber = null;

		if (arg.hasFlag("-threads") || arg.hasFlag(("-url"))) {
			int threads = 5;
			try {
				threads = Integer.parseInt(arg.getString("-threads", "5"));
				if (threads > 0) {
					threadSafeIndexBuilder = new ThreadSafeInvertedIndexBuilder(threadedIndex, threads);
					threadedResults = new ThreadSafeResultsBuilder(threadedIndex, threads);
				}
				if (arg.hasFlag("-url")) {
					int limit = 50;
					if (arg.hasFlag("-limit")) {
						limit = Integer.parseInt(arg.getString("-limit", "50"));
					}
					try {
						webber = new WebCrawler(limit, threadedIndex, threads);
						webber.crawl(arg.getString("-url"));
					} catch (MalformedURLException e) {
						Thread.currentThread().interrupt();
					}
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				System.out.println("No path or legal argument givenss.");
				return;
			}
		}
		
		if (arg.hasFlag("-port")) {
			
			int port = Integer.parseInt(arg.getString("-port", "8080"));
				
			Server server = new Server(port);
			
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServletWithMapping(new ServletHolder(new ResultsServlet(threadedResults, threadedIndex)), "/browser");	

			server.setHandler(servletHandler);
			
			try {
				server.start();
				server.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		hasArgs(arg, threadedIndex, threadedResults, threadSafeIndexBuilder);
		System.out.printf("Elapsed: %f seconds%n",
				(double) Duration.between(start, Instant.now()).toMillis() / Duration.ofSeconds(1).toMillis());
	}

	/**
	 * Checks for all viable arguments (path, index, counts, query, results)
	 *
	 * @param arg                    the args passed in to ArgumentParser
	 * @param threadedIndex          the threaded InvertedIndex to be created
	 * @param threadedResultsBuilder the threaded results
	 * @param threadSafeIndexBuilder the thread safe InvertedIndex builder
	 */
	public static void hasArgs(ArgumentParser arg, ThreadSafeInvertedIndex threadedIndex,
			ThreadSafeResultsBuilder threadedResultsBuilder, ThreadSafeInvertedIndexBuilder threadSafeIndexBuilder) {

		InvertedIndex index = new InvertedIndex();
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(index);
		ResultsBuilder resultsBuilder = new ResultsBuilder(index);

		if (arg.hasFlag("-path")) {
			hasPathArg(arg, indexBuilder, threadSafeIndexBuilder);
		}
		if (arg.hasFlag("-index")) {
			hasIndexArg(arg, threadedIndex, index);
		}
		if (arg.hasFlag("-counts")) {
			hasCountsArg(arg, threadedIndex, index);
		}
		if (arg.hasFlag("-query")) {
			hasQueryArg(arg, threadedResultsBuilder, resultsBuilder);

		}
		if (arg.hasFlag("-results")) {
			hasResultsArg(arg, threadedIndex, index);
		}
	}

	/**
	 * Check for the path argument.
	 *
	 * @param arg                    the args passed in to ArgumentParser
	 * @param indexBuilder           the index builder
	 * @param threadSafeIndexBuilder the thread safe InvertedIndex builder
	 */
	public static void hasPathArg(ArgumentParser arg, InvertedIndexBuilder indexBuilder,
			ThreadSafeInvertedIndexBuilder threadSafeIndexBuilder) {
		Path argPath = arg.getPath("-path");
		try {
			if (!arg.hasFlag("-threads")) {
				indexBuilder.getFile(argPath);
			} else {
				threadSafeIndexBuilder.getFile(argPath);
			}
		} catch (NullPointerException | IllegalArgumentException | IOException e) {
			System.out.println("No path or legal argument given.");
			return;
		}
	}

	/**
	 * Check for the counts argument.
	 *
	 * @param arg           the args passed in to ArgumentParser
	 * @param threadedIndex the threaded InvertedIndex to be created
	 * @param index         the InvertedIndex to be created
	 */
	static void hasCountsArg(ArgumentParser arg, ThreadSafeInvertedIndex threadedIndex, InvertedIndex index) {
		Path p = arg.getPath("-counts", Path.of("counts.json"));
		try {
			if (arg.hasFlag("-threads")) {
				SimpleJsonWriter.countArray(threadedIndex.getCountMap(), p);
			} else {
				SimpleJsonWriter.countArray(index.getCountMap(), p);
			}
		} catch (NullPointerException | IllegalArgumentException | IOException e) {
			System.out.println("No path or legal argument given.");
			return;
		}
	}

	/**
	 * Check for the query argument.
	 *
	 * @param arg                    the args passed in to ArgumentParser
	 * @param resultsBuilder         the results builder
	 * @param threadedResultsBuilder the threaded results
	 */
	public static void hasQueryArg(ArgumentParser arg, ThreadSafeResultsBuilder threadedResultsBuilder,
			ResultsBuilder resultsBuilder) {
		Path p = arg.getPath("-query");
		try {
			boolean exact = false;
			if (arg.hasFlag("-exact")) {
				exact = true;
			}
			if (arg.hasFlag("-threads")) {
				threadedResultsBuilder.queryParser(p, exact);
			} else {
				resultsBuilder.queryParser(p, exact);
			}
		} catch (NullPointerException | IllegalArgumentException | IOException e) {
			System.out.println("No path or legal argument given.");
			return;
		}
	}

	/**
	 * Check for the index argument.
	 *
	 * @param arg           the args passed in to ArgumentParser
	 * @param threadedIndex the threaded InvertedIndex to be created
	 * @param index         the InvertedIndex to be built
	 */
	public static void hasIndexArg(ArgumentParser arg, ThreadSafeInvertedIndex threadedIndex, InvertedIndex index) {
		Path p = arg.getPath("-index", Path.of("index.json"));
		try {
			if (arg.hasFlag("-threads")) {
				SimpleJsonWriter.asOuterNestedArray(threadedIndex.getMap(), p);
			} else {
				SimpleJsonWriter.asOuterNestedArray(index.getMap(), p);
			}
		} catch (IOException e) {
			System.out.println("No path or legal argument given.");
		}
	}

	/**
	 * Check for the results argument.
	 *
	 * @param arg           the args passed in to ArgumentParser
	 * @param threadedIndex the threaded InvertedIndex to be created
	 * @param index         the InvertedIndex to be built
	 */
	public static void hasResultsArg(ArgumentParser arg, ThreadSafeInvertedIndex threadedIndex, InvertedIndex index) {
		Path p = arg.getPath("-results", Path.of("results.json"));
		try {
			if (arg.hasFlag("-threads")) {
				SimpleJsonWriter.asCompareResults(threadedIndex.getCompareMap(), p);
			} else {
				SimpleJsonWriter.asCompareResults(index.getCompareMap(), p);
			}
		} catch (NullPointerException | IllegalArgumentException | IOException e) {
			System.out.println("No path or legal argument given.");
			return;
		}
	}
}
