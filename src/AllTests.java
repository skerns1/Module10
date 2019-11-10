import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

/**
 * Test all non-GUI involved classes using JUNIT
 */
class AllTests {
	
	TextAnalyzer testClass = new TextAnalyzer();

	/**
	 * Test that sorted word count correctly sorts by value of map indices
	 */
	@Test
	void testSortedWordCount() {
		List<Integer> expected = Arrays.asList(35, 14, 10, 4, 1); 
		Map<String, Integer> testMap = new HashMap<String, Integer>();
		testMap.put("Carl", 4);
		testMap.put("bob", 1);
		testMap.put("FrAn", 10);
		testMap.put("dan", 35);
		testMap.put("Abe", 14);
		testMap = testClass.SortedWordCount(testMap);
		Iterator<Entry<String, Integer>> It = testMap.entrySet().iterator();
		int i = 0;
		while (It.hasNext()) {
			Map.Entry<String, Integer> Pairs = It.next();
			assertEquals(expected.get(i), Pairs.getValue());
			i++;
		}//end while loop
		
	}//end testSortedWordCount
	
	/**
	 * Test that validation for URL works correctly
	 */
	@Test
	void testIsValidURL() {	
		String URLOne = "http://shakespeare.mit.edu/macbeth/full.html";
		String URLTwo = "SADFGHJ2134567@!#!#!#";
		
		boolean test = testClass.isValidURL(URLOne);
		assertTrue(test);
		test = testClass.isValidURL(URLTwo);
		assertFalse(test);
	}//end testIsValidURL

	/**
	 * Test that string array is used to fill map with keys and values
	 */
	@Test
	void testUnsortedWordCount() {
		String[] splitText = {"a","split","text","test"};
		Map<String, Integer> testMap = new HashMap<String, Integer>();
		testMap.put("a", 4);
		testMap.put("split", 1);
		testMap.put("text", 10);
		testMap.put("test", 35);
		testMap.put("Abe", 14);
		testClass.UnsortedWordCount(splitText, testMap);
		
		int test = testMap.get("a");
		assertEquals(test, 5);
		test = testMap.get("test");
		assertEquals(test, 36);	
	}//end testUnsortedWordCount

	/**
	 * Test that method returns a hashmap of words and word occurances from passed URL string
	 */
	@Test
	void testURLToWordCount() {
		String testSite = "http://shakespeare.mit.edu/macbeth/full.html";
		Map<String, Integer> testMap = testClass.URLToWordCount(testSite);
		int valueOne = testMap.get("the");
		int valueTwo = testMap.get("his");
		int valueThree = testMap.get("howl");
		
		assertEquals(valueOne, 730);	
		assertEquals(valueTwo, 145);	
		assertEquals(valueThree, 1);	
		
	}//end testURLToWordCount()

	/*
	 * test that parsed HTML successfully converts to a string
	 */
	@Test
	void testWebpageToString() {
		String testSite = "http://shakespeare.mit.edu/macbeth/full.html";
		String parsedString = testClass.WebpageToString(testSite);
		String[] SplitText = parsedString.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		List<String> splitArray = Arrays.asList(SplitText);
		assertTrue(splitArray.contains("macbeth") && splitArray.contains("witch") && splitArray.contains("macduff"));
	}//end testWebpageToString

	/**
	 * Test that the correct amount of word occurances is incremented in map
	 */
	@Test
	void testWordOccurancesIterator() {
		Map<String, Integer> testMap = new HashMap<String, Integer>();
		testMap.put("Carl", 4);
		testMap.put("bob", 1);
		testMap.put("FrAn", 10);
		testMap.put("dan", 35);
		testMap.put("Abe", 14);
		String stringOfMap = testClass.WordOccurancesIterator(testMap);
		assertEquals(stringOfMap, "1. dan, 35\n" + 
				"2. bob, 1\n" + 
				"3. Abe, 14\n" + 
				"4. Carl, 4\n" + 
				"5. FrAn, 10\n");	
	}//end testWordOccurancesIterator

}//end AllTests
