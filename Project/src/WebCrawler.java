import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A thread-safe utility class for crawling the inverted index.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class WebCrawler {

	/**
	 * The thread-safe inverted index to be crawled.
	 */
	private final ThreadSafeInvertedIndex ind;

	/**
	 * The work queue to be used to crawl the inverted index.
	 */
	private WorkQueue queue;

	/**
	 * The limit of urls to be used.
	 */
	private int limit;

	/**
	 * The set of links.
	 */
	private final Set<URL> linkset;

	/**
	 * Constructs the Web Crawler.
	 * 
	 * @param ind     the thread-safe inverted index to be created
	 * @param threads amount of threads to be used.
	 * @param limit   limit of urls to be used
	 */
	public WebCrawler(int limit, ThreadSafeInvertedIndex ind, int threads) {
		this.ind = ind;
		this.limit = limit;
		this.queue = new WorkQueue(threads);
		this.linkset = new HashSet<>();
	}

	/**
	 * Crawl the urls and execute them.
	 * 
	 * @param url url to be crawled.
	 * @throws MalformedURLException
	 */
	public void crawl(String url) throws MalformedURLException {

		linkset.add(new URL(url));
		queue.execute(new AddWebTask(new URL(url)));
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		queue.shutdown();
	}

	/**
	 * Method to stem url links and html and add them to the inverted index.
	 * 
	 * @param url   url to be crawled.
	 * @param index inverted index to be created
	 * @param html  the html to be stripped and parsed to get the text.
	 */
	public void stemmingLinks(String url, String html, InvertedIndex index) {
		int position = 0;
		SnowballStemmer stemmer = new SnowballStemmer(TextFileStemmer.DEFAULT);
		String[] stems = TextParser.parse(html);
		for (String curr : stems) {
			position++;
			index.addToMap(stemmer.stem(curr).toString(), url, position);
		}
	}

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected, will
	 * exit instead of grabbing new work from the queue. These threads will continue
	 * running in the background until a shutdown is requested. Crawls through the 
	 * inverted index.
	 */
	private class AddWebTask implements Runnable {
		
		/**
		 * The url to be crawled.
		 */
		private final URL url;

		/**
		 * Initializes this task.
		 * 
		 * @param url url to be crawled.
		 */
		public AddWebTask(URL url) {
			this.url = url;
		}

		/**
		 * Give the threads their work to do through the WorkQueue. Crawl through the
		 * inverted index.
		 */
		@Override
		public void run() {
			try {
				String html = HtmlFetcher.fetch(url, 3);
				if (html == null) {
					return;
				}

				HtmlCleaner cleaner = new HtmlCleaner(this.url, html);
				InvertedIndex index = new InvertedIndex();
				stemmingLinks(this.url.toString(), cleaner.text, index);
				ind.addAll(index);

				synchronized (linkset) {
					for (URL url : cleaner.urls) {
						if (!linkset.contains(url) && linkset.size() < limit) {
							linkset.add(url);
							queue.execute(new AddWebTask(url));
						}
					}
				}
			} catch (MalformedURLException e) {
				System.out.println("Malformed.");
			}
		}
	}
}
