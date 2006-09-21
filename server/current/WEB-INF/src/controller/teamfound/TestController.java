/*
 * Created on Mar 28, 2006
 */
package controller.teamfound;

import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.DBAccessException;
import controller.ServerInitFailedException;

import controller.Controller;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.GetProjectsResponse;
import controller.response.SearchResponse;
import controller.response.NewUserResponse;
import controller.response.LoginResponse;
import controller.SessionData;

import java.util.Date;

public class TestController implements Controller {
	public AddPageResponse addToIndex(String url, int category[], SessionData tfsession)
			throws DownloadFailedException, IndexAccessException {
		AddPageResponse r = new AddPageResponse("http://test.com");
		return r;
	}

	public SearchResponse search(String query, int offset, int category[],SessionData sessionkey)
			throws IndexAccessException {
		String[] keywords = {"hello"};
		SearchResponse r = new SearchResponse(keywords);
		return r;
	}
	public GetCategoriesResponse getCategories(int rootid,SessionData tfsession) {
		GetCategoriesResponse r = new GetCategoriesResponse();
		r.addCategory("test", "beschreibung", new Integer(0), new Integer(0));
		r.addCategory("test2", "beschreibung2", new Integer(1), new Integer(0));
		r.addCategory("test3", "beschreibung3", new Integer(2), new Integer(1));
		
		return r;
	}

	public AddCategoriesResponse addCategory(String name, int parentCat,
			String description, SessionData tfsession) {
		AddCategoriesResponse r = new AddCategoriesResponse( name, parentCat+1);
		return r;
	}

	public GetProjectsResponse getProjects(SessionData session) {
		// TODO Auto-generated method stub
		return null;		
	}
    public boolean initServer()
	{
		return true;
	}
	public NewUserResponse newUser(String user, String pass) throws DBAccessException, ServerInitFailedException
	{
		return null;
	}

	public LoginResponse rejectUser(String user) throws DBAccessException, ServerInitFailedException
	{
		return null;
	}
	public boolean checkUser(String user, String pass) throws DBAccessException, ServerInitFailedException
	{
		return true;
	}

	public LoginResponse loginUser(String user, String pass, String sessionkey,Date last) throws DBAccessException, ServerInitFailedException
	{
		return null;
	}



}
