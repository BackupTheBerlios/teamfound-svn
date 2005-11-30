package Search;

/**
 * Martin Klink
 * einfacher Searcher fuer AppletVersion 0.1
 */



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

public class SearchFiles {
  public String search(String keywords) 
  {
	 String response = new String();
    try 
    {
      Searcher searcher = new IndexSearcher("/tmp/index");
      Analyzer analyzer = new StandardAnalyzer();

      	
    
    //moechte gern ueber Title und Inhalt der Seite suchen
    String felder[] = new String[2];
    felder[0] = new String("contents"); 
    felder[1] = new String("title");
	Query query = MultiFieldQueryParser.parse(keywords, felder, analyzer);
	//System.out.println("Searching for: " + query.toString("contents"));

	Hits hits = searcher.search(query);
	response=(" TeamFound " +hits.length() + " sites<br><br>");

	
	  for (int i = 0; i < hits.length(); i++) 
	  {
	    Document doc = hits.doc(i);
	    String path = doc.get("path");
	    if (path != null) 
	    {
              response=(response + i + ". Pfad: " + path + "<br>");
	    } 
	    else 
	    {

	    	String url = doc.get("url");
              if (url != null) 
              {
						response = (response + i + ". "+ "<a href=" + url + ">" + doc.get("title") + "</a><br>");
            	  	response = (response + "<font size = -1>" + doc.get("summary")+ "</font><br>");
            	   response = (response + "<font size = -2>" + url+ "</font><br><br>");
            	  //System.out.println("Score " + hits.score(i)+ "<br>");
              } 
              else 
              {
            	  response = (response + i + ". " + "No path nor URL for this document <br>");
              }
	    }
	  }

	  
	
      
      searcher.close();
		return response;

    } 
    catch (Exception e) 
    {
      response = (response + " caught a " + e.getClass() +
			 "\n with message: " + e.getMessage());
		return response;
    }
  }
}
