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
	/**
	 * Fügt einen neuen Eintrag in den Index ein
	 * @param entry Das ENtry-Objekt welches eingefügt werden soll
	 * @param adress Die URL, die zu diesem Dokument führt
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(NewIndexEntry entry, URL adress) throws IndexAccessException
	{
		int i = 0;
	}
		
	/**
	 * Eine Query auf dem Index ausführen
	 * @param query Der Query-String
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public SearchResult query(String query) throws IndexAccessException
	{
		return(new TeamFoundSearchResult());	
	}
	
}
