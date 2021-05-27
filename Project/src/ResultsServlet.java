import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Servlet for Search Engine.
 *
 * @author Carlos Portillo
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class ResultsServlet extends HttpServlet {
	
	/** 
	 * Logger to log info.
	 */
	public static final Logger log = Log.getRootLogger();

	/** 
	 * Identifier used for serialization (unused). 
	 */
	private static final long serialVersionUID = 1L;

	/** 
	 * The title to use for this webpage. 
	 */
	private static final String TITLE = "Search Results";
	
	/** 
	 * Search results builder.
	 */
	private final ThreadSafeResultsBuilder results;
	
	/** 
	 * Thread safe inverted index.
	 */
	private final ThreadSafeInvertedIndex index;
	
	/** 
	 * Constructor for servlet.
	 * 
	 * @param results Search results builder.
	 * @param index thread safe inverted index.
	 */
	public ResultsServlet(ThreadSafeResultsBuilder results, ThreadSafeInvertedIndex index) {
		super();
		this.index = index;
		this.results = results;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		
		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf(
				"	<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	<section class=\"hero is-primary is-bold\">");
		out.printf("	  <div class=\"hero-body\">");
		out.printf("	    <div class=\"container\">");
		out.printf("	      <h1 class=\"title\">");
		out.printf("	        Herogine");
		out.printf("	      </h1>");
		out.printf("						<i class=\"fas fa-search\"></i>");
		out.printf("						&nbsp;");
		out.printf("						the World.%n");
		out.printf("	    </div>");
		out.printf("	  </div>");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container has-text-centered\">%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf(
				"						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter your query here.\">%n",
				"search");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-question-circle\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-sign-in-alt\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Search%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			<div class=\"control\">");
		out.printf("				<label class=\"radio\">");
		out.printf("		    		<input type=\"radio\" name=\"%s\" value=\"p\" checked=\"checked\">\n", "type");
		out.printf("						Partial");
		out.printf("		  		</label>\n");
		out.printf("			<label class=\"radio\">");
		out.printf("		    <input type=\"radio\" name=\"%s\" value=\"e\">\n", "type");
		out.printf("				Exact");
		out.printf("		  </label>\n");
		out.printf("	 	 </div>%n");
		out.printf("		</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	    </div>");
		out.printf("	  </div>");
		out.printf("	</section>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"hero\">");
		out.printf("	  <div class=\"hero-body\">");
		out.printf("	    <div class=\"container\">");
		out.printf("	      <h2 class=\"subtitle\">");
		out.printf("	        Index Browser");
		out.printf("	      </h2>");
		
		for (String key : index.getMap().keySet()) {
			out.printf("<p> %s%n </p>", key);
			out.printf("<details>");
			for (String word : index.getLocations(key)) {
				out.printf("<%s>%n", word);
				out.printf("<p> <a href=\"%s\">%s</a></p>", word, word);
				for (Integer ints : index.getPositions(key, word)) {
					out.printf("Counts: %s%n", ints);
				}
			}
			out.printf("</details>%n");
		}
		out.printf("	    </div>");
		out.printf("	  </div>");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"hero\">");
		out.printf("	  <div class=\"hero-body\">");
		out.printf("	    <div class=\"container\">");
		out.printf("	      <h2 class=\"subtitle\">");
		out.printf("	        Locations Browser");
		out.printf("	      </h2>");
		for (String key : index.getCountMap().keySet()) {
			out.printf("<p> <a href=\"%s\">%s</a>, Counts = %s%n </p>", key, key, index.getCountMap().get(key));
		}
		out.printf("	    </div>");
		out.printf("	  </div>");
		out.printf("	</section>%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	      <h5 class=\"subtitle\">%n");
		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");
		out.printf("					&nbsp;Updated %s%n", getDate());
		out.printf("	      </h5>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String search = request.getParameter("search");
		String type = request.getParameter("type");
		type = StringEscapeUtils.escapeHtml4(type);
		boolean exact = false;

		if (type.equals("e")) {
			exact = true;
		}

		// Avoid XSS attacks using Apache Commons Text
		search = StringEscapeUtils.escapeHtml4(search);

		log.info("New escaped search query = " + search);
		log.info("type = " + type);
		log.info("exact = " + exact);

		Instant now = Instant.now();

		results.parseSearch(search, exact);

		Duration elapsed = Duration.between(now, Instant.now());
		double secs = (double) elapsed.toNanos() / Duration.ofSeconds(1).toNanos();

		PrintWriter out = response.getWriter();
		DecimalFormat d = new DecimalFormat("#.###");

		TreeSet<String> seq = TextFileStemmer.uniqueStems(search);
		search = String.join(" ", seq);

		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf(
				"	<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	<section class=\"hero is-primary is-bold\">");
		out.printf("	  <div class=\"hero-body\">");
		out.printf("	    <div class=\"container\">");
		out.printf("	      <h1 class=\"title\">");
		out.printf("	        Herogine");
		out.printf("	      </h1>");
		out.printf("						<i class=\"fas fa-search\"></i>");
		out.printf("						&nbsp;");
		out.printf("						the World.%n");
		out.printf("	    </div>");
		out.printf("	  </div>");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("<section class=\"section\">%n");
		out.printf("<div class=\"container\">%n");
		out.printf("<h2 class=\"title\">Showing results for: %s</h2>%n", search);
		out.printf("%n");

		if (results.getSearches(search).isEmpty()) {
			out.printf("<p> No results for %s</p>%n", search);
		} else {
			out.printf("<p> Here are %d results for %s in %f seconds </p>%n", results.getSearches(search).size(),
					search, secs);
			for (CompareSearch comp : results.getSearches(search)) {
				out.printf("<div class=\"box has-text-center\">%n");
				out.printf("<a href=\"%s\">%s</a>: %s%n", comp.getLocation(), comp.getLocation(), comp.getCount());
				out.printf("<p class=\"has-text-red is-text-5 has-text-left\">Score = %s</p>%n",
						d.format(comp.getScore()));
				out.printf("</div>%n");
			}
		}
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
