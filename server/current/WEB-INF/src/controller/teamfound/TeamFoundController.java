/*
 * Created on Dec 6, 2005
 */
package controller.teamfound;

import index.NewIndexEntry;

import java.net.MalformedURLException;
import java.net.URL;

import controller.Download;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.Controller;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.SearchResponse;


public class TeamFoundController implements Controller {

	Download loader = new Download();
	
	public TeamFoundController() 
	{
	}
	
	public AddPageResponse addToIndex(String url) throws DownloadFailedException, IndexAccessException {
		URL adress = null;
		try {
			adress = new URL(url);
		} catch (MalformedURLException e) {
			DownloadFailedException e2 = new DownloadFailedException("nested MalformedURLException");
			e2.initCause(e);
		}
		
		// 0. Datenbank auf Existenz der URL checken
		
		// 1. URL herunterladen
		NewIndexEntry entry = loader.downloadFile(adress);
		
		// 2. Indexieren 

		// 3. Datenbank aktualisieren
		
		// 4. fertig
		
		// hier muss eine addpage-response zurückgegeben werden
		return null;
	}

	public SearchResponse search(String query) throws IndexAccessException {
		// 0. Datenbank nach Kategorien durchsuchen
		
		// 1. Im Index Suchen
		
		// 2. Fertig
		return null;
	}

	
	public SearchResponse search(String query, int offset) throws IndexAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public GetCategoriesResponse getCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	public AddCategoriesResponse addCategory(String name, int parentCat, String description) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
