/*
 * Created on Dec 6, 2005
 */
package index;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.lucene.document.Document;

/**
 * Ein neuer Index-Eintrag muss die Datei in welcher der Inhalt, die url von welcher die Datei stammt sowie eine Hashmap mit http-headern zur Verfügung stellen
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 * Martin Klink
 * zusaetzlich eine Methode um ein LuceneDocument zu erstellen
 *
 */
public interface NewIndexEntry {
	/**
	 * Liefert die temporäre Datei
	 * @deprecated
	 * @return
	 */
	public File getFile();
	
	
	/**
	 * Liefert den Inhalt des Dokuments
	 * 
	 */
	public String getContent();
	
	/**
	 * Liefert die URL für diese Inhalte
	 * @return
	 */
	public URL getUrl();
	
	/**
	 * Liefert die Header die beim Empfang der Daten empfangen wurden
	 * @return
	 */
	public Map getHeaders();
	
	/**
	 * Erzeugt ein Lucene Dokument,dass dem Index hinzugefuegt werden kann.
	 * @return Returns an org.apache.lucene.document.Document 
	 */
	public Document getdocument()  throws IOException, InterruptedException;
	
	/**
	 * Liefert die Kategorien als Array
	 **/
	public int[] getCategories();
}
