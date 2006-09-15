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
import java.util.Iterator;
import java.util.HashSet;
import java.util.Date;

import controller.Download;
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
	
	public TeamFoundController(Config c) 
	{
		conf = c;
		indexSync = new ReadWriteSync();
	}
	
	/**
	 * Eine URL zum Index hinzufügen
	 * 
	 * @param url die hinzuzufuegende Url
	 * @param category[] die Kategorien in die die URL gehoert (eigentlich mindestens die root Kategorie des Projekts ... normalerweise 0
	 * 
	 */
	public AddPageResponse addToIndex(String url, int category[]) throws DownloadFailedException, IndexAccessException, ServerInitFailedException {
	
		//TODO dies muss noch besser werden
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}

		URL adress = null;
		try {
			adress = new URL(url);
			/*System.out.println(adress.toString());
			System.out.println(adress.toExternalForm());
			System.out.println(adress.hashCode());
			System.out.println(adress.getPort());
			System.out.println(adress.getPath());
			System.out.println(adress.getHost());
			System.out.println(adress.getFile());
			System.out.println(adress.getProtocol());
			System.out.println(adress.getRef());
			System.out.println(adress.getQuery());
			System.out.println(adress.getUserInfo());
			//System.out.println(adress.getContent());
			System.out.println(adress.getAuthority());*/
			
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
			urltabBean urlbean = db.getUrl(conn,adress.toString());
			if(urlbean != null)
			{
				System.out.println("habe Url schon indiziert!");
				//Kategorien vergleichen falls weche Fehlen mit in den Vector aufnehmen
				Vector<Integer> oldcats = db.getCatsOfUrl(conn,urlbean.getID());
				Vector<Integer> newcats = new Vector<Integer>();	
				Vector<Integer> newcatstoadd = getAllPar(conn,db,category);
					 
				
				
				boolean updateurl = false;
				
				Iterator toadd = newcatstoadd.iterator();
				Integer tmpint;
				while(toadd.hasNext())
				{
					tmpint = (Integer)toadd.next();
					if( !oldcats.contains(tmpint))
					{
						updateurl=true;
						newcats.add(tmpint);
					}
				}

				if(!updateurl)
				{
					//Erfolgsmeldung liefer
					System.out.println("Nichts an der URL hat sich geaendert!");
					List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
					AddPageResponse resp = new AddPageResponse(vertup ,adress.toString());
					return(resp);
				}
				else
				{

					System.out.println("Neue Category zu der URL hinzutun");
					//1.Doc im index loeschen
					 Document doc = tfindexer.delDoc(adress.toString());
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
					
					//4.Erfolgsmeldung liefern (liste von (projectid, katbaumversion)
					List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
					AddPageResponse resp = new AddPageResponse(vertup ,adress.toString());
					return(resp);

				}

			}
	
			System.out.println("habe Url noch nicht indiziert!");
			// 1. URL herunterladen
			System.out.println("1. Url herunterladen!");
			
			Vector<Integer>cats = getAllPar(conn,db,category);	
			
			int[] categ = new int[cats.size()];
			Iterator intit = cats.iterator();
			int i = 0;
			while(intit.hasNext())
			{
				categ[i]= ((Integer)intit.next()).intValue();
				i=i+1;
			}
			
			NewIndexEntry entry = loader.downloadFile(adress, categ);
		
			// 2. Indexieren
			System.out.println("2. Url in den Index!");
			tfindexer.addUrl(entry,adress);

			// 3. Datenbank aktualisieren

			System.out.println("3. Url in die DB!");
			urltabBean ub = new urltabBean(adress.toString());			
			categoryBean catbean = new categoryBean();
			catbean.setID(new Integer(category[0]));
			urltabBean ubean = db.addUrl(conn, ub, catbean);
			
			//so category[0] ist drinn nun fehlen noch die anderen
			for(int h = 1; h<category.length; h++)
			{
				catbean.setID(new Integer(category[h]));
				db.addCatwithParentsToUrl(conn,ubean,catbean);
			}
		
			// 4. fertig
			// hier muss eine addpage-response zurückgegeben werden
			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
			AddPageResponse resp = new AddPageResponse(vertup ,adress.toString());
			
			conn.close();
			db.shutdown("anyserver","tfdb");
			return(resp);
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : AddToIndex"+e);
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
									 

		}
	}

	//Funktion die zu einem Array mit CatId eins baut mit allen ElternCats
	private Vector<Integer> getAllPar(Connection conn, DBLayer db, int[] categorys)throws java.sql.SQLException
	{
			categoryBean cb = new categoryBean();
			Vector<Integer> cats = new Vector<Integer>(); 
			
			for(int i=0; i<categorys.length;i++)
			{
				cb.setID(new Integer(categorys[i]));
				cats.add(new Integer(categorys[i]));
				Vector<categoryBean> allids = db.findAllParents(conn, cb); 
				Iterator allidit = allids.iterator();
			
				categoryBean tmpcb = new categoryBean();
				while(allidit.hasNext())
				{
					tmpcb = (categoryBean)allidit.next();
					
					if( !cats.contains(tmpcb.getID()) )
					{
						cats.add(tmpcb.getID());				
					}
					
				}
			}

			return(cats);
	}

	public SearchResponse search(String query, int category[]) throws IndexAccessException, ServerInitFailedException

	{
//TODO dies muss noch besser werden
		
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
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
			Vector<Document> docvec = tfindexer.query(query, category , 50, 0 ); 
	
		
	//2.Antwort bauen
			//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(vertup ,keywords);
			resp.addSearchResults(docvec);
			
			conn.close();	
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

	
	public SearchResponse search(String query, int offset, int category[]) throws IndexAccessException, ServerInitFailedException

	{
//TODO dies muss noch besser werden
		
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
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
			Vector<Document> docvec = tfindexer.query(query, category , 50, offset ); 
	
		
	//2.Antwort bauen
		//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(vertup ,keywords);
			resp.addSearchResults(docvec);
		
			conn.close();
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

	public GetCategoriesResponse getCategories(int rootid) throws ServerInitFailedException 
	{
	//TODO dies muss noch besser werden
			
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		try
		{
		//0. DatenBAnk verbindung		
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

		//1. Versionen der KategorieBaeume aus db
			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);

		//3. response fuellen
			GetCategoriesResponse resp = new GetCategoriesResponse(vertup);
			
			categoryBean rootbean = db.getCatByID(conn,rootid);
			Vector<categoryBean> childvec = db.getAllChildCategorys(conn, rootbean);
		
			//nur zum testen
			System.out.println("Rootbean:");
			rootbean.printAll();
			
			resp.addRoot(
					rootbean.getCategory(),
					rootbean.getBeschreibung(),
					rootbean.getID(),
					rootbean.getID());
					//null);
				
			Iterator it = childvec.iterator();
			while(it.hasNext())
			{
				categoryBean catbean = (categoryBean)it.next();
				categoryBean parent = db.findParent(conn, catbean);
			
				//nur zum testen
				System.out.println("\ncurrent parentbean:");
				parent.printAll();
				
				//nur zum testen
				System.out.println("\ncurrent child:");
				catbean.printAll();
				
				System.out.println("\n");

				
				resp.addCategory(
						catbean.getCategory(),
						catbean.getBeschreibung(),
						catbean.getID(),
						parent.getID());
						
			}
			conn.close();
			return(resp);
			
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : addCategory)"+e);
			return null;
		}
		
	}

	public AddCategoriesResponse addCategory(String name, int parentCat, String description) throws ServerInitFailedException 
	{
		
		//TODO dies muss noch besser werden
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		
		try
		{
		//1. Versionen der KategorieBaeume aus db
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);

		//2. categorybeans erstellen und ind db adden
			categoryBean newcat = new categoryBean();
			newcat.setCategory(name);
			newcat.setBeschreibung(description);
			categoryBean parentcat = new categoryBean();
			parentcat.setID(parentCat);
			
			newcat = db.addCategory(conn, newcat, parentcat);
		
		//3.response liefern		
			AddCategoriesResponse resp = new AddCategoriesResponse(
					vertup,
					newcat.getCategory(),
					newcat.getID());
			
			conn.close();
			return(resp);

		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : addCategory)"+e);
			return null;
		}
			
	}
	
	public GetProjectsResponse getProjects() throws ServerInitFailedException
	{

//TODO dies muss noch besser werden
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}

		try
		{
		//1. Versionen der KategorieBaeume und alle RootCats() aus der Datenbank holen
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
			Vector<categoryBean> catvec = db.getAllRootCats(conn);
		//2. mit den Infos die Response fuellen
			GetProjectsResponse resp = new GetProjectsResponse(vertup);
			Iterator it = catvec.iterator();
			while(it.hasNext())
			{
				categoryBean cat = (categoryBean)it.next();
				resp.addProject(cat.getCategory(), cat.getBeschreibung(),cat.getID());
						
			}
			conn.close();
			return(resp);
			
		
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : getProjects)"+e);
			return null;
		}
		
	}
	/**
	 * Diese Funktion liest die Konfiguration und initialisiert mit den
	 * Angaben die Datenbank und den Index.
	 *
	 * @return true->initialized(entweder war schon oder ist es nun) false->heisst irgentwas funzt nicht richtig im Server
	 */
	public boolean initServer()
	{
		try
		{
		// Da noch keine Admin schnittstelle wird diese Funktion einfach aufgerufen
		// prueft ob was existiert und wenn nicht legt es die noetigen sachen an ...
		//TODO Admin install interface und diese ueberfluessige Ueberpruefung raus!
		
		//0. kucken ob schon was existiert
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			if(db.initialized(conn))
				return true;

			
		//1. Konfiguration auslesen 
			String tfpath = conf.getConfValue("tfpath");
			String project = conf.getConfValue("project");
			String description = conf.getConfValue("description");
			String adminname = conf.getConfValue("name");
			String adminpass = conf.getConfValue("pass");
			
		//2. Index anlegen
			Indexer tfindexer = new TeamFoundIndexer(conf,indexSync);
			tfindexer.createIndex(tfpath);
			
		//3. DB initialiesieren
			categoryBean catbean = new categoryBean(project);
			catbean.setBeschreibung(description);
			catbean = db.addRootCategory(conn, catbean);
			
			tfuserBean admin = new tfuserBean();
			admin.setServeradmin(new Boolean(true));
			admin.setUsername(adminname);
			admin.setPass(adminpass);
			admin = db.createNewUser(conn, admin);
			db.addUserToAdminsOfProject(conn, admin.getID(), catbean.getRootID());
			
			projectdataBean pdata = new projectdataBean(catbean.getRootID(),
														new Boolean(false),
														new Boolean(false),
														new Boolean(false),
														new Boolean(true),
														new Boolean(true),
														new Boolean(true),
														new Boolean(false),
														new Boolean(false),
														new Boolean(false),
														new Boolean(false));
			db.setProjectdata(conn, pdata);

			//damit sich niemand als Gast registrieren kann
			tfuserBean guest = new tfuserBean();
			guest.setServeradmin(new Boolean(false));
			guest.setUsername("guest");
			guest.setPass("none");
			guest = db.createNewUser(conn, guest);

			conn.close();
			return(true);
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : initServer"+e);
			return false;
		}
		
	}

	/*------------------------------------------
	 *Neu Milestone 3
	 *----------------------------------------*/
	
	/**
	 * Eine Kategoie umbennen oder iher Description veraendern
	 * 
	 * @param catid id der Kat
	 * @param catname neuer Name, wenn catname = null oder leer, dann gleichlassen
	 * @param description neue Desc, wenn description = null dann gleichlassen, wenn string der laenge 0 dann loeschen
	 * 
	 */
	public void editCategory(int catid, String catname, String description) throws DBAccessException, ServerInitFailedException
	{
	//TODO dies muss noch besser werden
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		try
		{
		//0. DatenBAnk verbindung		
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

		// TODO ueberpruefen ob clients Version vom Categorytree akatuell
		// Rechte ueberpruefen?
			
		//1. Datenbank eintraege zur Kategorie aendern
			// erstmal alte zustand auslesen			
			categoryBean catbean = db.getCatByID(conn,catid);
			if((catname != null)&&(catname.length() > 0)) 
			{
				catbean.setCategory(catname);
			}
			if(description != null)
			{
				catbean.setBeschreibung(description);
			}
		
		//2. Versionsnummer erhoehen
			
		//3. response fuellen
			
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : addCategory)"+e);
		}
		
	}
	
	
	/**
	 * Einen neuen User anlegen
	 *
	 * @param name Username
	 * @param pass passwort
	 */
	public NewUserResponse newUser(String user, String pass) throws DBAccessException, ServerInitFailedException
	{	
	
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		
		try
		{
		//1. Establish connection
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);

		//2. user in db adden
			

			tfuserBean newuser = new tfuserBean();
			newuser.setUsername(user);
			newuser.setPass(pass);

			//Username zu kurz ?
			if(user.length() < 4)
			{
				NewUserResponse re = new NewUserResponse(vertup, newuser.getUsername());
				//7 ist ReturnCode fuer Username zu kurz
				re.tfReturnValue(new Integer(7));
				return(re);

			}
			
			//Username schon vergeben ?
			if(db.getUserByName(conn,newuser.getUsername()) != null)
			{
				NewUserResponse re = new NewUserResponse(vertup, newuser.getUsername());
				//6 ist ReturnCode fuer User existiert (siehe spezifikation
				re.tfReturnValue(new Integer(6));
				return(re);
			}
		
			newuser = db.createNewUser(conn, newuser);

		//3.response liefern		
			NewUserResponse	resp = new NewUserResponse(
					vertup,
					newuser.getUsername());
			
			conn.close();
			return(resp);

		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : newuser)"+e);
			DBAccessException dbe = new DBAccessException("NewUser SQLException");
			dbe.initCause(dbe);
			throw(dbe);

		}
	}

	/**
	 * Ueberpruefen ob User existiert und passwort stimmt
	 *
	 * @param name Username
	 * @param pass passwort
	 * @return boolean
	 */
	public boolean checkUser(String user, String pass) throws DBAccessException, ServerInitFailedException
	{	
	
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		
		try
		{
		//1. Establish connection
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");


		//2. in DB nachschauen
			tfuserBean tf = db.getUserByName(conn,user);
			if(tf != null)
			{
				if(pass == tf.getPass())
				{
					return(true);
				}
			}
			
			return false;
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : checkuser)"+e);
			DBAccessException dbe = new DBAccessException("checkUser SQLException");
			dbe.initCause(dbe);
			throw(dbe);

		}

	}

	/**
	 * Antwort erstellen die Anmeldewunsch des Users zurueckweist!
	 *
	 * @param name Username
	 * @return LoginResponse
	 */
	public LoginResponse rejectUser(String user) throws DBAccessException, ServerInitFailedException
	{	
	
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		
		try
		{
		//1. Establish connection
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
			conn.close();
		//2. antwort erstellen

			LoginResponse resp = new LoginResponse(
					vertup,
					user);
			resp.tfReturnValue(new Integer(8));	
			return(resp);

		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : rejectUser)"+e);
			DBAccessException dbe = new DBAccessException("rejectUser SQLException");
			dbe.initCause(dbe);
			throw(dbe);

		}
	}

	/**
	 * User einloggen
	 *
	 * @param String user
	 * @param String pass 
	 * @param String sessionkey
	 * @param String last (time when accessed)
	 * @return LoginResponse
	 */
	public LoginResponse loginUser(String user, String pass, String sessionkey,Date last) throws DBAccessException, ServerInitFailedException
	{	
	
		if(!initServer())
		{
			ServerInitFailedException e = new ServerInitFailedException("Reading Config, Creating Index or initialize DB failed!");
			e.initCause(e);
			throw(e);
		}
		
		try
		{
		//1. Establish connection
			DBLayer db;
			db = new DBLayerHSQL(conf);
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			List<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);
			conn.close();

		//2.Session info speichern
			db.newSession(conn, user, pass, sessionkey, last);

		//3. antwort erstellen

			LoginResponse resp = new LoginResponse(
					vertup,
					user);
			return(resp);

		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(dbe);
			throw(dbe);

		}
	}

}
