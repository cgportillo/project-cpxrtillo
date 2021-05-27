import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class TextFileFinder {

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive). Useful for
	 * {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 */
	public static final Predicate<Path> isText = (path) -> !Files.isDirectory(path) && Files.isRegularFile(path)
			&& (path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text"));

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive). Useful for
	 * {@link Files#find(Path, int, BiPredicate, FileVisitOption...)}.
	 *
	 */
	public static final BiPredicate<Path, BasicFileAttributes> isTextWithAttribute = (path, attr) -> isText.test(path);

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 *
	 * @throws IOException if an I/O error occurs
	 * 
	 */
	public static Stream<Path> find(Path start) throws IOException {
		Stream<Path> strPath = Files.walk(start).filter(isText);
		return strPath;
	}

	/**
	 * Returns a list of text files.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an I/O error occurs
	 *
	 */
	public static List<Path> list(Path start) throws IOException {
		return find(start).collect(Collectors.toList());
	}
}
