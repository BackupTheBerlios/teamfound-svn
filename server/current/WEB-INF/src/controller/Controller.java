/*
 * Created on Dec 6, 2005
 */
package controller;

import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.DBAccessException;
import controller.ServerInitFailedException;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.GetProjectsResponse;
import controller.response.SearchResponse;
import controller.response.NewUserResponse;


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
	public AddPageResponse addToIndex(String url, int category[]) throws DownloadFailedException, IndexAccessException, ServerInitFailedException;
	
	/**
	 * Nach einer Query im Index suchen
	 * 
	 * @todo Das Ergebnis müsste genauer spezifiziert werden, siehe Interface SearchResult
	 * @see controller.SearchResponse
	 */
	public SearchResponse search(String query, int offset, int category[]) throws IndexAccessException, ServerInitFailedException;

	public GetCategoriesResponse getCategories(int rootid) throws ServerInitFailedException;
	
	public AddCategoriesResponse addCategory(String name, int parentCat, String description) throws ServerInitFailedException;

	//TODO Response -> einfach Kategorien nur halt als Liste nicht als Baum ...
	public GetProjectsResponse getProjects()throws ServerInitFailedException;
	
	// evtl.später:
	
	/*
	public DeleteCategoryResponse deleteCategory(int nr);
	
	public ConfigResponse getConfiguration();
	
	*/
	/**
	 * Diese Funktion liest die Konfiguration und initialisiert mit den
	 * Angaben die Datenbank und den Index.
	 *
	 * @return true->initialized(entweder war schon oder ist es nun) false->heisst entweder Index oder Datenbank laesst sich nicht anlegen oder erreichen
	 */
	public boolean initServer();
		
	/*------------------------------------------
	 *Neu Milestone 3
	 *----------------------------------------*/
	/**
	 * Einen neuen User anlegen
	 *
	 * @param name Username
	 * @param pass passwort
	 */
	public NewUserResponse newUser(String user, String pass) throws DBAccessException, ServerInitFailedException;
	
	
}
