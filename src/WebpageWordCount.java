import java.util.HashMap;
import java.util.Map;

/**
 * Represents the words and word occurances of a webpage along with its URL
 */
public class WebpageWordCount {

	//String of URL to pull HTML file for Text Analyzer List and WordCount Map of word and frequency
	private String URL;
	private Map<String, Integer> WordCount = new HashMap<String, Integer>();

	/**
	 * Default Constructor
	 */
	public WebpageWordCount() {
		super();
	}//end Default Constructor
	
	/**
	 * Getter for URL string
	 * @return URL string
	 */
	public String getURL() {
		return URL;
	}//end getURL

	/**
	 * Setter for URL string
	 * @param uRL this objects URL
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}//end setURL
	
	/**
	 * Getter for Hash map of words and word occurences
	 * @return Map of Words and word occurances
	 */
	public Map<String, Integer> getWordCount() {
		return WordCount;
	}//end getWordCount
	
	/**
	 * Setter for Hash map of words and word occurences
	 * @param wordCount This objects HashMap
	 */
	public void setWordCount(Map<String, Integer> wordCount) {
		WordCount = wordCount;
	}//end setWordCount
	
}//end WebpageWordCount
