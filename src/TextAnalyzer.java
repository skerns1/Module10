//Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.*;
import java.util.LinkedHashMap;
//JavaFX Imports
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//Imports from External Libraries
import org.jsoup.Jsoup;

/**
 * Runs GUI to process word count of webpages
 * Save and load word occurances results and display them
 * @author Steven
 *
 */
public class TextAnalyzer extends Application {
	
	/**
	 * launch the GUI
	 * @param args Main method arguement
	 */
	public static void main(String[] args) {
		launch(args);
	}//END MAIN
	
	/**
	 * Put all the entries in unsorted map into LinkedMap of Object in descending value order
	 * @param Unsorted Hash Map of Unsorted words and word occurances
	 * @return Map sorted by word occurances
	 */
	public Map<String, Integer> SortedWordCount (Map<String, Integer> Unsorted) {
		//Create a Sorted Map
		Map<String, Integer> Sorted = Unsorted
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(
						toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
								LinkedHashMap::new));
		return Sorted; 
	}//end SortedWordCount
	
	/**
	 * Start Application(non-Javadoc)
	 * @param primaryStage Main window of GUI application
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 * @throws Exception When GUI fails to launch
	 */
	public void start(Stage primaryStage) throws Exception {
		//Stage setup
		primaryStage.setTitle("Word Occurances GUI");
		
		//Create objects for UI
		Button submitB = new Button();
		Button saveB = new Button();
		Button loadB = new Button();
		TextField submitT = new TextField();
		TextField saveT = new TextField();
		TextArea outputTA = new TextArea();
		Label tipsL = new Label();
		ListView<String> fileList = new ListView<String>();
		HBox top = new HBox();
		VBox left = new VBox();
		VBox right = new VBox();
		VBox center = new VBox();
		BorderPane borderPane = new BorderPane();
		//Create object for Word Occurances
		WebpageWordCount WWC = new WebpageWordCount();
		
		//Text and Options for objects
		submitB.setText("Submit");
		saveB.setText("Save");
		loadB.setText("Load");
		submitT.setPromptText("URL");
		saveT.setPromptText("File Name");
		outputTA.setEditable(false);
		tipsL.setText("Submit: Submit URL to analyze word occurances"
				+ "\n" + "Save: Save results to text file" +
				"\n" + "Load: Load text file");
		tipsL.setWrapText(true);
		
		//Object sizes
		submitB.setPrefWidth(75);
		saveB.setPrefWidth(75);
		loadB.setPrefWidth(75);
		submitT.setPrefWidth(250);
		saveT.setPrefWidth(200);
		outputTA.setPrefSize(250,500);
		tipsL.setMinSize(150, 250);
		fileList.setPrefSize(275,500);
		
		//Object alignments
		top.getChildren().addAll(submitT, submitB, saveT, saveB);
		top.setAlignment(Pos.BASELINE_CENTER);
		center.getChildren().addAll(loadB, tipsL);
		center.setAlignment(Pos.CENTER);
		left.getChildren().add(outputTA);
		right.getChildren().add(fileList);
		borderPane.setTop(top);
		borderPane.setCenter(center);
		borderPane.setLeft(left);
		borderPane.setRight(right);
		
		//Object Padding and spacing
		borderPane.setPadding(new Insets(10, 10, 10, 10));
		left.setPadding(new Insets(10));
		right.setPadding(new Insets(10));
		top.setPadding(new Insets(10));
		top.setSpacing(30);
		center.setPadding(new Insets(10));
		center.setSpacing(10);
		
		
		//Update empty file list
		UpdateFileList(fileList);
		
		//Action Handlers for buttons
		submitB.setOnAction(e -> { 
			if (isValidURL(submitT.getText())) {
				outputTA.clear();
				WWC.setURL(submitT.getText());
				WWC.setWordCount(URLToWordCount(WWC.getURL()));
				outputTA.setText(WordOccurancesIterator(WWC.getWordCount()));
			}//end if
			else
				AlertBox.display("URL Error", "The URL Entered is invalid");
		});
		saveB.setOnAction(e -> {
			mySQLInsert(WWC.getWordCount(), saveT.getText());
			UpdateFileList(fileList);
			});
		loadB.setOnAction(e -> {
			outputTA.clear();
			mySQLSelect(outputTA, WWC.getWordCount(), fileList.getSelectionModel().getSelectedItem());
			outputTA.setText(WordOccurancesIterator(WWC.getWordCount()));
			});
		
		Scene scene = new Scene(borderPane, 750, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	
	}//end start
	
	/**
	 * Checks URL is valid
	 * @param url from WebpageWordCount object
	 * @return True or false if URL is valid or invalid
	 */
	public boolean isValidURL(String url) 
    { 
        /* Try creating a valid URL */
        try { 
            new URL(url).toURI(); 
            return true; 
        } 
          
        // If there was an Exception 
        // while creating URL object 
        catch (Exception e) { 
            return false; 
        } 
    }//end isValidURL
	
	/**
	 * mySQLInsert
	 * @param Map Hasmap to get word occurances for mySQL Insert
	 * @param filename Name of website entered by user
	 */
	public void mySQLInsert(Map<String, Integer> Map, String filename) {
		try
	    {
	      // create a mysql database connection
	      String myDriver = "org.gjt.mm.mysql.Driver";
	      String myUrl = "jdbc:mysql://localhost:3306/word_occurances?characterEncoding=latin1";
	      Class.forName(myDriver);
	      Connection conn = DriverManager.getConnection(myUrl, "root", "password");

	      //insert display name for word occurnaces id
	      String query = " insert into website_name (website_name)"
	        + " values (?)";
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setString (1, filename);
	      preparedStmt.execute();
	      
	      //get id number for insert words and word occurances
	      query = " select id_website_name from website_name where website_name=?";
	      preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setString(1, filename);
	      ResultSet rs = preparedStmt.executeQuery();
	      rs.first();
	      int id = rs.getInt(1);
	      
	      //loop to insert all words and word occurances for current map
	      query = " insert into word (id, word, word_occurances)" + " values (?, ?, ?)";
	      preparedStmt = conn.prepareStatement(query);
    	  preparedStmt.setInt(1, id);
	      for (Entry<String, Integer> entry : Map.entrySet()) {
	    	  preparedStmt.setString(2, entry.getKey());
	    	  preparedStmt.setInt(3, entry.getValue());
	    	  preparedStmt.execute();
	      }//end for loop
	      
	      conn.close();
	    }//end try
	    catch (Exception e)
	    {
	      System.err.println(e.getMessage());
	    }//end catch
	}//end mySQLInsert
	
	/**
	 * mySQL select to put data into HashMap
	 * @param output TextArea to put data in from map
	 * @param Map Hasmap to put MySQL data in
	 * @param filename Name of website to get word occurances for
	 */
	public void mySQLSelect(TextArea output, Map<String, Integer> Map, String filename) {
		Map.clear();
		try
	    {
	      // create a mysql database connection
	      String myDriver = "org.gjt.mm.mysql.Driver";
	      String myUrl = "jdbc:mysql://localhost:3306/word_occurances?characterEncoding=latin1";
	      Class.forName(myDriver);
	      Connection conn = DriverManager.getConnection(myUrl, "root", "password");

	      //get id number for insert words and word occurances
	      String query = " select id_website_name from website_name where website_name=?";
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setString(1, filename);
	      ResultSet rs = preparedStmt.executeQuery();
	      rs.first();
	      int id = rs.getInt(1);
	      
	      //get words and word occurances for the passed website name from mySQL
	      query = " select word, word_occurances from word where id=?";
	      preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt (1, id);
	      rs = preparedStmt.executeQuery();
	      
	      while (rs.next()) {
	    	  Map.put(rs.getString(1), rs.getInt(2));
	      }//end while
	      
	      
	      conn.close();
	    }//end try
	    catch (Exception e)
	    {
	      System.err.println(e.getMessage());
	    }//end catch
	}
	
	/**
	 * Check if file list folder exists, if not create, and show files in list
	 * @param fileList ListView of saved text files for Word Occurances
	 */
	public void UpdateFileList(ListView<String> fileList) {
		fileList.getItems().clear();
		try {
		      // create a mysql database connection
		      String myDriver = "org.gjt.mm.mysql.Driver";
		      String myUrl = "jdbc:mysql://localhost:3306/word_occurances?characterEncoding=latin1";
		      Class.forName(myDriver);
		      Connection conn = DriverManager.getConnection(myUrl, "root", "password");
		      
		      //get id number for insert words and word occurances
		      String query = " select website_name from website_name";
		      PreparedStatement preparedStmt = conn.prepareStatement(query);
		      ResultSet rs = preparedStmt.executeQuery();
		      
		      while (rs.next()) {
		    	  fileList.getItems().add(rs.getString(1));
		      }//end while
		      
		      
		}//end try
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}//end catch
		
		
		
		//fileList.getItems().add(file.getName());
	}//end UpdateFileList
	
	/**
	 * Map the word count to unsorted map
	 * @param SplitText Array of parsed words from webpage
	 * @param UnsortedMap Map to put words and word occurances in from SplitText
	 */
	public void UnsortedWordCount (String[] SplitText, Map<String, Integer> UnsortedMap) {
		//Check if word exists in map and increase count, if not add word to unsorted map
		int count = 0;
		for (String word:SplitText) { 
			if (!UnsortedMap.containsKey(word))
				UnsortedMap.put(word, 1);
			else {
				count = UnsortedMap.get(word);
				UnsortedMap.put(word, count+1);
				}//end else statement
		}//end for loop
	}//end UnsortedWordCount

	/**
	 * Takes the WebpageWordCount URL to make an unsorted map of word count
	 * @param URL String of URL from WebpageWordCount object
	 * @return The returned sorted map from SortedWordCount
	 * @see SortedWordCount
	 */
	public Map<String, Integer> URLToWordCount(String URL) {
		
		//Call function and put parsed HTML into string
		String Text = WebpageToString(URL);
		
		//Take the string text remove punctuation and capitalization, and split the string into a String array of words
		String[] SplitText = Text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+"); //Make regex pattern if used multiple times
		
		//Call function and create unsorted word count map
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		UnsortedWordCount(SplitText, tempMap);
		
		//Call function and sort the word count map
		return SortedWordCount(tempMap);
		
	}//end URLToWordCount
	
	/**
	 * Gets HTML from URL, parses tags from HTML with Jsoup and returns result in string
	 * @param page Unparsed HTML webpage
	 * @return String of parsed HTML from webpage of passed URL
	 */
	public String WebpageToString(String page) {
        String HTML = "";
        String inputLine = "";
		
		try {
			//Initialize variables
			URL url = new URL(page);
            URLConnection conn = url.openConnection();
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
            
            //Read in each line of webpage into String
            while ((inputLine = br.readLine()) != null) {
            	HTML = HTML.concat(inputLine);
            }//end while loop
            br.close();
            //Parse HTML tags
            HTML = Jsoup.parse(HTML).text();
        }//end try
		catch (MalformedURLException e) {
            e.printStackTrace();
        }//end catch 
		catch (IOException e) {
            e.printStackTrace();
        }//end catch

		return HTML;
	}//end WebpageToString

	/**
	 * Iteratates through all words in Map
	 * @param SortedMap Sorted Hash Map from WebpageWordCount object
	 * @return Map as a string to display in output
	 */
	public String WordOccurancesIterator(Map<String, Integer> SortedMap) {
		Iterator<Map.Entry<String, Integer>> entries = SortedMap.entrySet().iterator();
		int i = 0;
		String result = "";
		
		while (entries.hasNext() && i < SortedMap.size()) {
		    Map.Entry<String, Integer> entry = entries.next();
		    result = result.concat((i+1 + ". " + entry.getKey() + ", " + entry.getValue() + "\n"));
		    i++;
		}//end while loop		
		return result;
	}//end WordOccurancesIterator

}//end TextAnalyzer
