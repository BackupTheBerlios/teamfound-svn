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
import java.sql.SQLException;
import java.util.Vector;

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
	  *gibt ergebnisse des select-statements auf der Konsole aus
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
	 * Bean Objekt braucht nur Name (und Beschreibung) Es wird mit id etc. zurueckgeliefert.
	 * Das ParentObject muss ausschlieslich seine ID kennen..
	 * (nicht vorher mit einer extra Anfrage fuellen,dass wird von dieser Funktion selber erledigt)
	 * da laut interface der Client alle IDs der Kathegorien hat
	 * und mitsendet ist das also kein Problem
	 */
	public categoryBean addCategory(Connection conn, categoryBean catbean,categoryBean parent) throws SQLException;
	
	/** 
	* Loescht ein Blatt aus dem Category-Baum (nested Set) 
	* Achtung keine Fehlermeldung falls es kein Blatt war und
	* in dem fall wird einfach gar nichts gemacht ....
	* TODO exception fuer den beschriebenen Fall
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public void deleteLeafCategory(Connection conn, categoryBean catbean) throws SQLException;
	
	/** 
	* Loescht einen TeilBaum aus dem Category-Baum (nested Set) 
	* TODO alle Zuordnungne zu Urls entfernen oder soll das uebergeordnet geregelt werden? 
	* Ist es ueberhaupt in unserem Prog erlaubt ?
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public void deletePartialTree(Connection conn, categoryBean catbean) throws SQLException;

	/** 
	* Loescht eine beliebigen Knoten aus dem Category-Baum (nested Set) 
	* TODO alle Zuordnungne zu Urls entfernen 
	* Ist es ueberhaupt in unserem Prog erlaubt ?
	* Methode bisher nicht implementiert ...!!!
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public void deleteCategory(Connection conn, categoryBean catbean) throws SQLException;

	/**
	 * Hinzufuegen einer Url(nur Url in der Bean benoetigt) 
	 * ohne zugehoerige Category
	 * liefert bean mit der ID und dem erzeugten Datum zurueck
	 * 
	 * Achtung: keine Ueberpruefung ob URL bereits existiert! 
	 * Wuerde also doppelte Eintraege generieren ...
	 *
	 * Bitte die Funktion mit Kathegorie verwenden und zumindestens die RootKathegorie
	 * mitangeben!
	 * DEPRECATED 
	 * 
	 * @param conn  Connection
	 * @param urlbean urltabBean
	 * 
	 **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean) throws SQLException;
	
	/**
	  *Hinzufuegen einer Url (nur Url in der Bean benoetigt)
	  *mit zugehoeriger Category(auch hier nur ID benoetigt)
	  *
	  *Die Funktion ueberprueft ob die Url existiert und 
	  *fuegt in dem Fall nur den Link auf die Kathegorie hinzu.
	  * 
	  *Es werden automatisch Links auf alle Elternkathegorien mit hinzugefuegt.
	  **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException;

	/**
	 * Alle Eltern zur Category finden 
	 * in der CategoryBean wird nur die ID benoetigt 
	 */ 
	public Vector<categoryBean> findAllParents(Connection conn, categoryBean catbean) throws SQLException;

	/**
	 * Hinzufuegen einer Category zu einer Url
	 * genau diese Category wird der URL zugeordnet(keine zuordnung der Parentkategorien)
	 * 
	 * Achtung: addCatwithParentsToUrl verwenden ... Deprecated! 
	 * 
	 */ 
	public void addCatToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException;

	/**
	 * Hinzufuegen einer Category zu einer Url
	 * Es werden auch alle ElternCategorien mit zugeordnet
	 *
	 * Die IDs von Category und Url reichen aus ...
	 */ 
	public void addCatwithParentsToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException;
	
	/**
	 * Category anhand bezeichnung suchen ..
	 * wir brauchen als Information in welchem Projekt nach der Kathegorie gesucht wird.
	 * (nur id muss in Bean stehen)
	 *
	 * wenn keine Kathegorie gefunden wird, ist return null
	 * 
	 * Es kann in mehreren Projekten auch gleichlautende Kathegorien geben.
	 * Wichtig:Gleichlautend bedeuted in so einem Fall nicht,
	 * dass es diesselbe Kathegorie ist!
	 *
	 */
	public categoryBean getCategoryByName(Connection conn, String catname, categoryBean rootbean) throws SQLException;


	/**
	 * nach Url suchen
	 */ 
	public urltabBean getUrl(Connection conn, String url) throws SQLException;
	
	/**
	 * nach Url suchen -> liefert gefuellte urltabBean
	 * Returnwert ist Null wenn die Url nicht in der Datenbank ist
	 * oder nicht dem Mituebergebenen Projekt zugeordnet ist.
	 * (die categoryBean kann z.b. der Root des Projektes sein um 
	 * rauszufinden ob die URL in dem Projekt ist. 
	 * Es wird nur die ID der Kathegorie benoetigt)
	 */ 
	public urltabBean getUrl(Connection conn, String url, categoryBean rootbean) throws SQLException;

	/**
	 * CategoryBaum auslesen ..
	 * Wir brauchen die ID der Rootbean, da wir fuer verschiedene Projekte
	 * verschiedene Baeume speichern koennen
	 * Zurueckgeliefert wird ein String,der die XML Darstellung 
	 * des trees beinhaltet so wie sie fuer die getCategory 
	 * anfrage des Clients benoetigt wird!
	 *
	 * Bsp. :
	 * <category>
	 * 	<name>name der 1. kategorie</name>
	 *	<description>laengere beschreibung</description>
	 *	<id>0</id>
	 *	<subcategories>
	 *		<category>
	 *		<name>unterkat 1.1</name>
	 *		<id>1</id>
	 *		<subcategories>
	 *		.
	 *		.
	 *		.
	 * 	</subcategories>
	 * </category>
	 */
	public String getCategoryTree(Connection conn,categoryBean rootbean) throws SQLException;


	/**
	 * Liefert die Versionsnummer fuer den Baum der zu diesem RootKnoten gehoert.
	 * 
	 */
	public Integer getVersionNumber(Connection conn,categoryBean rootbean) throws SQLException;

		
//TODO Was man noch so braucht


	
}
