/*
 * Created on Mar 28, 2006
 */
package controller.teamfound;

import controller.Controller;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.SearchResponse;

public class TestController implements Controller {

	public AddPageResponse addToIndex(String url, int category[])
			throws DownloadFailedException, IndexAccessException {
		AddPageResponse r = new AddPageResponse("http://test.com");
		return r;
	}

	public SearchResponse search(String query, int offset, int category[])
			throws IndexAccessException {
		String[] keywords = {"hello"};
		SearchResponse r = new SearchResponse(keywords);
		return r;
	}

	public GetCategoriesResponse getCategories(int rootid) {
		GetCategoriesResponse r = new GetCategoriesResponse();
		r.addCategory("test", "beschreibung", new Integer(0), new Integer(0));
		r.addCategory("test2", "beschreibung2", new Integer(1), new Integer(0));
		r.addCategory("test3", "beschreibung3", new Integer(2), new Integer(1));
		
		return r;
	}

	public AddCategoriesResponse addCategory(String name, int parentCat,
			String description) {
		AddCategoriesResponse r = new AddCategoriesResponse(name, parentCat+1);
		return r;
	}
	//TODO Response -> einfach Kategorien nur halt als Liste nicht als Baum ...
	public void GetProjects()
	{
		return;
	}

}
