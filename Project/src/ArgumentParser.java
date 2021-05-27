import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 * 
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ArgumentParser {

	/**
	 * Stores command-line arguments in key = value pairs.
	 */
	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentParser() {
		this.map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten..
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentParser(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (i + 1 < args.length) {
				if (isFlag(args[i])) {
					if (isFlag(args[i + 1])) {
						this.map.put(args[i], null);
					} else if (isValue(args[i + 1])) {
						this.map.put(args[i], args[i + 1]);
					}
				}
				// at the end of the arguments, the last argument is a flag
			} else if (isFlag(args[i])) {
				this.map.put(args[i], null);
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 */
	public static boolean isFlag(String arg) {
		// check to make sure arg length greater than 1 and arg itself is not null
		if (arg != null && arg.length() > 1) {

			// check to make sure arg starts with "-"
			if (arg.startsWith("-")) {
				int nextCharIndex = 1;
				boolean isNextCharDigit = Character.isDigit(arg.charAt(nextCharIndex));

				// if the next char is not a digit, return true
				if (isNextCharDigit == false) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 */
	public static boolean isValue(String arg) {
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {

		return this.map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {

		return this.map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		String value = this.map.get(flag);
		return value == null ? false : true;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {

		return this.map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {
		String value = this.getString(flag);
		return value == null ? defaultValue : value;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 */
	public Path getPath(String flag) {

		if (this.map.get(flag) != null) {
			Path path = Path.of(this.map.get(flag));
			if (path != null) {
				return path;
			}
		}
		return null;
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {
		Path value = this.getPath(flag);
		return value == null ? defaultValue : value;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
