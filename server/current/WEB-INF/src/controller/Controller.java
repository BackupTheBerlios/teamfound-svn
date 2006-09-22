/*
 * Created on Dec 6, 2005
 */
package controller;

import index.NewIndexEntry;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.DBAccessException;
import controller.response.*;
import controller.response.EditPermissionsResponse;
import controller.SessionData;

import java.util.Date;

/**
 * Interface für einen Controller nach Milestone3-Spezifikation
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 * @author Martin Klink <moddin@hqpm.net>
 */
public interface Controller {

	
	/**
	 * Eine URL zum Index hinzufügen
	 *
	 * @param url die hinzuzufuegende Url
	 * @param category[] die Kategorien in die die URL gehoert (eigentlich mindestens die root Kategorie des Projekts ... normalerweise 0
	 */
	public AddPageResponse addToIndex(String url, int category[], SessionData tfsession) throws DownloadFailedException, IndexAccessException ;
	
	/**
	 * Nach einer Query im Index suchen
	 * 
	 * @todo Das Ergebnis müsste genauer spezifiziert werden, siehe Interface SearchResult
	 * @see controller.SearchResponse
	 */
	public SearchResponse search(String query, int offset, int category[],SessionData tfsession) throws IndexAccessException, DBAccessException;

	public GetCategoriesResponse getCategories(int rootid, SessionData tfsession) throws DBAccessException;
	
	public AddCategoriesResponse addCategory(String name, int parentCat, String description, SessionData tfsession) throws DBAccessException;

	//TODO Response -> einfach Kategorien nur halt als Liste nicht als Baum ...
	public GetProjectsResponse getProjects(SessionData session)throws DBAccessException;
	
	// evtl.später:
	
	/*
	public DeleteCategoryResponse deleteCategory(int nr);
	
	public ConfigResponse getConfiguration();
	
	*/
	
	/**
	 * Ein Dokument im Index und der Datenbank updaten
	 * 
	 * Diese Methode ändert nur den Inhalt, nicht die Kategorien, 
	 * es wird vorallem vom UpdateThread benutzt
	 */
	public int updateDocument(NewIndexEntry nd);
		

	/**
	 * Ein Dokument im Index und der Datenbak updaten
	 */
	public int updateDocument(NewIndexEntry nd, int[] categories);

	
	/*------------------------------------------
	 *Neu Milestone 3
	 *----------------------------------------*/
	/**
	 * Einen neuen User anlegen
	 *
	 * @param name Username
	 * @param pass passwort
	 */
	public NewUserResponse newUser(String user, String pass) throws DBAccessException ;

	/**
	 * Ueberpruefen ob User existiert und passwort stimmt
	 *
	 * @param name Username
	 * @param pass passwort
	 * @return boolean
	 */
	public boolean checkUser(String user, String pass) throws DBAccessException ;
	
	/**
	 * Antwort erstellen die Anmeldewunsch des Users zurueckweist!
	 *
	 * @param name Username
	 * @return LoginResponse
	 */
	public LoginResponse rejectUser(String user) throws DBAccessException ;
	
	/**
	 * User einloggen
	 *
	 * @param String user
	 * @param String pass 
	 * @param String sessionkey
	 * @param String last (time when accessed)
	 * @return LoginResponse
	 */
	public LoginResponse loginUser(String user, String pass, String sessionkey,Date last) throws DBAccessException ;
	

	/* Rechte eines Projekts anpassen
	 */
	public EditPermissionsResponse editPermissions(Integer projectid, SessionData tfsession, Boolean _useruseradd, 
		Boolean _userurledit,
		Boolean _usercatedit,
		Boolean _useraddurl,
		Boolean _useraddcat,
		Boolean _guestread,
		Boolean _guesturledit,
		Boolean _guestcatedit,
		Boolean _guestaddurl,
		Boolean _guestaddcat ) throws IndexAccessException,  DBAccessException;

	public Response editCategory(Integer category, String catname, String description, SessionData session) throws DBAccessException;

	/**
	 * User einem Project zuordnen
	 *
	 * @param String user
	 * @param Integer projectid
	 * @return Response
	 */
	public Response addUserToProject(String user,Integer projectid, SessionData tfsession) throws DBAccessException; 
	
	/**
	 * User ProjektAdminRechte in einem Project geben
	 *
	 * @param String user
	 * @param Integer projectid
	 * @return Response
	 */
	public Response grantProjectAdmin(String user,Integer projectid, SessionData tfsession) throws DBAccessException; 
	
	/**
	 * User aus Projekt entfernen
	 *
	 * @param String user
	 * @param Integer projectid
	 * @return Response
	 */
	public Response removeUserFromProject(String user,Integer projectid, SessionData tfsession) throws DBAccessException;
	

	public Response getUsersOfProject(Integer projectid, SessionData tfsession) throws DBAccessException;
}
