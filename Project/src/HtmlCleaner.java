import java.util.Collections;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Cleans simple, validating HTML 4/5 into plain text.
 */
public class HtmlCleaner {
	

	/**
	 * The words/text after being stripped of it's html
	 */
	public final String text;
	
	/**
	 * The list of urls given by the base
	 */
	public final List<URL> urls;
	
	/**
	 * The html to be stripped for text
	 */
	public final String html;
	
	/**
	 * The url that will be used fetched
	 */
	public final URL base;

	/**
	 * Clean the base URL of it's html to get the text 
	 *
	 * @param base the url that is passed in to be cleaned
	 * @param html information of the url that is passed in to be cleaned
	 * @throws MalformedURLException 
	 */
	public HtmlCleaner(URL base, String html) throws MalformedURLException {
		this.base = base;
		this.html = html;

		if (html == null) {
			this.urls = null;
			this.text = null;
		}
		
		else {
			html = stripBlockElements(html);
			this.urls = Collections.unmodifiableList(LinkParser.listLinks(base, html));
			html = stripTags(html);
			html = stripEntities(html);
			this.text = html;
		}
	}

	/**
	 * Removes all HTML tags and certain block elements from the provided text. The
	 * block elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip tags and elements from
	 * @return text clean of any HTML tags and certain block elements
	 */
	public static String stripHtml(String html) {
		html = stripBlockElements(html);
		html = stripTags(html);
		html = stripEntities(html);
		return html;
	}

	/**
	 * Removes comments and certain block elements from the provided html. The block
	 * elements removed include: head, style, script, noscript, and svg.
	 *
	 * @param html the HTML to strip comments and block elements from
	 * @return text clean of any comments and certain HTML block elements
	 */
	public static String stripBlockElements(String html) {
		html = stripComments(html);
		html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
		html = stripElement(html, "noscript");
		html = stripElement(html, "svg");
		return html;
	}

	// THE FOLLOWING REPLACE WITH THE EMPTY STRING

	/**
	 * Replaces all HTML entities with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html text including HTML entities to remove
	 * @return text without any HTML entities
	 */
	public static String stripEntities(String html) {
		String regex = "(?is)&\\S*?;";
		return html.replaceAll(regex, "");
	}

	/**
	 * Replaces all HTML tags with an empty string. For example, "A<b>B</b>C" will
	 * become "ABC".
	 *
	 * @param html text including HTML tags to remove
	 * @return text without any HTML tags
	 */
	public static String stripTags(String html) {
		String regex = "(?is)(<.+?>)";
		return html.replaceAll(regex, "");
	}

	// THE FOLLOWING REPLACE WITH A SINGLE SPACE

	/**
	 * Replaces all HTML comments with a single space. For example, "A<!-- B -->C"
	 * will become "A C".
	 *
	 * @param html text including HTML comments to remove
	 * @return text without any HTML comments
	 */
	public static String stripComments(String html) {
		String regex = "(?is)<!--([\\s\\S]*?)-->";
		return html.replaceAll(regex, " ");
	}

	/**
	 * Replaces everything between the element tags and the element tags themselves
	 * with a single space. For example, consider the html code: *
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed, and
	 * replaced with a single space.
	 *
	 * @param html text including HTML elements to remove
	 * @param name name of the HTML element (like "style" or "script")
	 * @return text without that HTML element
	 */
	public static String stripElement(String html, String name) {
		String regex = "(?is)(<" + name + ".*?/" + name + "\\s*?>)";
		return html.replaceAll(regex, " ");
	}
}
