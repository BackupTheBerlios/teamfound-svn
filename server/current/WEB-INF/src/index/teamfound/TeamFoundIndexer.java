/**
 * Created on Mar 21, 2006
 */
package index.teamfound;

import java.net.URL;
import java.util.Vector;
import java.util.HashSet;
import java.io.StringReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.Iterator;
import java.util.Collection;

import index.Parser.Html.HTMLParser;
import index.crawler.teamfound.TeamFoundCrawler;

import controller.IndexAccessException;

import index.Indexer;
import index.NewIndexEntry;
import index.teamfound.TeamFoundAnalyzer;

import config.teamfound.TeamFoundConfig;
import sync.*;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.*;
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
 * Implementation Indexer nach Milestone3-Spezifikation
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
	private ReadWriteSync indexsync; 
	private String indexpath;
    protected TeamFoundCrawler crawler;

	public TeamFoundIndexer(ReadWriteSync s )
	{
		indexsync = s;
		//teamfound BasePfad erfragen und indexPfad bauen 
		String path = TeamFoundConfig.getConfValue("tfpath");
		indexpath = (path+"/index");

		// TODO maximum frame fetch depth könnte in die properties ausgelagert werden
		crawler = new TeamFoundCrawler(3);
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
		if(!IndexReader.indexExists(path))
		{
				IndexWriter writer = new IndexWriter(indexpath, new TeamFoundAnalyzer(), true);
				writer.close();
		}
		
	}
	
	/**
	 * Fügt einen neuen Eintrag in den Index ein
	 *
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param document Das ENtry-Objekt welches eingefügt werden soll
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(Document doc) throws IndexAccessException
	{
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
	 * Fügt einen neuen Eintrag in den Index ein.
	 * 1. Die URL heruntergeladen ueber den Crawler
	 * 2. Das Lucene Dokument wird erstellt
	 * 3. Das Dokument wird dem Index hinzugefuegt
	 *
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param entry Das ENtry-Objekt welches eingefügt werden soll
	 * @param adress Die URL, die zu diesem Dokument führt
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(URL adress , Vector<Integer> cats) throws IndexAccessException
	{
		try
		{
			NewIndexEntry entry = crawler.fetch(adress);
			
			//schreibewunsch anmelden
			indexsync.doWrite();
			
			//Dokument erstellen und in den Index schreiben
			Document doc = getdocument(entry, cats);
			
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
	 * Erzeugt ein Lucene Dokument,dass wir dann dem Index inzufuegen koennen.
	 * @return Returns an org.apache.lucene.document.Document 
	 */
	private Document getdocument(NewIndexEntry entry, Vector<Integer> cats)  throws IOException, InterruptedException		
	{
		Document doc = new Document();

		//URL als KeyWord-Feld ins Document adden
		//Bedeutet es wird nicht in Tokens zerlegt aber indexiert und gespeichert
		//es ist suchbar
		doc.add(Field.Keyword("url",entry.getUrl().toString()));

		//ein Datum ( z.b. letzte modifikation)
		//bin noch nicht sicher ob wir das brauchen, da wir ja ein Datum in der Daten
		//bank haben .. allerdings gibt uns das die Moeglichkeit einfach aus dem Index alle
		//eintraege rauszufischen die z.b. ein bestimmtes alter ueberschritten haben
		//auch hier nicht in Tokens zerlegt aber suchbar gespeichert
		/* doc.add(Field.Keyword("datum",datum));*/

		//TODO anderen HTML Parser suchen -> dann koennen wir ohne das apbspeichern der URL als File auskommen
		//TODO Test ob File existiert
		
		index.Parser.Html.HTMLParser parser = new HTMLParser(new StringReader(entry.getContent()));
		
		//Ok, der Parser liefert uns einen Reader der uns den Text ohne HTML Tags etc.
		//liefert und dieser kann dann indexiert und suchbar gespeichert werden
		doc.add(Field.Text("contents",parser.getReader()));
		
		//wir speichern die Zusammenfassung der Seite aber indexieren nicht
		//kann nur zum anzeigen bei Suchergebnissen genutzt werden nicht durchsuchbar
		doc.add(Field.UnIndexed("summary",parser.getSummary()));

		//Title wird wieder indexiert damit er zusaetzlich durchsucht werden kann
		doc.add(Field.Text("title", parser.getTitle()));

		//Kategorien: wir bauen erstmal einen String der die ids beinhaltet und 
		//z.b. so aussieht "3 45 6 ..."
		//eine Aufzaehlung aller Kathegorien mit leerzeichen getrennt
		//Dann nehmen wir unseren eigen Analyser der bei dem Feld Cats automatisch
		//den org.apache.lucene.analysis.WhitespaceTokenizer benutzt somit haben
		//wir die ids schoen durchsuchbar gespeichert
		String catstring  = createCatString(cats);
		//Kategorien speicher indexieren und zwar als tokens
		doc.add(new Field("cats",catstring,true,true,true));

		return doc; 
		
	}

	private String createCatString(Collection<Integer> cats)
	{
		String catstring= new String();
		Iterator it = cats.iterator();
		while(it.hasNext())
		{
			catstring = (catstring + ((Integer)it.next()).toString() + " ");
		}
		return catstring;

	}


	/**
	 * Eine Query auf dem Index ausführen
	 * @param query Der Query-String
	 * @param categorys Array mit Kathegorie-ids in denen gesucht werden muss es muss mindestens eine id mitgeliefert werden (mindestens die root id des Projektes)
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @param count Wieviele ergebnisse sollen geliefert werden
	 * @param offset wieviele ergebebnisse waren vorher schon da
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public Vector<Document> query(String query, int[] categorys, int count , int offset ) throws IndexAccessException
	{
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
	
	/**
	 *	Bei einem Dokument das Feld Category updaten 
	 * @param url Die url des zu ersetzenden Documents
	 */
	public void updateCategory(String url,HashSet<Integer> allcats) throws IndexAccessException
	{
			//1.Doc im index loeschen
			Document doc = delDoc(url);
			doc.removeField("cats");
				
			//2.Doc neu adden mit allen sich ergebenden Kats
			String cats = new String();
			java.util.Iterator allit = allcats.iterator();
			Integer tmp;
			while(allit.hasNext())
			{
				tmp = (Integer)allit.next();
				cats = (cats + tmp.intValue() +" ");
			}
			doc.add(new org.apache.lucene.document.Field("cats",cats,true,true,true));
			addUrl(doc);

	}
	
	
}
