/*
 * Created on Dec 6, 2005
 */
package index;

import java.io.File;
import java.util.Map;

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
	 * @return
	 */
	public File getFile();
	
	/**
	 * Liefert die URL für diese Inhalte
	 * @return
	 */
	public String getUrl();
	
	/**
	 * Liefert die Header die beim Empfang der Daten empfangen wurden
	 * @return
	 */
	public Map getHeaders();
	
	/**
	 * Erzeugt ein Lucene Dokument,dass dem Index hinzugefuegt werden kann.
	 * @return Returns an org.apache.lucene.document.Document 
	 */
	public org.apache.lucene.document.Document getdocument()  throws java.io.IOException, java.lang.InterruptedException;
}
