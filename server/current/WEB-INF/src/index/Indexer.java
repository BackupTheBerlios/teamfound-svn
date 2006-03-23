/*
 * Created on Dec 6, 2005
 */
package index;

import java.io.File;
import java.net.URL;

import controller.IndexAccessException;
import controller.SearchResult;

/**
 * Interface für einen Indexer nach Milestone2-Spezifikation
 * 
 * Der Indexer übernimmt die low-level Arbeiten direkt am Index, er kümmert sich
 * weder um Datenbank (Kategorien, Urls etc) noch um Download der Daten
 * 
 * Der Indexer muss selbst für synchronisierung der Zugriffe sorgen: Es wird 
 * angenommen das jede Indexer-Instanz Threadsafe ist! 
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 * @todo Evtl. die Übergabe der Daten von File nach OutputStream oder so ändern
 */
public interface Indexer {
	/**
	 * Fügt einen neuen Eintrag in den Index ein
	 * @param entry Das ENtry-Objekt welches eingefügt werden soll
	 * @param adress Die URL, die zu diesem Dokument führtt
	 * @param path Pfad des Indexes (muss aus den Properties kommen)
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(NewIndexEntry entry, URL adress ) throws IndexAccessException;
	
	/**
	 * Eine Query auf dem Index ausführen
	 * @param query Der Query-String
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public SearchResult query(String query) throws IndexAccessException;
	
}
