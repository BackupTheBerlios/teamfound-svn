/*
 * Created on Dec 6, 2005
 */
package controller.teamfound;

import index.NewIndexEntry;
import index.Indexer;
import index.teamfound.TeamFoundIndexer;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.Connection;

import java.util.Vector;
import java.util.List;

import controller.Download;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.Controller;
import controller.response.AddCategoriesResponse;
import controller.response.AddPageResponse;
import controller.response.GetCategoriesResponse;
import controller.response.GetProjectsResponse;
import controller.response.SearchResponse;

import config.Config;
import config.teamfound.TeamFoundConfig;

import db.teamfound.DBLayerHSQL;
import db.DBLayer;
import db.dbbeans.*;

import sync.ReadWriteSync;

import tools.Tuple;

import org.apache.lucene.document.Document;

public class TeamFoundController implements Controller {

	Download loader = new Download();
	public ReadWriteSync indexSync;
	public Config conf;
	
	public TeamFoundController() 
	{
		conf = new TeamFoundConfig();
		indexSync = new ReadWriteSync();
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
		 DBLayer db;
		 db = new DBLayerHSQL(conf);
		 Connection conn;
		
		//Indexer erstellen
		Indexer tfindexer = new TeamFoundIndexer(conf,indexSync);
		
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
					//Erfolgsmeldung liefer
					List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
					AddPageResponse resp = new AddPageResponse(vertup ,url);
					return(resp);
				}
				else
				{
					//1.Doc im index loeschen
					 Document doc = tfindexer.delDoc(url);
					 doc.removeField("cats");
					 
					 //2.Doc neu adden mit allen sich ergebenden Kats
					 String cats = new String();
					 Vector<Integer> allcats = new Vector<Integer>();
					 allcats.addAll(oldcats);
					 allcats.addAll(newcats);
					 java.util.Iterator allit = allcats.iterator();
					 Integer tmp;
					 while(allit.hasNext())
					 {
						 tmp = (Integer)allit.next();
						 cats = (cats + tmp.intValue() +" ");
					 }
					 doc.add(new org.apache.lucene.document.Field("cats",cats,true,true,true));
					 tfindexer.addUrl(doc);
					 
					//3.DB Aktualisieren
					java.util.Iterator newit = newcats.iterator();
					categoryBean catbean = new categoryBean();
					while(newit.hasNext())
					{
						catbean.setID((Integer)newit.next());
						db.addCatwithParentsToUrl(conn,urlbean,catbean);
					}
					
					//4.Erfolgsmeldung liefern
					List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
					AddPageResponse resp = new AddPageResponse(vertup ,url);
					return(resp);

				}

			}
		
			// 1. URL herunterladen
			NewIndexEntry entry = loader.downloadFile(adress, category);
		
			// 2. Indexieren
			tfindexer.addUrl(entry,adress);

			// 3. Datenbank aktualisieren
			urltabBean ub = new urltabBean(url);			
			categoryBean catbean = new categoryBean();
			catbean.setID(new Integer(category[0]));
			urltabBean ubean = db.addUrl(conn, ub, catbean);
			
			//so category[0] ist drinn nun fehlen noch die anderen
			for(int i = 1; i<category.length; i++)
			{
				catbean.setID(new Integer(category[i]));
				db.addCatwithParentsToUrl(conn,ubean,catbean);
			}
		
			// 4. fertig
			// hier muss eine addpage-response zurückgegeben werden
			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
			AddPageResponse resp = new AddPageResponse(vertup ,url);
			return(resp);
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : AddToIndex)"+e);
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
									 

		}
	}

	public SearchResponse search(String query, int category[]) throws IndexAccessException 
	{
		try
		{
	// 0. Datenbank nach Kategorienversionen durchsuchen
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
		
	// 1. Im Index Suchen
		
			Indexer tfindexer = new TeamFoundIndexer(conf,indexSync);
			//TODO -> hart den count auf 30 und den Offset auf 0 ??
			Vector<Document> docvec = tfindexer.query(query, category , 30, 0 ); 
	
		
	//2.Antwort bauen
			//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(vertup ,keywords);
			resp.addSearchResults(docvec);
		
			return (resp);
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : search)"+e);
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
									 

		}
	}

	
	public SearchResponse search(String query, int offset, int category[]) throws IndexAccessException 
	{
		try
		{
	// 0. Datenbank nach Kategorienversionen durchsuchen
		
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
		
	// 1. Im Index Suchen
		
			Indexer tfindexer = new TeamFoundIndexer(conf,indexSync);
			//TODO -> hart den count auf 30 ?
			Vector<Document> docvec = tfindexer.query(query, category , 30, offset ); 
	
		
	//2.Antwort bauen
		//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(vertup ,keywords);
			resp.addSearchResults(docvec);
		
			return (resp);
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : search)"+e);
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}

	}

	public GetCategoriesResponse getCategories(int rootid) {
		// TODO Auto-generated method stub
		return null;
	}

	public AddCategoriesResponse addCategory(String name, int parentCat, String description) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//TODO Response -> einfach Kategorien nur halt als Liste nicht als Baum ...
	public GetProjectsResponse getProjects()
	{
		return null;
	}
	

	

}
