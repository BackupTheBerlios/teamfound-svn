/**
 * Created on Mar 21, 2006
 */
package index.teamfound;


import java.net.URL;
import java.util.Vector;

import controller.IndexAccessException;
import controller.response.SearchResponse;

import index.Indexer;
import index.NewIndexEntry;
import index.teamfound.TeamFoundAnalyzer;

import config.Config;

import sync.*;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.HitIterator;
import org.apache.lucene.search.Hit;			

/**
 * Implementation Indexer nach Milestone2-Spezifikation
 * 
 * Der Indexer übernimmt die low-level Arbeiten direkt am Index, er kümmert sich
 * weder um Datenbank noch um Download der Daten
 * 
 * Der Indexer muss selbst für synchronisierung der Zugriffe sorgen: Es wird 
 * angenommen das jede Indexer-Instanz Threadsafe ist! 
 * 
 * 
 * @author Martin Klink
 *
 */
public class TeamFoundIndexer implements Indexer {

	//der Indexer braucht zugang zur Konfiguration damit er den Index findet
	private Config tfconfig;
	private ReadWriteSync indexsync; 
	
	public TeamFoundIndexer(Config c, ReadWriteSync s )
	{
		tfconfig = c;
		indexsync = s;
	}
	
	/**
	 * neuen Index erstellen
	 * Eigentlich nur beim Anlegen von neuen TeamfoundServer
	 * benoetigt. Der Pfad muss dann aus Konfig Daten kommen.
	 *
	 * @param path Dateipfad, gibt an wo der Index angelegt wird
	 * 				sollte der Basispfad von Teamfound sein
	 *
	 */
	public void createIndex(String path) throws java.io.IOException
	{
		
		String indexpath = (path+"/index");
		
		if(!IndexReader.indexExists(path))
		{
				IndexWriter writer = new IndexWriter(indexpath, new TeamFoundAnalyzer(), true);
				writer.close();
		}
		
	}
	
	/**
	 * Fügt einen neuen Eintrag in den Index eina
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param entry Das ENtry-Objekt welches eingefügt werden soll
	 * @param adress Die URL, die zu diesem Dokument führt
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(NewIndexEntry entry, URL adress) throws IndexAccessException
	{
		//teamfound BasePfad erfragen und indexPfad bauen 
		String path = tfconfig.getConfValue("tfpath");
		String indexpath = (path+"/index");
		
		try
		{
			//schreibewunsch anmelden
			indexsync.doWrite();
			
			//Dokument erstellen und in den Index schreiben
			Document doc = entry.getdocument();
			IndexWriter writer = new IndexWriter(indexpath, new TeamFoundAnalyzer(), false);
			writer.addDocument(doc);
			
			//TODO eigentlich muss man nur gelegentlich optimieren, irgentwann noch ein Management dafuer
			writer.optimize();
			writer.close();
			
			//schreiben fertig
			indexsync.endWrite();

		}
		catch(java.io.IOException io)
		{
			//z.b. File existiert gar nicht
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer : addUrl()"+io);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(io);
			throw a;
									
		}
		catch(java.lang.InterruptedException ie)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer : addUrl()"+ ie);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(ie);
			throw a;
		}
		finally
		{
			//auf jeden Fall muessen wir den lock freigeben
			indexsync.endWrite();
		}
	}
		
	/**
	 * Eine Query auf dem Index ausführen
	 * @param query Der Query-String
	 * @param categorys Array mit Kathegorie-ids in denen gesucht werden muss es muss mindestens eine id mitgeliefert werden (mindestens die root id des Projektes)
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public SearchResponse query(String query, int[] categorys) throws IndexAccessException
	{
		//lesewunsch anmelden
		//indexsync.doRead();
		
 		
		String path = tfconfig.getConfValue("tfpath");
		String indexpath = (path+"/index");		
		try
		{
			//lesewunsch anmelden
			indexsync.doRead();
		
			IndexSearcher searcher = new IndexSearcher(indexpath);		
			
			//erstmal die Frage aus den Suchworten bilden		
			String querys[] = new String [2];
			querys[0] = query;
			querys[1] = query;
		
			String felder[] = new String[2];
			felder[0] = new String("contents"); 
			felder[1] = new String("title");
			Query humanquery = MultiFieldQueryParser.parse(querys, felder, new org.apache.lucene.analysis.standard.StandardAnalyzer());
		
		
			//So nun den zweiten Teil der Frage aus den Kategorien bilden 
			BooleanQuery catquerry = new BooleanQuery();
			TermQuery[] tq = new TermQuery[categorys.length];
			Term startterm = new Term("cats","0");
			Term t;
		
			if(categorys.length < 1)
			{
				//TODO was soll nun sein 
				IndexAccessException a = new IndexAccessException("keine Category angegeben!");
				throw a;

			}	
		
			if(categorys.length < 2)
			{
				t = startterm.createTerm(String.valueOf(categorys[0]));
                tq[0] = new TermQuery(t);
				catquerry.add(tq[0], BooleanClause.Occur.MUST);
			}
			else
			{
				for(int i=0; i<categorys.length; i++)
				{
					t = startterm.createTerm(String.valueOf(categorys[i]));
					tq[i] = new TermQuery(t);
					//nur zum testen
					//System.out.println("id["+i+"]:"+categorys[i] +" TermQuery: "+tq[i].toString());
					catquerry.add(tq[i], BooleanClause.Occur.SHOULD); 
				}
			}
			//nur zum testen
			//System.out.println(catquerry.toString());
			//nur zum testen
			//System.out.println(humanquery.toString());

			//Vollstaendige Frage bauen
			BooleanQuery completequery = new BooleanQuery();
		
			completequery.add(humanquery, BooleanClause.Occur.MUST);
			completequery.add(catquerry, BooleanClause.Occur.MUST);
			
			//nur zum testen
			System.out.println(completequery.toString());

						
			String keywords[] = new String[1];
			keywords[0] = query;
			SearchResponse result = new SearchResponse(keywords);
			
			Hits hits = searcher.search(completequery);
			//nur zum testen
			//System.out.println("Anzahl Ergebnisse:"+hits.length());
			

			
			HitIterator it = (HitIterator)hits.iterator();
			Hit hit;
			Document d;
			
			Vector<Document> docvec = new Vector<Document>();
			
			//TODO Das geht eigentlich nicht aus PerformanceGruenden ...
			//Document Vector bauen
			while(it.hasNext())
			{
				hit = (Hit)it.next();
				d = hit.getDocument();

				//nur zum testen
				//System.out.println(d.get("url"));
			
				docvec.add(d);
			}

			//den Vektor mit Dokumenten ins SearchResult
			result.addSearchResults(docvec);
			
			searcher.close();
			//lesen fertig
			indexsync.endRead();


			return(result);

			
		}
		catch(java.io.IOException ioe)
		{
			//TODO
			System.out.println(ioe);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(ioe);
			throw a;

		}
		catch(org.apache.lucene.queryParser.ParseException pe)
		{
			//TODO
			System.out.println(pe);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(pe);
			throw a;

		}
		catch(java.lang.InterruptedException ie)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer query()"+ ie);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(ie);
			throw a;
		}
		finally
		{
			//auf jeden Fall muessen wir den lock freigeben
			indexsync.endRead();
		}
				
		
	}
	
	
	
}
