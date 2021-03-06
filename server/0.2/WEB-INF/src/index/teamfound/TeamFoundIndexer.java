/**
 * Created on Mar 21, 2006
 */
package index.teamfound;


import java.net.URL;
import java.util.Vector;

import controller.IndexAccessException;

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
import org.apache.lucene.index.TermDocs;

/**
 * Implementation Indexer nach Milestone2-Spezifikation
 * 
 * Der Indexer �bernimmt die low-level Arbeiten direkt am Index, er k�mmert sich
 * weder um Datenbank noch um Download der Daten
 * 
 * Der Indexer muss selbst f�r synchronisierung der Zugriffe sorgen: Es wird 
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
	 * F�gt einen neuen Eintrag in den Index eina
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param document Das ENtry-Objekt welches eingef�gt werden soll
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(Document doc) throws IndexAccessException
	{
		//teamfound BasePfad erfragen und indexPfad bauen 
		String path = tfconfig.getConfValue("tfpath");
		String indexpath = (path+"/index");
		
		try
		{
			//schreibewunsch anmelden
			indexsync.doWrite();
			
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
			System.out.println("TeamFoundIndexer : addUrl(doc)"+io);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(io);
			throw a;
									
		}
		catch(java.lang.InterruptedException ie)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer : addUrl(doc)"+ ie);
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
	 * F�gt einen neuen Eintrag in den Index eina
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param entry Das ENtry-Objekt welches eingef�gt werden soll
	 * @param adress Die URL, die zu diesem Dokument f�hrt
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
	 * Eine Query auf dem Index ausf�hren
	 * @param query Der Query-String
	 * @param categorys Array mit Kathegorie-ids in denen gesucht werden muss es muss mindestens eine id mitgeliefert werden (mindestens die root id des Projektes)
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @param count Wieviele ergebnisse sollen geliefert werden
	 * @param offset wieviele ergebebnisse waren vorher schon da
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public Vector<Document> query(String query, int[] categorys, int count , int offset ) throws IndexAccessException
	{
 		
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
			Query humanquery = MultiFieldQueryParser.parse(querys, felder, new TeamFoundAnalyzer());
		
		
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
			
			//bei nur einer Kategorie muessen die gefundenen Seiten in der Kategorie sein
			if(categorys.length < 2)
			{
				t = startterm.createTerm(String.valueOf(categorys[0]));
                tq[0] = new TermQuery(t);
				catquerry.add(tq[0], BooleanClause.Occur.MUST);
			}
			//bei mehreren Kategorien muessen die Seiten nur in einer der Kategorien sein
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

			//die komplette Anfrage an LuceneIndex stellen
			Hits hits = searcher.search(completequery);
			//nur zum testen
			//System.out.println("Anzahl Ergebnisse:"+hits.length());
			
			//neue Ergebnisse?
			if(hits.length() < offset)
			{
				return null;
			}
			
			//Wieviel Docs koennen wir liefern
			int ende = offset+count;
			if(hits.length() < ende)
				ende = hits.length();
			
			Document d;
			Vector<Document> docvec = new Vector<Document>();
			
			//Document Vector bauen
			for(int i = offset; i < ende; i++) 
			{
				d = hits.doc(i);

				//nur zum testen
				//System.out.println(d.get("url"));
			
				docvec.add(d);
			}

			
			searcher.close();
			//lesen fertig
			indexsync.endRead();


			return(docvec);

			
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
	
	/**
	 * Ein einzelnes Dokument aus dem Index loeschen
	 * @param url Die url des zu loeschenden Documents
	 * @return das geloeschte Document (wird benoetigt damit updates moeglich werden)
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public Document delDoc(String url) throws IndexAccessException
	{
		//teamfound BasePfad erfragen und indexPfad bauen 
		String path = tfconfig.getConfValue("tfpath");
		String indexpath = (path+"/index");
		
		try
		{
			//schreibewunsch anmelden
			indexsync.doWrite();
			
			//Reader erstellen. Document auslesen und loeschen
			IndexReader reader = IndexReader.open(indexpath);
			Document doc = new Document();
			Term t = new Term("url",url);
			TermDocs tdocs = reader.termDocs(t);
			//TODO eigentlich sollte es nur einen URL geben falls nicht werden einfach
			//alle geloescht und nur das letzte Dokument wir zurueckgegeben
			int docnum;
			while(tdocs.next())
			{
				docnum = tdocs.doc();
				doc = reader.document(docnum);
				reader.deleteDocument(docnum);
			}
			tdocs.close();
			
			reader.close();
						
			//schreiben fertig
			indexsync.endWrite();
			return(doc);
		}
		catch(java.io.IOException io)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer : delDoc(url)"+io);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(io);
			throw a;
									
		}
		catch(java.lang.InterruptedException ie)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer : delDoc(url)"+ ie);
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
	

		
	
	
}
