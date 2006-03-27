/*
 * Created on Dec 6, 2005
 */
package controller;

import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.SearchResponse;


/**
 * Interface f�r einen Controller nach Milestone2-Spezifikation
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 */
public interface Controller {

	
	/**
	 * Eine URL zum Index hinzuf�gen
	 * 
	 * @todo Kategorien hinzuf�gen
	 */
	public AddPageResponse addToIndex(String url) throws DownloadFailedException, IndexAccessException;
	
	/**
	 * Nach einer Query im Index suchen
	 * 
	 * @todo Das Ergebnis m�sste genauer spezifiziert werden, siehe Interface SearchResult
	 * @see controller.SearchResponse
	 */
	public SearchResponse search(String query, int offset) throws IndexAccessException;

	public GetCategoriesResponse getCategories();
	
	public AddCategoriesResponse addCategory(String name, int parentCat, String description);
	
	
	// evtl.sp�ter:
	
	/*
	public DeleteCategoryResponse deleteCategory(int nr);
	
	public ConfigResponse getConfiguration();
	
	*/
}
