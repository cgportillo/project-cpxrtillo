import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A thread-safe inverted index builder.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {

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
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex ind, int threads) {
		super(ind);
		this.threads = threads;
		this.ind = ind;
		this.queue = new WorkQueue(threads);
	}

	/**
	 * Sends stems, positions, and path to Inverted Index map.
	 *
	 * @param argPath the path to be read from
	 * 
	 * @throws IOException if an IO error occurs
	 */
	public void getFile(Path argPath) throws IOException {

		List<Path> paths = TextFileFinder.list(argPath);
		for (Path path : paths) {
			TaskAdd p = new TaskAdd(path);
			queue.execute(p);
		}
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		queue.shutdown();
	}
		

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected, will
	 * exit instead of grabbing new work from the queue. These threads will continue
	 * running in the background until a shutdown is requested. Adds to the
	 * thread-safe inverted index.
	 */
	private class TaskAdd implements Runnable {

		/** The path to add or list. */
		private Path path;

		/**
		 * Initializes this task.
		 *
		 * @param path the path to add or list
		 */
		public TaskAdd(Path path) {
			this.path = path;
		}

		/**
		 * Give the threads their work to do through the WorkQueue. Create the
		 * ThreadSafeInvertedIndex map.
		 */
		@Override
		public void run() {
			try {
				InvertedIndex idx = new InvertedIndex();
				queryFileParse(path, idx);
				ind.addAll(idx);
			} catch (IOException e) {
				System.out.println("IOException encountered.");
			}
		}
	}
}
