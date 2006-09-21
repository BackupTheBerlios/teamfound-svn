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
import java.util.HashSet;
import java.util.Date;
import java.util.Map;

import db.dbbeans.*;
import tools.Tuple;

import controller.SessionData;

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
	 * gibt true zurueck falls die DB schon existiert
	 */
	public boolean initialized(Connection conn)throws SQLException;
	
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
	* not implemented
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	//public void deleteCategory(Connection conn, categoryBean catbean) throws SQLException;

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
	 * 
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
	 *	direkten Parent zur Category finden 
	 * 	in der CategoryBean wird nur die ID benoetigt 
	 */ 
	public categoryBean findParent(Connection conn, categoryBean catbean) throws SQLException;



	
	/**
	 * Hinzufuegen einer Category zu einer Url
	 * genau diese Category wird der URL zugeordnet(keine zuordnung der Parentkategorien)
	 * 
	 * Achtung: addCatwithParentsToUrl verwenden ... Deprecated! 
	 * 
	 */ 
	public void addCatToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException;

	/**
	 * Hinzufuegen einer Url zu einer Category
	 * Es werden auch alle ElternCategorien mit zugeordnet
	 *
	 * Die IDs von Category und Url reichen aus ...
	 * Achtung : addUrl zum anlegen einer URL benutzen
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
	 * nach Kategorien der Url suchen
	 * @return ids der kategorien
	 * @param id der Url
	 */ 
	public Vector<Integer> getCatsOfUrl(Connection conn, Integer urlid) throws SQLException;
	
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
	 * liefert alle Kinder der Kategorie
	 * bean muss left und right enthalten
	 * 
	 */
	public Vector<categoryBean> getAllChildCategorys(Connection conn, categoryBean rootbean)throws SQLException;
	
	/**
	 * liefert ganze categoryBean zu der Id
	 *
	 */
	public categoryBean getCatByID(Connection conn, int id) throws SQLException;

	/**
	 * Ersetzt Name und Beschreibung der Category
	 */
	public void updateCat(Connection conn, categoryBean catbean) throws SQLException;


	/**
	 * Liefert die Versionsnummer fuer den Baum der zu diesem RootKnoten gehoert.
	 * 
	 * Achtung geht nur mit wirklicher RootKategorie
	 */
	public Integer getVersionNumber(Connection conn,categoryBean rootbean) throws SQLException;
	/**
	 * Liefert alle Tuple <Integer,Integer> 
	 * der erste int ist die ID der RootCAT der Zweite ist die Version des Baumes
	 * 
	 *	@deprecated as of release 0.3
	 */
	@Deprecated public HashSet<Tuple<Integer,Integer>> getAllVersions(Connection conn) throws SQLException;

	/**
	 * Liefert Map <Integer,Integer> 
	 * der erste int ist die ID der RootCAT der Zweite ist die Version des Baumes
	 * Also alle Versionsnummer der Projecte mit ProjektID
	 */
	public Map<Integer,Integer> getAllVersion(Connection conn) throws SQLException;

	/**
	 * Liest alle WurzelKategorien aus.
	 * (ob diese nun als Projekte oder sonstewas indentifiziert werden)
	 */
	public Vector<categoryBean> getAllRootCats(Connection conn) throws SQLException;

	//////////////////////////////
	//
	//        N E U    MIlestone 3
	//
	//////////////////////////////
	
	/**
	 * Liefert alle indizierten Dokumente, deren Indizierung
	 * laenger her ist als das uebergebene Datum
	 *
	 * @return Vector<urltabBean> die indizierten URLs
	 * @param Date Datum 
	 */ 
	public Vector<urltabBean> getOlderDocs(Connection conn, java.util.Date datum) throws SQLException;

	/**
	 * Setz das Datum eines Eintrages in der indexedUrls Tabelle auf
	 * das jetzige Serverdatum
	 * 
	 */ 
	public void refreshIndexDate(Connection conn, Integer id) throws SQLException;
	
	
	/**
	 * Adminprojekte des Users
	 * @return Liste der projectadminBeans des users
	 * @param userid des Users
	 */ 
	public Vector<projectadminBean> getAdminProjectsForUser(Connection conn, Integer userid) throws SQLException;

	/**
	 * User by ID
	 * @return tfuserBean des Users
	 * @param userid des Users
	 */ 
	public tfuserBean getUserByID(Connection conn, Integer userid) throws SQLException;
	
	/**
	 * User by name
	 * @return tfuserBean des Users
	 * @param String name  des Users
	 */ 
	public tfuserBean getUserByName(Connection conn, String name) throws SQLException;

	/**
	 * User by Sessionkey
	 * @return tfuserBean des Users
	 * @param sessionkey 
	 */ 
	public tfuserBean getUserBySessionkey(Connection conn, String sessionkey) throws SQLException;

	/**
	 * Aktualisiere lastaction (setzt lastaction auf jetzige Zeit/Datum
	 * @param sessionkey 
	 */ 
	public void updateLastActionForUserID(Connection conn, Integer userid) throws SQLException;

	/**
	 * beim anlegen neuer Session anlegen 
	 * Sessionkey, lastaccessed fuer einen user in die Datenbank
	 * 
	 */ 
	public void newSession(Connection conn,String user, String pass, String sessionkey, Date last) throws SQLException;

	/**
	 * Erzeuge einen neuen User
	 * @return tfuserBean mit neu vergebener ID aus Datenbank
	 * @param tfuserBean
	 */ 
	public tfuserBean createNewUser(Connection conn, tfuserBean tfuser) throws SQLException;

	/**
	 * Einen Nutzer dem Projekt zuordnen
	 * @param userid
	 * @param rootid 
	 *
	 * @return boolean true user geadded, false kein Eintrag hat stattgefunden
	 */ 
	public boolean addUserToProject(Connection conn, Integer userid, Integer rootid) throws SQLException;
	
	/**
	 *
	 * Gib einem Nutzer adminrechte fuer ein Projekt (vorausgesetzt er ist dem Project zugeordnet)
	 * Achtung der Nutzer muss bereits dem Project angehoeren!
	 * @param userid
	 * @param rootid
	 *
	 * @return boolean true user geadded, false kein Eintrag hat stattgefunden
	 */ 
	public boolean addUserToAdminsOfProject(Connection conn, Integer userid, Integer rootid) throws SQLException;

	/**
	 * Setzt die Rechte fuer ein Project neu
	 *  
	 * @param projectdataBean
	 */ 
	public void setProjectdata(Connection conn, projectdataBean projectdata) throws SQLException;

	/**
	 * Gibt die Rechte fuer ein Project zurueck
	 * @param rootid
	 * @return projectdataBean
	 */ 
	public projectdataBean getProjectdata(Connection conn, Integer rootid) throws SQLException;

	/**
	 * Sortiert alle Kategorien aus auf die kein ReadAccess besteht
	 * sessionkey = null falls keine session besteht
	 */ 
	public int[] checkCatReadAccess(Connection conn, int[]category, Integer userid) throws SQLException;
	
	/**
	 * Alle Rechte eines Users auslesen
	 * Achtung : Admin und User werden in der UserRightBean getrennt behandelt
	 * d.h. wenn man Admin ist, ist man nciht mehr als User gelistet
	 *
	 * @param userid
	 * @return
	 */
	public userRightBean getRights(Connection conn, Integer userid) throws SQLException;

	/**
	 * projectdata zu einer Kategorie holen 
	 * (nicht zu einem Project !)
	 * @param catid
	 * @return projectdataBean
	 */
	public projectdataBean getProjectDataToCat(Connection conn, Integer catid) throws SQLException;

	public java.util.concurrent.ConcurrentHashMap<Integer, projectdataBean> getAllProjectData(Connection conn) throws SQLException;

}
