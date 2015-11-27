package twitterSearch ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


class Tweet {
	public String username;
	public String location;
	public String time;
	public String body;
	public String linkTitle;
	public String place;
	
	public Tweet(String u, String b, String title, String c, String t) {
		this.username = u;
		this.body = b;
		this.linkTitle = title;
		this.place = c;
		this.time = t;
	}
}


public class run {
	public static final String INDEX_DIR = "index";
	
	public static void main(String[] args) throws CorruptIndexException, IOException {
		
		int counter = 0;
		BufferedReader reader = null;
		String filename = "data/tweets" + Integer.toString(counter)+".txt";
		File file = new File(filename);		
	    
	    while( file.isFile() ){
	    
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
	    	
		    while ((text = reader.readLine()) != null) {
		    	if(text.isEmpty() || text.contains( "{\"limit\":" ) ){
	             continue; 
		    	}
		    	
			  Object obj=JSONValue.parse(text);
			  JSONObject j_obj = (JSONObject)obj;
			  
			  if(j_obj == null){
				  continue;
			  }
			  
			  JSONObject j_obju = (JSONObject) j_obj.get("user");
			  JSONObject j_objp = (JSONObject) j_obj.get("place");
			  
			  String time = (String) j_obj.get("created_at");
			  String body = (String) j_obj.get("text");
			  String linkTitle = (String) j_obj.get("HTML_PAGE_TITLE");
			  
			  String username = "";
			  if(j_obju != null){
				  username = (String) j_obju.get("screen_name") ;
			  }

			  String place = "";
			  if(j_objp != null){
			  	place = (String) j_objp.get("full_name");
			  }
//		      System.out.println("file: " + filename);
//			  System.out.println( "username: " + username);
//			  System.out.println("body: " + body);
//			  System.out.println( "link: " + linkTitle);
//			  System.out.println( "place: " + place);
//			  System.out.println( "time: " + time);

	    	  Tweet tweetObj = new Tweet(username, body, linkTitle, place, time);
	    	  index(tweetObj);		

		    }
		    reader.close();
			filename = "../data/tweets" + Integer.toString(counter)+".txt";
			file = new File(filename);
			++counter;
	    }
		 

		
		//Tweet page = new Tweet("This is test Title 1", "body of the test web page", "http://www.dummy.edu");
		//index(page);		
		//search("test", 5);
	}
	
	public static void readFile(){
		
	}
	
	public static void index (Tweet tweet) {
		File index = new File(INDEX_DIR);	
		IndexWriter writer = null;
		try {	
			IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_34));
			writer = new IndexWriter(FSDirectory.open(index), indexConfig);
			//System.out.println("Indexing to directory '" + index + "'...");	
			
			Document luceneDoc = new Document();
			
			Field username = new Field("username", tweet.username, Field.Store.YES, Field.Index.NOT_ANALYZED );
			username.setBoost(3);
			luceneDoc.add(username );
			
			Field body_text =  new Field("body", tweet.body, Field.Store.YES, Field.Index.ANALYZED);
			body_text.setBoost(3);
			luceneDoc.add(body_text );

			Field linktitle = new Field("linkTitle", tweet.linkTitle, Field.Store.YES, Field.Index.ANALYZED);
			linktitle.setBoost(3);
			luceneDoc.add(linktitle);
			
			Field place = new Field("place", tweet.place, Field.Store.YES, Field.Index.ANALYZED);
			place.setBoost(3);
			luceneDoc.add(place);
			
			Field time = new Field("time", tweet.time, Field.Store.YES, Field.Index.NO);
			luceneDoc.add(time);
			
			writer.addDocument(luceneDoc);			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (writer !=null)
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static TopDocs search (String queryString, int topk) throws CorruptIndexException, IOException {
		
		IndexReader indexReader = IndexReader.open(FSDirectory.open(new File(INDEX_DIR)), true);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryparser = new QueryParser(Version.LUCENE_34, "text", new StandardAnalyzer(Version.LUCENE_34));

		try {
			StringTokenizer strtok = new StringTokenizer(queryString, " ~`!@#$%^&*()_-+={[}]|:;'<>,./?\"\'\\/\n\t\b\f\r");
			String querytoparse = "";
			while(strtok.hasMoreElements()) {
				String token = strtok.nextToken();
				querytoparse += "text:" + token + "^1" + "title:" + token+ "^1.5";
				//querytoparse += "text:" + token;
			}		
			Query query = queryparser.parse(querytoparse);
			//System.out.println(query.toString());
			TopDocs results = indexSearcher.search(query, topk);
			System.out.println(results.scoreDocs.length);	
			System.out.println(indexSearcher.doc(results.scoreDocs[0].doc).getFieldable("text").stringValue());
			
			//https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/document/Field.html#setBoost(float)
			
			return results;			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexSearcher.close();
		}
		return null;
	}
	
}


//System.out.println("=======decode=======");
//
//String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
//Object obj=JSONValue.parse(s);
//JSONArray array=(JSONArray)obj;
//System.out.println("======the 2nd element of array======");
//System.out.println(array.get(1));
//System.out.println();
//            
//JSONObject obj2=(JSONObject)array.get(1);
//System.out.println("======field \"1\"==========");
//System.out.println(obj2.get("1"));    
//
//            
//s="{}";
//obj=JSONValue.parse(s);
//System.out.println(obj);
//            
//s="[5,]";
//obj=JSONValue.parse(s);
//System.out.println(obj);
//            
//s="[5,,2]";
//obj=JSONValue.parse(s);
//System.out.println(obj);