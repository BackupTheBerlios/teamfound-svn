/*
 * Created on Dec 6, 2005
 */
package index;

import java.net.URL;

import controller.IndexAccessException;

import org.apache.lucene.document.Document;

import java.util.Vector;
import java.util.HashSet;

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
	 * @param Vector alle Kategorien
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(NewIndexEntry entry, URL adress, Vector<Integer> cats ) throws IndexAccessException;
	
	/**
	 * Eine Query auf dem Index ausführen
	 * @param query Der Query-String
	 * @param categorys Array mit Kathegorie-ids in denen gesucht werden muss es muss mindestens eine id mitgeliefert werden (mindestens die root id des Projektes)
	 * @return Das Suchergebnis zu diesem query oder null wenn nichts gefunden wurde
	 * @param count Wieviele ergebnisse sollen geliefert werden
	 * @param offset wieviele ergebebnisse waren vorher schon da
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public Vector<Document> query(String query, int[] categorys, int count, int offset) throws IndexAccessException;
	
	/**
	 * Ein einzelnes Dokument aus dem Index loeschen
	 * @param url Die url des zu loeschenden Documents
	 * @return das geloeschte Document (wird benoetigt damit updates moeglich werden)
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	public Document delDoc(String url) throws IndexAccessException;
	
	/**
	 * Alle Dokumente einer bestimmten Kategorie aus dem Index loeschen
	 * @param id Die ID  der Kategorie
	 * @return Vector mit geloeschten Documenten (wird benoetigt damit updates moeglich werden)
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Excpetions einpacken.
	 */
	//TODO 
	//public Vector<Document> delCat(int id) throws IndexAccessException;
	
	/**
	 * Fügt einen neuen Eintrag in den Index eina
	 * Diese Funktion geht davon aus, dass der Eintrag ncoh nicht existiert
	 * Sollte er existieren wuerde es doppelte Eintraege geben!
	 * 
	 * @param document Das ENtry-Objekt welches eingefügt werden soll
	 * @throws IndexAccessException Bei Zugriffsfehlern auf den Index, kann andere Exceptions einpacken
	 */
	public void addUrl(Document doc) throws IndexAccessException;

	/**
	 * neuen Index erstellen
	 * Eigentlich nur beim Anlegen von neuen TeamfoundServer
	 * benoetigt. Der Pfad muss dann aus Konfig Daten kommen.
	 *
	 * @param path Dateipfad, gibt an wo der Index angelegt wird
	 * 				sollte der Basispfad von Teamfound sein
	 *
	 */
	public void createIndex(String path) throws java.io.IOException;
	
	/**
	 *	Bei einem Dokument das Feld Category updaten 
	 * @param url Die url des zu ersetzenden Documents
	 */
	public void updateCategory(String url,HashSet<Integer> allcats) throws IndexAccessException;
		
}
