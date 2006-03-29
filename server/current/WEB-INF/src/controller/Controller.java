/*
 * Created on Dec 6, 2005
 */
package controller;

import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.GetProjectsResponse;
import controller.response.SearchResponse;


/**
 * Interface für einen Controller nach Milestone2-Spezifikation
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 */
public interface Controller {

	
	/**
	 * Eine URL zum Index hinzufügen
	 *
	 * @param url die hinzuzufuegende Url
	 * @param category[] die Kategorien in die die URL gehoert (eigentlich mindestens die root Kategorie des Projekts ... normalerweise 0
	 */
	public AddPageResponse addToIndex(String url, int category[]) throws DownloadFailedException, IndexAccessException;
	
	/**
	 * Nach einer Query im Index suchen
	 * 
	 * @todo Das Ergebnis müsste genauer spezifiziert werden, siehe Interface SearchResult
	 * @see controller.SearchResponse
	 */
	public SearchResponse search(String query, int offset, int category[]) throws IndexAccessException;

	public GetCategoriesResponse getCategories(int rootid);
	
	public AddCategoriesResponse addCategory(String name, int parentCat, String description);

	//TODO Response -> einfach Kategorien nur halt als Liste nicht als Baum ...
	public GetProjectsResponse getProjects();
	
	// evtl.später:
	
	/*
	public DeleteCategoryResponse deleteCategory(int nr);
	
	public ConfigResponse getConfiguration();
	
	*/
}
