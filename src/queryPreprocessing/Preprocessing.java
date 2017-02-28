package queryPreprocessing;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;


/**
 * 
 * 
 * 
 * 
 * 
 * @author rohith
 *
 */

public class Preprocessing {

	

	public static TokenStream tokenStream;
	public static FileWriter fileWriter = null;

	
	//custom stop word list
	final static List<String> stop_Words = Arrays.asList("We", "had", "a", "couple", "like", "the", "I", "a", "an",
			"and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on",
			"or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with",
			"really", "it's", "very", "just", "Ok", "I've", "been", "here", "my", "3", "times");

	final static CharArraySet stopSetCustom = new CharArraySet(Version.LUCENE_48, stop_Words, true);

	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	public Preprocessing() {
		// TODO Auto-generated constructor stub
	}

	
	
	//  reads the reviewtext reviewid and votes from the json file
	// prints in the console for test purposes
	// a json parser

	public static void printjson() throws Exception, IOException, ParseException {

		File file = new File("C:/Users/rohith/Desktop/yelp_dataset_challenge_academic_dataset/review.json");

		fileWriter = new FileWriter("C:/Users/rohith/Desktop/yelp_dataset_challenge_academic_dataset/processedQuery.json");
		fileWriter = new FileWriter("C:/Users/rohith/Desktop/yelp_dataset_challenge_academic_dataset/taggedQuery.json");
		

		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String jsonContent = null;

		int i = 0;
		Long useful = (long) 1.00;
		
		//parsing the json text from the file one by one and 
		//reading out the useful votes,review text and review id
		
		while ((jsonContent = bufferedReader.readLine()) != null) {
			JSONObject jsonObject = JSONReader(jsonContent);


			JSONObject votes = (JSONObject) jsonObject.get("votes");
			System.out.println("test useful vount" + useful);
			useful = (Long) votes.get("useful");

			String reviewId = (String) jsonObject.get("review_id");
			String reviewText = (String) jsonObject.get("text");

			System.out.println("#################");
			System.out.println("bussi id is" + "   -----" + reviewId + "   usefulVotes is" + "   -----" + useful);			
			System.out.println("data review text" + reviewText);
			System.out.println("#################");
			
			String reducedText;
			
			//function to remove stops words when line by line it is parsed
			reducedText = removeStopwords(reviewText);

			System.out.println("Processed data is" + reviewText);
			System.out.println(reducedText);
			

			 
			String taggedReducedtext;
			//a function to tagge the processed reiview text
			taggedReducedtext=tagging(reducedText);
			
			//writing to csv format
			writetoCsv(reviewId,reducedText,taggedReducedtext);

		}


	}

	public static void writetoCsv(String reviewId, String reviewText,String taggedreviewText) throws Exception {

		FileWriter fileWriter1,fileWriter2 = null;

		fileWriter1 = new FileWriter("C:/Users/rohith/Desktop/yelp_dataset_challenge_academic_dataset/processedQuery.json");
		fileWriter2 = new FileWriter("C:/Users/rohith/Desktop/yelp_dataset_challenge_academic_dataset/taggedQuery.json");
		
		// a file writer writing the processes review  text after stop words removal and steming
		//
		fileWriter1.append(NEW_LINE_SEPARATOR);
		fileWriter1.append("test1");
		fileWriter1.append(COMMA_DELIMITER);
		fileWriter1.append(reviewId);
		fileWriter1.append(COMMA_DELIMITER);
		fileWriter1.append(reviewText);
		fileWriter1.flush();
		fileWriter1.close();
		
		//a filewrite  to write the processed text after tagging to another file
		fileWriter2.append(NEW_LINE_SEPARATOR);
		fileWriter2.append("test2");
		fileWriter2.append(COMMA_DELIMITER);
		fileWriter2.append(reviewId);
		fileWriter2.append(COMMA_DELIMITER);
		fileWriter2.append(taggedreviewText);
		fileWriter2.flush();
		fileWriter2.close();
		
		
		

	}

	
	//removal of stop words using custom stop words
	public static String removeStopwords(String data) throws Exception {

		String text = data;
		System.out.println("**********************************88");
		System.out.println(text);
		System.out.println("*****************************88*88");
		tokenStream = new StandardTokenizer(Version.LUCENE_48, new StringReader(text.trim()));

		tokenStream = new StopFilter(Version.LUCENE_48, tokenStream, stopSetCustom);
		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
			String term = charTermAttribute.toString();
			sb.append(term + " ");
		}
		// System.out.println("in funcrion");
		System.out.println("#################################");
		System.out.println(sb);
		System.out.println("#################################");

		return data;
	}
	
	
	//tagging using stanford postagger
	public static String tagging( String reviewText) throws Exception, IOException
	{
		
		String a = "I like watching movies and eating popcorn very much";
		MaxentTagger tagger =  new MaxentTagger("taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(reviewText);
		
		//String str = "helloslkhellodjladfjhello";
		String findStr = "_VBG";
		int lastIndex = 0;
		int count = 0;
		String test = "mynameis";

		while(lastIndex != -1){

		    lastIndex = tagged.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		
		//for test
		System.out.println("_VBG implies  Verb, gerund or present participle ");
		System.out.println("the count of verb is " + count);		
		System.out.println(tagged);
		
		return tagged;
	}
	
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		printjson();

	}

	
	// a json parser
	public static JSONObject JSONReader(String jsonContent) {
		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
			return jsonObject;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
