import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe utility class for searching and creating a thread-safe inverted
 * index. Overrides all methods from InvertedInvertedIndex to be able to add 
 * read/write locks.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	/** 
	 * The lock used for reading/writing. 
	 */
	private SimpleReadWriteLock lock;

	/** 
	 * Instantiate maps for the thread-safe inverted index.
	 * Create new lock.
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}
	
	@Override
	public void addToMap(String word, String path, int position) {

		lock.writeLock().lock();
		try {
			super.addToMap(word, path, position);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void addAll(InvertedIndex o) {
		lock.writeLock().lock();
		try {
			super.addAll(o);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public Map<String, Map<String, Set<Integer>>> getMap() {

		lock.readLock().lock();
		try {
			return super.getMap();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Map<String, Integer> getCountMap() {

		lock.readLock().lock();
		try {
			return super.getCountMap();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Map<String, List<CompareSearch>> getCompareMap() {

		lock.readLock().lock();
		try {
			return super.getCompareMap();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {

		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public Set<Integer> getPositions(String word, String location) {

		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean checkLocations(List<CompareSearch> comps, String word, String location) {

		lock.readLock().lock();
		try {
			return super.checkLocations(comps, word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void updateSearch(String word, CompareSearch comp, String location) {

		lock.readLock().lock();
		try {
			super.updateSearch(word, comp, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addToComps(String word, List<CompareSearch> comps) {

		lock.readLock().lock();
		try {
			super.addToComps(word, comps);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void partialSearch(String line, List<CompareSearch> comps) {

		lock.readLock().lock();
		try {
			super.partialSearch(line, comps);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void exactSearch(String line, List<CompareSearch> comps) {

		lock.readLock().lock();
		try {
			super.exactSearch(line, comps);
		} finally {
			lock.readLock().unlock();
		}
	}
}
