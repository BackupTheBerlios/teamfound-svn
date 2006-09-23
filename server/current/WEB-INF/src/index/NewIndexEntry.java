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
 *
 */
public interface NewIndexEntry {
	
	
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
	
}
