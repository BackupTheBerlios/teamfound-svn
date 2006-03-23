/**
 * Created on Mar 21, 2006
 */
package index.teamfound;


import java.io.File;
import java.net.URL;

import controller.IndexAccessException;
import controller.teamfound.TeamFoundSearchResult;
import controller.SearchResult;

import index.Indexer;
import index.NewIndexEntry;
import index.teamfound.TeamFoundAnalyzer;

import config.Config;

import sync.*;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;

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
			System.out.println("TeamFoundIndexer"+io);
			IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(io);
			throw a;
									
		}
		catch(java.lang.InterruptedException ie)
		{
			//TODO noch was machen?
			System.out.println("TeamFoundIndexer"+ ie);
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
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public SearchResult query(String query) throws IndexAccessException
	{
		//lesewunsch anmelden
		//indexsync.doRead();

		
		//TODO
	
		
		//lesen fertig
		//indexsync.endRead();
		
		return(new TeamFoundSearchResult());	
	}
	
}
