/*
 * Created on Dec 6, 2005
 */
package controller.teamfound;

import index.NewIndexEntry;
import index.Indexer;
import index.crawler.teamfound.TeamFoundCrawler;
import index.teamfound.TeamFoundIndexer;

import java.net.MalformedURLException;
import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

import config.teamfound.TeamFoundConfig;
import controller.Download;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.DBAccessException;
import controller.Controller;
import controller.response.*;
import controller.SessionData;
import controller.teamfound.checkAuthorisation;



import db.teamfound.DBLayerHSQL;
import db.DBLayer;
import db.dbbeans.*;

import sync.ReadWriteSync;

import tools.Tuple;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

public class TeamFoundController implements Controller {

	//Download loader = new Download();
	public static ReadWriteSync indexSync;
	private DBLayer db;
	protected Logger log;
	protected TeamFoundCrawler crawler;
	
	public TeamFoundController() 
	{
		indexSync = new ReadWriteSync();

		log = Logger.getLogger("tf-ctrl");
		// TODO maximum frame fetch depth könnte in die properties ausgelagert werden
		crawler = new TeamFoundCrawler(3);

		db = new DBLayerHSQL();
		
		if(initServer()) //TODO Logg falls nciht
		{	
			try{
				// 0. ProjectData auslesen
				Connection conn;
				conn = db.getConnection("tf","tfpass","anyserver","tfdb");
				SessionData.projectdata = db.getAllProjectData(conn);

			}catch(Exception e)
			{
				//Todo logg
				System.out.println(e);
			}
		}

	}


	/**
	 * Eine URL zum Index hinzufügen
	 * 
	 * @param url die hinzuzufuegende Url
	 * @param category[] die Kategorien in die die URL gehoert (eigentlich mindestens die root Kategorie des Projekts ... normalerweise 0
	 * 
	 */
	public AddPageResponse addToIndex(String url, int category[], SessionData tfsession) throws DownloadFailedException, IndexAccessException {
	

		URL adress = null;
		try {
			adress = new URL(url);
		
		} catch (MalformedURLException e) {
			DownloadFailedException e2 = new DownloadFailedException("nested MalformedURLException");
			e2.initCause(e);
		}
		
		AddPageResponse resp = new AddPageResponse(adress.toString());
		
		//Connection fuer DB
		Connection conn;
		
		//Indexer erstellen
		Indexer tfindexer = new TeamFoundIndexer(indexSync);
		
		try
		{
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
		
			//1. Authorisation checken
			for(int i=0; i<category.length; i++)
			{
				categoryBean cbean = db.getCatByID(conn,category[i]);
				if(!checkAuthorisation.checkAddPage(tfsession, cbean.getRootID()))
				{
					resp.tfReturnValue(9);
					return(resp);
				}
			}

			//zu den Kats noch alle  elternKats suchen
			Vector<Integer> newcatstoadd = getAllPar(conn,category);
			
			// 2. Datenbank auf Existenz der URL checken
			urltabBean urlbean = db.getUrl(conn,adress.toString());
			if(urlbean != null)
			{
				System.out.println("habe Url schon indiziert!");
				Vector<Integer> oldcats = db.getCatsOfUrl(conn,urlbean.getID());
				
				if(oldcats.containsAll(newcatstoadd))
				{
					//brauchen nichts erneueren
					System.out.println("Nichts an der URL hat sich geaendert!");
					return(resp);
				}
				else
				{
					//muessen nur Kategorien hinzufuegen
					System.out.println("Neue Categorys zu der URL hinzutun");
					HashSet<Integer> allcats = new HashSet<Integer>();
					allcats.addAll(oldcats);
					allcats.addAll(newcatstoadd);
					//3. Kategorien in Index
					tfindexer.updateCategory(adress.toString(),allcats);	
					
					//4.DB Aktualisieren
					for(int i=0;i<category.length;i++)
					{
						if(!oldcats.contains(new Integer(category[i])))
						{
							categoryBean catbean = new categoryBean();
							catbean.setID(new Integer(category[i]));
							db.addCatwithParentsToUrl(conn,urlbean,catbean);
						}
					}

					
					//5.Erfolgsmeldung liefern (liste von (projectid, katbaumversion)
					return(resp);

				}

			}
	
			/*Url muss heruntergeladen und neu Indiziert werden*/
			//System.out.println("habe Url noch nicht indiziert!");
			
			NewIndexEntry entry = crawler.fetch(adress); 
			//loader.downloadFile(adress, categ);
		
			// 2. Indexieren
			System.out.println("2. Url in den Index!");
			tfindexer.addUrl(entry,adress, newcatstoadd);

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
		
			// 4. fertig alles schliessen und response senden
			
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

	//Funktion die zu einem Array mit CatId eins baut mit den Ids und allen Ids der ElternCats aber keine Cat doppelt einfuegt
	private Vector<Integer> getAllPar(Connection conn,  int[] categorys)throws java.sql.SQLException
	{
			categoryBean cb = new categoryBean();
			Vector<Integer> cats = new Vector<Integer>(); 
			
			for(int i=0; i<categorys.length;i++)
			{
				cb.setID(new Integer(categorys[i]));
				cats.add(new Integer(categorys[i]));
				Vector<categoryBean> allids = db.findAllParents(conn, cb); 
				Iterator allidit = allids.iterator();
			
				while(allidit.hasNext())
				{

					categoryBean tmpcb = new categoryBean();
					tmpcb = (categoryBean)allidit.next();
					
					if( !cats.contains(tmpcb.getID()) )
					{
						cats.add(tmpcb.getID());				
					}
					
				}
			}

			return(cats);
	}

	/*
	 * @deprecated
	 */

	@Deprecated public SearchResponse search(String query, int category[]) throws IndexAccessException
	{
		
		try
		{
	// 0. Datenbank nach Kategorienversionen durchsuchen
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

	// 1. Im Index Suchen
			
			SearchResponse resp;
		
			Indexer tfindexer = new TeamFoundIndexer(indexSync);
			//TODO -> hart den count auf 30 und den Offset auf 0 ??
			//
			Vector<Document> docvec = tfindexer.query(query, category , 50, 0 ); 

			// Antwort bauen
			String[] keywords = new String[1];
			keywords[0] = query;
			resp = new SearchResponse(keywords);
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

	
	public SearchResponse search(String query, int offset, int category[], SessionData tfsession) throws IndexAccessException,  DBAccessException

	{
		
		try
		{
			// 0. Datenbank 
		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			SearchResponse resp;

			if( query != null)
			{
		

				// Basis-Antwort bauen
				//TODO keywords ?
				String[] keywords = new String[1];
				keywords[0] = query;
				resp = new SearchResponse(keywords);

				// 0.1. Userrechte ueberpruefen (seit Milestone 3)
				for( int i = 0; i < category.length; i++)
				{
					// root-cat der cat auslesen
					categoryBean cb = db.getCatByID(conn, category[i]);

					if( cb == null)
					{	// category not found
						resp.tfReturnValue(new Integer(5));
						return(resp);
					}
					if(!checkAuthorisation.checkSearch(tfsession,cb.getRootID()))
					{
						resp.tfReturnValue(new Integer(9));
						return(resp);
					}
				}
				
			
				// 1. Im Index Suchen
			
				Indexer tfindexer = new TeamFoundIndexer(indexSync);
				//TODO -> hart den count auf 30 ?
				Vector<Document> docvec = tfindexer.query(query, category , 50, offset ); 
		
			
				//2.Suchergebnisse in Antwort einbauen
				resp.addSearchResults(docvec);
			}
			else
			{
				// getall = yes
				// category[0] da nur fuer eine kat erlaubt
				Vector<String> v = db.getAllUrlsInCategory(conn, category[0]); 
				resp = new SearchResponse(null);
				resp.addSimpleSearchResults(v, category[0]);
			}
		
			conn.close();
			return (resp);
		}
		catch(SQLException e)
		{
			System.out.println("TeamFoundController : search)"+e);
            DBAccessException a = new DBAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : search)"+e);
			System.out.println("INDEX FEHLER");
			e.printStackTrace();
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}

	}

	public GetCategoriesResponse getCategories(int rootid, SessionData tfsession) throws DBAccessException 
	{
			
		GetCategoriesResponse resp = new GetCategoriesResponse();
		if(!checkAuthorisation.checkSearch(tfsession, rootid))
		{
			resp.tfReturnValue(9);
			return resp;
		}

		try
		{
			//1. DatenBAnk verbindung		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			//2. response fuellen
			categoryBean rootbean = db.getCatByID(conn,rootid);

			if( rootbean != null)
			{

				Vector<categoryBean> childvec = db.getAllChildCategorys(conn, rootbean);
				
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
					
					resp.addCategory(
							catbean.getCategory(),
							catbean.getBeschreibung(),
							catbean.getID(),
							parent.getID());
							
				}
			}
			conn.close();
			return(resp);
			
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : getCategories)"+e);
			e.printStackTrace();
			DBAccessException dba = new DBAccessException("TeamFoundController : addCategory" + e);
			dba.initCause(e);
			throw dba;
		}
		
	}
	/*
	 * Legt neue subKategorie zur uebergebenen ElternKategorie an.
	 * Wird -1 als Elternkateorie angegeben so wird ein neues Projekt angelegt
	 *
	 */
	public AddCategoriesResponse addCategory(String name, int parentCat, String description, SessionData tfsession) throws DBAccessException
	{
		AddCategoriesResponse resp;
		// initialisiert mit not authorized
		resp = new AddCategoriesResponse(
							"",
							new Integer(-1));
		resp.tfReturnValue(new Integer(9));

		try
		{
			//1.Connection zur DB
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			//2. categorybeans erstellen und ind db adden
			categoryBean newcat = new categoryBean();
			newcat.setCategory(name);
			newcat.setBeschreibung(description);

			if( parentCat == -1)
			{

				if( tfsession != SessionData.guest)
				{
					try
					{
						// neues Projekt anlegen
						conn.setSavepoint("newproject");
						newcat = db.addRootCategory(conn, newcat);
						db.addUserToProject(conn, tfsession.tfu.getID(), newcat.getID());
						db.addUserToAdminsOfProject(conn, tfsession.tfu.getID(), newcat.getID());
						// DataSession updaten
						tfsession.urb = db.getRights(conn, tfsession.tfu.getID());
						projectdataBean pdb = db.getProjectDataToCat(conn, newcat.getID());
						SessionData.projectdata.put(pdb.getRootID(),pdb);
						conn.commit();
						resp = new AddCategoriesResponse(
							newcat.getCategory(),
							newcat.getID());
					}
					catch( SQLException e)
					{
						conn.rollback();
						throw e;
					}
				}
				else
				{
					// not authorized
					conn.close();
					return resp;
				}

			}
			else
			{
				// neue Kategorie in existierendem Projekt anlegen
				categoryBean parentcat = db.getCatByID(conn, parentCat);

				if( parentcat == null)
				{
					throw new Exception("XXX");
				}


				if(!checkAuthorisation.checkAddCat(tfsession,parentcat.getRootID()))
				{
					// not authorized
					conn.close();
					return(resp);
				}
				newcat = db.addCategory(conn, newcat, parentcat);
				resp = new AddCategoriesResponse(
					newcat.getCategory(),
					newcat.getID());

			}
		
						
			conn.close();
			return(resp);
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : addCategory)");
			e.printStackTrace();
			DBAccessException dba = new DBAccessException("TeamFoundController : addCategory" + e);
			dba.initCause(e);
			throw dba;
		}
	}
	/*
	 * alle Projekte auslesen
	 *
	 */
	public GetProjectsResponse getProjects(SessionData session) throws DBAccessException
	{
		try
		{
			//1. Versionen der KategorieBaeume und alle RootCats() aus der Datenbank holen
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			Map<Integer,Integer> projver = db.getAllVersion(conn);
			Vector<categoryBean> catvec = db.getAllRootCats(conn);
			//2. mit den Infos die Response fuellen
			GetProjectsResponse resp = new GetProjectsResponse();
			Iterator it = catvec.iterator();
			while(it.hasNext())
			{
				categoryBean cat = (categoryBean)it.next();
				if( checkAuthorisation.checkRead(session, cat.getRootID()))
				{
					resp.addProject(cat.getCategory(), cat.getBeschreibung(),cat.getID(),projver.get(cat.getRootID()), session);
				}
						
			}
			conn.close();
			return(resp);
			
		
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : getProjects)"+e);
			e.printStackTrace();
            DBAccessException a = new DBAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}
		
	}
	/**
	 * Diese Funktion liest die Konfiguration und initialisiert mit den
	 * Angaben die Datenbank und den Index.
	 *
	 * @return true->initialized(entweder war schon oder ist es nun) false->heisst irgentwas funzt nicht richtig im Server
	 */
	protected boolean initServer()
	{
		try
		{
		// Da noch keine Admin schnittstelle wird diese Funktion einfach aufgerufen
		// prueft ob was existiert und wenn nicht legt es die noetigen sachen an ...
		//TODO Admin install interface und diese ueberfluessige Ueberpruefung raus!
		
		//0. kucken ob schon was existiert
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			if(db.initialized(conn))
				return true;

			
		//1. Konfiguration auslesen 
			String tfpath = TeamFoundConfig.getConfValue("tfpath");
			String project = TeamFoundConfig.getConfValue("project");
			String description = TeamFoundConfig.getConfValue("description");
			String adminname = TeamFoundConfig.getConfValue("name");
			String adminpass = TeamFoundConfig.getConfValue("pass");
			
		//2. Index anlegen
			Indexer tfindexer = new TeamFoundIndexer(indexSync);
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
			db.addUserToProject(conn, admin.getID(), catbean.getRootID());
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
			
			conn.commit();
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
	public Response editCategory(Integer category, String catname, String description, SessionData session) throws DBAccessException
	{

		try
		{
		//0. DatenBAnk verbindung		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			categoryBean catbean = db.getCatByID(conn,category);

			// TODO ueberpruefen ob clients Version vom Categorytree akatuell
			// Rechte ueberpruefen!
			if( !checkAuthorisation.checkEditCat(session, catbean.getRootID()))
			{
				ErrorResponse er = new ErrorResponse();
				er.tfReturnValue(9);
				return er;
			}
			
		//1. Datenbank eintraege zur Kategorie aendern
			// erstmal alte zustand auslesen			
			if((catname != null)&&(catname.length() > 0))  // catname darf nie leer werden
			{
				catbean.setCategory(catname);
			}
			if(description != null) // beschreibung darf geloescht werden (string "")
			{
				catbean.setBeschreibung(description);
			}
			db.updateCat(conn, catbean);
		
		//2. Versionsnummer erhoehen
		// TODO
			
		//3. response fuellen
			return new EditCategoryResponse(catbean.getCategory(), catbean.getBeschreibung(), catbean.getID());
			
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : addCategory)"+e);
			DBAccessException dbe = new DBAccessException("TeamFoundController : addCategory)"+e);
			dbe.initCause(e);
			throw dbe;
		}
		
	}
	
	/**
	 * Liest den Inhalt für das übergebene Dokument neu ein. Dabei werden die Kategorien des Dokuments nicht verändert
	 * 
	 * Diese Methode wird zB. vom autgomatischen Updater benutzt um veränderungen an seiten in den Index zu holen
	 * @param doc Das neue Dokument
	 * @return 1 wenn die seite geupdated wurde, 0 wenn nicht,-1 bei fehler
	 * 
	 * TODO rückgabe könnte verschönert werden, response-objektre sind aber unsinnig, da der updateThread die garnicht braucht
	 */
	public int updateDocument(NewIndexEntry doc) {
		/* 
		 * 1. 	hole alte seite, bzw. einen hash davon und teste ob
		 * 		sich die neue seite überhaupt davon unterscheidet
		 */
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch(NoSuchAlgorithmException e) {
			log.fatal(e);
			return -1;
		}
		md.update(doc.getContent().getBytes());
		byte[] digestNew = md.digest();
		
		// TODO hier muss der digest vom alten dokument berechnet oder aus der datenbank geholt werden
		md.update(doc.getContent().getBytes());
		byte[] digestOld = md.digest();
		
		if(!MessageDigest.isEqual(digestNew, digestOld)) {
			/*
			 * 2. 	wenn änderungen, lese die seite neu ein und ändere den 
			 * 		last-update wert in der datenbank auf jetzt
			 */
			// TODO dokument in der datenbank sowie dem index updaten
			/*
			 * 3a. 	antworten
			 */
			return 1;
		} else {
			/*
			 * 3b. es hat sich nichts geändert, generieren wir eine entsprechende antwort
			 */
			return 0;
		}
	}
	
	
	/**
	 * Einen neuen User anlegen
	 *
	 * @param name Username
	 * @param pass passwort
	 */
	public NewUserResponse newUser(String user, String pass) throws DBAccessException 
	{	
		try
		{
			//1. Establish connection
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

	


			HashSet<Tuple<Integer,Integer>> vertup = db.getAllVersions(conn);

		//2. user in db adden

			tfuserBean newuser = new tfuserBean();
			newuser.setUsername(user);
			newuser.setPass(pass);

			//Username zu kurz ?
			if(user.length() < 3)
			{
				NewUserResponse re = new NewUserResponse(newuser.getUsername());
				//7 ist ReturnCode fuer Username zu kurz
				re.tfReturnValue(new Integer(7));
				return(re);
			}
			if(pass.length() < 3)
			{
				NewUserResponse re = new NewUserResponse(newuser.getUsername());
				//10 ist ReturnCode fuer Passwort zu kurz
				re.tfReturnValue(new Integer(10));
				return(re);
			}
			
			//Username schon vergeben ?
			if(db.getUserByName(conn,newuser.getUsername()) != null)
			{
				NewUserResponse re = new NewUserResponse(newuser.getUsername());
				//6 ist ReturnCode fuer User existiert (siehe spezifikation
				re.tfReturnValue(new Integer(6));
				return(re);
			}
		
			newuser = db.createNewUser(conn, newuser);

			//3.response liefern		
			NewUserResponse	resp = new NewUserResponse(newuser.getUsername());
			
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
	public boolean checkUser(String user, String pass) throws DBAccessException 
	{	
		try
		{
			//1. Establish connection
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			//2. in DB nachschauen
			tfuserBean tf = db.getUserByName(conn,user);
			if(tf != null)
			{
				if(pass.equals(tf.getPass()))
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
	public LoginResponse rejectUser(String user) throws DBAccessException 
	{	
		try
		{
			//1. Establish connection
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			conn.close();
			//2. antwort erstellen

			LoginResponse resp = new LoginResponse(
					user,
					null);
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
	public LoginResponse loginUser(String user, String pass, String sessionkey,Date last) throws DBAccessException 
	{	
		try
		{
			//1. Establish connection
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			//2.Session info speichern
			tfuserBean tfun = db.getUserByName(conn,user);
			userRightBean uright = db.getRights(conn,tfun.getID());
			SessionData.addSession(sessionkey,uright,tfun);

			conn.close();
			//3. antwort erstellen

			LoginResponse resp = new LoginResponse(
					user,
					sessionkey);
			return(resp);

		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(e);
			throw(dbe);

		}
	}
	
	public int updateDocument(NewIndexEntry nd, int[] categories) {
		// TODO Auto-generated method stub
		return -1;
	}

	public EditPermissionsResponse editPermissions(Integer projectid, SessionData tfsession, Boolean _useruseradd, 
	Boolean _userurledit,
	Boolean _usercatedit,
	Boolean _useraddurl,
	Boolean _useraddcat,
	Boolean _guestread,
	Boolean _guesturledit,
	Boolean _guestcatedit,
	Boolean _guestaddurl,
	Boolean _guestaddcat ) throws IndexAccessException,  DBAccessException
	{
		
		try
		{
			// 0. Datenbank 
		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

			// Basis-Antwort bauen
			EditPermissionsResponse resp = new EditPermissionsResponse(projectid);

			// 1. Userrechte ueberpruefen (seit Milestone 3)
			
			if(!(checkAuthorisation.isAdmin(tfsession,projectid)))
			{
				resp.tfReturnValue(9);
			}
			
			// 2. Permissions anpassen	
			projectdataBean pdata = new projectdataBean(projectid,
					_useruseradd, 
					_userurledit,
					_usercatedit,
					_useraddurl,
					_useraddcat,
					_guestread,
					_guesturledit,
					_guestcatedit,
					_guestaddurl,
					_guestaddcat);

			db.setProjectdata(conn, pdata);
			projectdataBean pdb = db.getProjectDataToCat(conn, projectid);
			SessionData.projectdata.put(pdb.getRootID(),pdb);
			conn.close();
			return (resp);
		}
		catch(SQLException e)
		{
			System.out.println("TeamFoundController : editPermission)"+e);
            DBAccessException a = new DBAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}
		catch(Exception e)
		{
			//TODO Exceptions richtig machen
 			System.out.println("TeamFoundController : editPermission)"+e);
			e.printStackTrace();
            IndexAccessException a = new IndexAccessException("nested Exception");
			a.initCause(e);
			throw a;
		}

	}


	/**
	 * User einem Project zuordnen
	 *
	 * @param String user
	 * @param Integer projectid
	 * @param SessionData
	 * @return Response
	 */
	public Response addUserToProject(String user,Integer projectid, SessionData tfsession) throws DBAccessException 
	{	
		try
		{
			if(!(checkAuthorisation.userAdd(tfsession,projectid)))
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(9));
			}

			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			
			tfuserBean tfuser = db.getUserByName(conn,user);
			if(tfuser == null)
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(11));
			}

			conn.setSavepoint("usertoproject");
			db.addUserToProject(conn, tfuser.getID(), projectid);
			// DataSession updaten
			projectdataBean pdb = db.getProjectDataToCat(conn, projectid);
			SessionData.projectdata.put(pdb.getRootID(),pdb);
			conn.commit();

			UserToProjectResponse re = new UserToProjectResponse(user,projectid);
			re.setRoleUser();
			return(re);
		
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(e);
			throw(dbe);

		}
	}

	/**
	 * User ProjektAdminRechte in einem Project geben
	 *
	 * @param String user
	 * @param Integer projectid
	 * @param sessiondata
	 * @return Response
	 */
	public Response grantProjectAdmin(String user,Integer projectid,SessionData tfsession) throws DBAccessException 
	{	
		try
		{
			if(!(checkAuthorisation.isAdmin(tfsession,projectid)))
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(9));
			}

			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			
			tfuserBean tfuser = db.getUserByName(conn,user);
			if(tfuser == null)
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(11));
			}

			conn.setSavepoint("usertoproject");
			if(!db.addUserToAdminsOfProject(conn, tfuser.getID(), projectid))
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(12));
			}
			// DataSession updaten
			projectdataBean pdb = db.getProjectDataToCat(conn, projectid);
			SessionData.projectdata.put(pdb.getRootID(),pdb);
			conn.commit();

			UserToProjectResponse re = new UserToProjectResponse(user,projectid);
			re.setRoleAdmin();
			return(re);
		
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(e);
			throw(dbe);

		}
	}

	/**
	 * User aus Projekt entfernen
	 *
	 * @param String user
	 * @param Integer projectid
	 * @param sessiondata
	 * @return Response
	 */
	public Response removeUserFromProject(String user,Integer projectid, SessionData tfsession) throws DBAccessException 
	{	
		try
		{
			if(!(checkAuthorisation.isAdmin(tfsession,projectid)))
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(9));
			}

			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
		
			tfuserBean tfuser = db.getUserByName(conn,user);
			if(tfuser == null)
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(11));
			}
		
			db.removeFromProject(conn, tfuser.getID(), projectid);

			RemoveUserResponse re = new RemoveUserResponse(user,projectid);
			return(re);
		
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(e);
			throw(dbe);

		}
	}

	/**
	 * User von Projekt ausgeben
	 *
	 * @param Integer projectid
	 * @return Response
	 */
	public Response getUsersOfProject(Integer projectid, SessionData tfsession) throws DBAccessException 
	{	
		try
		{
			if(tfsession == SessionData.guest || !(tfsession.urb.isAdmin(projectid)) || !tfsession.urb.isUser(projectid))
			{
				ErrorResponse re = new ErrorResponse();
				re.tfReturnValue(new Integer(9));
			}

			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
		
			Vector<Tuple<tfuserBean,Boolean>> vuser = db.getUsersOfProject(conn,projectid);

			Iterator vuserit = vuser.iterator();
			GetUsersOfProjectResponse re = new GetUsersOfProjectResponse(projectid);
			while(vuserit.hasNext())
			{
				Tuple user = (Tuple)vuserit.next();
				re.addUser((tfuserBean)user.getFirst(), (Boolean)user.getSecond());
			}

			return(re);
		
		}
		catch(Exception e)
		{
 			System.out.println("TeamFoundController : loginUser)"+e);
			DBAccessException dbe = new DBAccessException("loginUser SQLException");
			dbe.initCause(e);
			throw(dbe);

		}
	}
	
}
