import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class SimpleJsonWriter {
	
	/**
	 * Formatter to format the scores of the CompareSearch objects. 
	 */
	static DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
	
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param collection the elements to write
	 * @param writer     the writer to use
	 * @param level      the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> collection, Writer writer, int level) throws IOException {

		/* write first bracket */
		writer.write("[\n");

		if (collection.size() > 0) {

			/* iterate over collection items if there are any */
			var start = collection.iterator();
			indent(writer, level + 1);
			writer.write(start.next().toString());

			while (start.hasNext()) {
				writer.write(",\n");
				indent(writer, level + 1);
				writer.write(start.next().toString());
			}
			writer.write("\n");
		}

		/* indent, write end bracket */
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {

		/* write first curly brace */
		writer.write("{\n");

		/* initialize iterator over elements map */
		var start = elements.entrySet().iterator();

		while (start.hasNext()) {
			var entry = start.next();
			indent(writer, level + 1);
			quote(entry.getKey(), writer);
			writer.write(": ");
			asArray(entry.getValue(), writer, level + 1);

			if (start.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		/* indent, write end brace */
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asOuterNestedArray(Map<String, Map<String, Set<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asOuterNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with a double nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param map    the elements to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asOuterNestedArray(Map<String, Map<String, Set<Integer>>> map, Writer writer, int level)
			throws IOException {

		/* write first curly brace */
		writer.write("{\n");

		/* initialize iterator over elements map */
		var start = map.entrySet().iterator();

		while (start.hasNext()) {
			var entry = start.next();
			indent(writer, level + 1);
			quote(entry.getKey(), writer);
			writer.write(": ");
			asNestedArray(entry.getValue(), writer, level + 1);

			if (start.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		/* indent, write end brace */
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param map  the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asOuterNestedArray(Map<String, Map<String, Set<Integer>>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asOuterNestedArray(map, writer, 0);
		}
	}
	
	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param map  the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void countArray(Map<String, Integer> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			countArray(map, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param map    the elements to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void countArray(Map<String, Integer> map, BufferedWriter writer, int level) throws IOException {

		writer.write("{\n");

		var start = map.entrySet().iterator();

		while (start.hasNext()) {
			var entry = start.next();
			indent(writer, level + 1);
			quote(entry.getKey(), writer);
			writer.write(": " + entry.getValue());

			if (start.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		/* indent, write end brace */
		indent(writer, level);
		writer.write("}");

	}
	
	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param compareMap  the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asCompareResults(Map<String, List<CompareSearch>> compareMap, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asCompareResults(compareMap, writer, 0);
		}
	}

	/**
	 * Writes the elements of the map which contains the words and their
	 * CompareSearch objects as a pretty JSON object with a nested array.
	 *
	 * @param compareMap    the elements to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asCompareResults(Map<String, List<CompareSearch>> compareMap, BufferedWriter writer, int level)
			throws IOException {

		writer.write("{\n");

		var start = compareMap.entrySet().iterator();

		while (start.hasNext()) {
			var entry = start.next();

			indent(writer, level + 1);
			quote(entry.getKey(), writer);
			writer.write(": ");
			asCompareResults(entry.getValue(), writer, level);

			if (start.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}

		/* indent, write end brace */
		indent(writer, level);
		writer.write("}");

	}
	
	/**
	 * Writes the elements of a CompareSearch element 
	 * as a pretty JSON object with a nested array. 
	 *
	 * @param value    the elements to write
	 * @param writer the writer to use
	 * @param level  the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	private static void asCompareResults(List<CompareSearch> value, BufferedWriter writer, int level)
			throws IOException {

		writer.write("[\n");
		
		var start = value.iterator();

		while (start.hasNext()) {
			var entry = start.next();
			indent(writer, level + 2);
			writer.write("{\n");
			indent(writer, level + 3);
			quote("where", writer);
			writer.write(": ");
			quote(entry.getLocation(), writer);
			writer.write(",\n");
			indent(writer, level + 3);
			quote("count", writer);
			writer.write(": ");
			writer.write(entry.getCount() + "");
			writer.write(",\n");
			indent(writer, level + 3);
			quote("score", writer);
			writer.write(": ");
			writer.write(FORMATTER.format(entry.getScore()));
			writer.write("\n");
			indent(writer, level + 2);
			writer.write("}");
			
			if (start.hasNext()) {
				writer.write(",");
			}
			writer.write("\n");
		}
		
		/* indent, write end brace */
		indent(writer, level + 1);
		writer.write("]");

	}

	/**
	 * Indents using 2 spaces by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write(' ');
			writer.write(' ');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException if an IO error occurs
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}
}
