/*
 * Created on Dec 6, 2005
 */
package index;

import java.net.URL;

import controller.IndexAccessException;
import controller.SearchResponse;

/**
 * Interface f�r einen Indexer nach Milestone2-Spezifikation
 * 
 * Der Indexer �bernimmt die low-level Arbeiten direkt am Index, er k�mmert sich
 * weder um Datenbank (Kategorien, Urls etc) noch um Download der Daten
 * 
 * Der Indexer muss selbst f�r synchronisierung der Zugriffe sorgen: Es wird 
 * angenommen das jede Indexer-Instanz Threadsafe ist! 
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 * @todo Evtl. die �bergabe der Daten von File nach OutputStream oder so �ndern
 */
public interface Indexer {
	/**
	 * F�gt einen neuen Eintrag in den Index ein
	 * @param entry Das ENtry-Objekt welches eingef�gt werden soll
	 * @param adress Die URL, die zu diesem Dokument f�hrtt
	 * @param path Pfad des Indexes (muss aus den Properties kommen)
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(NewIndexEntry entry, URL adress ) throws IndexAccessException;
	
	/**
	 * Eine Query auf dem Index ausf�hren
	 * @param query Der Query-String
	 * @param categorys Array mit Kathegorie-ids in denen gesucht werden muss es muss mindestens eine id mitgeliefert werden (mindestens die root id des Projektes)
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde 
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public SearchResult query(String query, int[] categorys) throws IndexAccessException;
	
}
