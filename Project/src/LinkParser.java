import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 */
public class LinkParser {

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 * @throws MalformedURLException 
	 */
	public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {

		ArrayList<URL> urls = new ArrayList<URL>();
		
		String regex = "(?msi)<a\\s*[^>]*?\\s*href\\s*=\\s*\"(.*?)\"\\s*.*?\\s*>";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(html);

		int index = 0;

		// Keep going while found a match in text
		while (index < html.length() && m.find(index)) {
			
			// Store matching substring
			urls.add(clean(new URL(base, html.substring(m.start(1), m.end(1)))));
			
			if (m.start() == m.end()) {
				// Advance index if matched empty string
				index = m.end() + 1;
			} else {
				// Otherwise start looking at end of last match
				index = m.end();
			}
		}

		return urls;
	}
}
