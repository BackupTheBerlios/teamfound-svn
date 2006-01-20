/**
 * Created 10.12.2005
 * 
 * DBLayer abstrahiert Zugriff auf die Datenbank.
 * Schreibt und liest Beans in bzw. aus der Datenbank.
 * 
 * @author Martin Klink <moddin@hqpm.net>
 */

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import db.dbbeans.*;


public interface DBLayer
{
	/** 
	 * Verbindung zur Datenbank herstellen.
	 * Da vielleicht mehrere threads auf die Datenbank zugreifen,
	 * gebe ich direkt das Connection Objekt zurueck.
	 * Das bedeutet allerdings, dass der Thread der den DBLayer benutzen will
	 * immer sein Connection Object uebergeben muss...
	 * 
	 *
	 * @param user	Nutzername
	 * @param pass	Passwort
	 * @param server Servername
	 * @param database DBname
	 *
	 */	
	public Connection getConnection(String user, String pass, String server, String database)
		throws 	java.lang.ClassNotFoundException,
				java.lang.InstantiationException,
				java.lang.IllegalAccessException,
				java.sql.SQLException;

	/** 
	 * Shutdown der Datenbank!
	 * Nur im wenn DB eingebunden und nicht als Server arbeitet 
	 * wird hier wirklich etwas gemacht.Sollte beim beenden von 
	 * Teamfound aufgerufen werden damit alles sauber in der Datenbank landet.
	 * 
	 * Only if DB is in embedded use!
	 * 
	 * @param server Servername
	 * @param database DBname
	 *
	 */														                 
	public void shutdown(String server, String database)
		throws java.sql.SQLException,
				java.lang.ClassNotFoundException,
				java.lang.InstantiationException,
				java.lang.IllegalAccessException;
	
	 /**
	  * fuer select Anfragen
	  *nur als testfunktion zu verwenden..
	  */
	public void query(Connection conn, String expression) throws  SQLException;


	/**
	 * Einfuegen einer rootCategory ins Set ..
	 * Bean Objekt braucht nur Name und wird mit id etc. zurueckgeliefert
	 * 
	 */
	public categoryBean addRootCategory(Connection conn, categoryBean catbean) throws SQLException;

	/**
	 * Einfuegen einer Category ins Set ..
	 *
	 * Bean Objekt braucht nur Name und wird mit id etc. zurueckgeliefert
	 * Parent muss seine ID kennen.. da laut interface der Client alle IDs hat
	 * und mitsendet habe ich mich jetzt darauf verlassen dass id vorhanden
	 */
	public categoryBean addCategory(Connection conn, categoryBean catbean,categoryBean parent) throws SQLException;
	
	 /**
	  *Hinzufuegen einer Url (nur Url in der Bean benoetigt)
	  *
	  *
	  *Achtung: keine Ueberpruefung ob URL bereits existiert!
	  **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean) throws SQLException;

	 /**
	  *Hinzufuegen einer Url (nur Url in der Bean benoetigt)
	  *mit zugehoeriger Category
	  *
	  *Achtung: keine Ueberpruefung ob URL bereits existiert!
	  **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException;

/**
	 * Alle Eltern zur Category finden 
	 * in der CategoryBean wird nur die ID benoetigt 
	 */ 
	public java.util.Vector<categoryBean> findAllParents(Connection conn, categoryBean catbean) throws SQLException;


	
	/**
	 * Category ahand bezeichnung suchen ..
	 *
	 */
	public categoryBean getCategoryByName(Connection conn, String catname) throws SQLException;

	/**
	 * nach Url suchen
	 */ 
	public urltabBean getUrl(Connection conn, String url) throws SQLException;

	/**
	 * CategoryBaum auslesen ..
	 * TODO RueckgabeWert
	 */
	public void getCategoryTree(Connection conn) throws SQLException;

//TODO Was man noch so braucht


	
}
