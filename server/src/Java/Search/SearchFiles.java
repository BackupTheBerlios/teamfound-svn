package Search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;

class SearchFiles {
  public static void main(String[] argv) {
    try 
    {
      Searcher searcher = new IndexSearcher("index");
      Analyzer analyzer = new StandardAnalyzer();

      String word;
      String usage =new String("SearchFiles <Suchbegriff>");
      if (argv.length!=1) 
      {
			  System.err.println("Usage: " + usage);
			  return;
      } 
      else	
      {
			  word = new String(argv[0]);			  
      }	

	Query query = QueryParser.parse(word, "contents", analyzer);
	System.out.println("Searching for: " + query.toString("contents"));

	Hits hits = searcher.search(query);
	System.out.println("<H3>" + hits.length() + " total matching documents</H3>");

	
	  for (int i = 0; i < hits.length(); i++) 
	  {
	    Document doc = hits.doc(i);
	    String path = doc.get("path");
	    if (path != null) 
	    {
              System.out.println(i + ". Pfad: " + path + "<br>");
	    } 
	    else 
	    {
              String url = doc.get("url");
              if (url != null) 
              {
            	  System.out.println(i + ". URL: " + "<a href=" + url + ">" + url + "</a><br>");
            	  System.out.println("  Titel: " + doc.get("title")+ "<br><br>");
              } 
              else 
              {
            	  System.out.println(i + ". " + "No path nor URL for this document <br>");
              }
	    }
	  }

	  
	
      
      searcher.close();

    } 
    catch (Exception e) 
    {
      System.out.println(" caught a " + e.getClass() +
			 "\n with message: " + e.getMessage());
    }
  }
}
