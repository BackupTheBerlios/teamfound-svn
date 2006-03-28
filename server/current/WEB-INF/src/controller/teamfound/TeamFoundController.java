/*
 * Created on Dec 6, 2005
 */
package controller.teamfound;

import index.NewIndexEntry;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.Connection;

import java.util.Vector;

import controller.Download;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.Controller;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.SearchResponse;

import config.Config;
import config.teamfound.TeamFoundConfig;

import db.teamfound.DBLayerHSQL;
import db.DBLayer;
import db.dbbeans.*;




public class TeamFoundController implements Controller {

	Download loader = new Download();
	
	public TeamFoundController() 
	{
	}
	
	/**
	 * Eine URL zum Index hinzufügen
	 * 
	 * @param url die hinzuzufuegende Url
	 * @param category[] die Kategorien in die die URL gehoert (eigentlich mindestens die root Kategorie des Projekts ... normalerweise 0
	 * 
	 */
	public AddPageResponse addToIndex(String url, int category[]) throws DownloadFailedException, IndexAccessException {
		URL adress = null;
		try {
			adress = new URL(url);
		} catch (MalformedURLException e) {
			DownloadFailedException e2 = new DownloadFailedException("nested MalformedURLException");
			e2.initCause(e);
		}
		
		//DBVerbindung fuer die Funktion erstellen
		 Config conf = new TeamFoundConfig();
		 DBLayer db;
		 db = new DBLayerHSQL(conf);
		 Connection conn;
		 
		try
		{
		 	conn = db.getConnection("tf","tfpass","anyserver","tfdb");
		
			// 0. Datenbank auf Existenz der URL checken
			urltabBean urlbean = db.getUrl(conn,url);
			if(urlbean != null)
			{
				//Kategorien vergleichen falls weche Fehlen mit in den Vector aufnehmen
				Vector<Integer> oldcats = db.getCatsOfUrl(conn,urlbean.getID());
				Vector<Integer> newcats = new Vector<Integer>();
				boolean updateurl = false;
				for(int i = 0; i<category.length; i++)
				{
					if( !oldcats.contains(new Integer(category[i])) )
					{
						updateurl=true;
						newcats.add(new Integer(category[i]));
					}
				}
				if(!updateurl)
				{
					//TODO
					//Erfolgsmeldung liefern
				}
				else
				{
					//TODO
					//1.Doc im index loeschen
					//2.Doc neu adden mit allen sich ergebenden Kats
					//3.DB Aktualisieren
					//4.Erfolgsmeldung liefern
				}

			}
		
			// 1. URL herunterladen
			NewIndexEntry entry = loader.downloadFile(adress);
		
			// 2. Indexieren 

			// 3. Datenbank aktualisieren
		
			// 4. fertig
		
			// hier muss eine addpage-response zurückgegeben werden
		}
		catch(Exception e)
		{
			//TODO
			System.out.println("Fuck off!");
		}
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
