package Index;

/**
 * Author: Martin Klink 
 * Datum : 20.11.05
 * 
 * erster Indexersteller fuer Teamfound
 * Nutze erstmal den HTMLParser vom Apache-Projekt der bei den Demos dabei war und
 * halte mich so an die Funktionsweise von HTMLIndexer 
 */


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import java.io.File;
import java.util.Date;


public class TeamFoundIndexer {

	
	private static IndexReader reader;		  // zum Index durchsuchen
	private static IndexWriter writer;		  // zum Anfuegen unseres Html Files
	private static TermEnum urlIter;		  // iterator ueber Url der Page
	
	
	  public static void main(String[] argv) {
	    try 
	    {
	      String index = "index";	// lege directory fest auf index erstmal einfachheit halber
	      							// sowas sollten wir spaeter ueber einstellbare properties loesen
	      boolean create = false;	// Index neu erstellen? erstmal wollen wir das nicht
	      
	      File file = null;			//hier soll das uebergebene file rein
	      String url = new String();//hier die url
	      
	      //so erstmal aus dem Perl script rufen
	      String usage = "TeamFoundIndexer <File> <URL>";
	      
	      //2 Argumente das File und die URL einlesen
	      if (argv.length!=2) 
	      {
				  System.err.println("Usage: " + usage);
				  return;
	      } 
	      else	
	      {
				  file = new File(argv[0]);
				  url = new String(argv[1]);
	      }	
	      
	      //von unserm Aufbau her werden erstmal nur einzelne html files geadded
	      if (file.isDirectory())  
	      {
	    	  System.err.println("Not a file! " + file.getName() +" " + url + " Usage: " + usage);
	    	  return;
	      }
	      
	    
	      Date start = new Date();
	      
	      
	      // ich brauche noch ne bessre Moeglichkeit um zu pruefen ob index existiert
	      try
	      {
	    	  writer = new IndexWriter(index, new StandardAnalyzer(), create);
	    	  writer.close();
	      }
	      catch(java.io.IOException e)
	      {
	    	  System.out.println("Muss neuen Index anlegen.");
	    	  create = true;	    	  	    	  	    	  
	      }
	      	      	            	      	      
	      //hinzufuegen der Seite
	      indexDocs(file, index, create, url);		
	      
	      Date end = new Date();

	      System.out.print(end.getTime() - start.getTime());
	      System.out.println(" total milliseconds");

	    } catch (Exception e) 
	    {
	      System.out.println(" caught a " + e.getClass() +
				 "\n with message: " + e.getMessage());
	    }
	  }

	  

	  /**
	   * Fuege File zum Index dazu , sollte es schon existieren loesche ich es
	   * vorher aus dem Index.
	   * 
	   * @param file
	   * @param index
	   * @param create
	   * @param url
	   * @throws Exception
	   */
	  private static void indexDocs(File file, String index, boolean create, String url)
	       throws Exception 
	       {
		  		if(create)
		  		{
		  			try
				      {
				    	  writer = new IndexWriter(index, new StandardAnalyzer(), create);
				    	
				    	  //dieser Wert gibt soweit ich das sehe, die maximale Anzahl an 
					      //unterschiedlichen Worten pro Document an
					      // hier kann man optimieren ...  je hoeher die Zahl umso mehr Speicher wird  benoetigt
					      // erstmal eher niedrig da ich davon ausgehe, dass ne Website nicht soviel 
					      //braucht
					      writer.maxFieldLength = 1000;
					      
					      //File in Index hinzufuegen    
					      indexDocs(file,url);
					      System.out.println("Close indexWriter...");
					      writer.close();
				      }
				      catch(java.io.IOException e)
				      {
				    	  System.out.println("Schreibschutzprobleme?."+e);				    	  
				      }
				      
		  		}
		  		else
		  		{
	     
				      reader = IndexReader.open(index);		  // open existing index
				      urlIter = reader.terms(new Term("url", "")); // init url iterator
				      	
				      // durchsuche Index nach Seite wenn schon drinn loesche erstmal und
				      // indiziere neu (ich gehe davon aus dass URL eindeutig als id verwendbar ;-)
				      while (urlIter.term()!=null && urlIter.term().field().equals("url")) 
				      {			    	  
				    	  if (urlIter.term().text().equals(url))
				    	  {
				    		  System.out.println("deleting " + (urlIter.term().text()));
				    		  int i;
				    		  i = reader.delete(urlIter.term());
				    		  System.out.println("Sites deleted : "+ i);
				    	  }
				    	  
				    	  if(!urlIter.next())
				    		  break;
				      }
				      
				      urlIter.close();				  // close uid iterator
				      reader.close();				  // close existing index
				      
				      try
				      {
				    	  writer = new IndexWriter(index, new StandardAnalyzer(), create);
					      writer.maxFieldLength = 1000;
					      
					      //File in Index hinzufuegen    
					      indexDocs(file,url);
					      System.out.println("Close indexWriter...");
					      writer.close();
				      }
				      catch(java.io.IOException e)
				      {
				    	  System.out.println("Schreibschutzprobleme?."+e);	
				      }
				      
		  		}
	     	  }

	  /**
	   * Das wirkliche einfuegen in den Index
	   */
	  private static void indexDocs(File file, String url) throws Exception 
	  {
	   
			// index nur von html htm oder txt
		    if (file.getPath().endsWith(".html") || 
			       file.getPath().endsWith(".htm") || 
			       file.getPath().endsWith(".txt") ||
			       file.getPath().endsWith(".php") ||
			       file.getPath().endsWith(".pl") ||
			       file.getPath().endsWith(".shtml") 
			       )  
		    {
		    	
			      
		    		// Document erstellen und hinzufuegen
		    		  Document doc = HTMLDocument.Document(file,url);
		    		  System.out.println("adding " + doc.get("url"));
		    		  writer.addDocument(doc);
		    		  System.out.println("Optimizing index...");
		    	      writer.optimize();
		    	    
		    }
		    else
		    	System.out.println("Falsche Dateiendung!");
	  }	
}
