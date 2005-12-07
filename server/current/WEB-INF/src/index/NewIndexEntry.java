/*
 * Created on Dec 6, 2005
 */
package index;

import java.io.File;
import java.util.Map;

/**
 * Ein neuer Index-Eintrag muss die Datei in welcher der Inhalt, die url von welcher die Datei stammt sowie eine Hashmap mit http-headern zur Verf�gung stellen
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */
public interface NewIndexEntry {
	/**
	 * Liefert die tempor�re Datei
	 * @return
	 */
	public File getFile();
	
	/**
	 * Liefert die URL f�r diese Inhalte
	 * @return
	 */
	public String getUrl();
	
	/**
	 * Liefert die Header die beim Empfang der Daten empfangen wurden
	 * @return
	 */
	public Map getHeaders();
}
