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
import java.sql.SQLException;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
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
import controller.SessionData;

import config.teamfound.TeamFoundConfig;

import db.teamfound.DBLayerHSQL;
import db.DBLayer;
import db.dbbeans.*;

import sync.ReadWriteSync;

import tools.Tuple;

import org.apache.lucene.document.Document;

public class TeamFoundController implements Controller {

	Download loader = new Download();
	public static ReadWriteSync indexSync;
	private DBLayer db;
	
	public TeamFoundController() 
	{
		indexSync = new ReadWriteSync();
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
	public AddPageResponse addToIndex(String url, int category[]) throws DownloadFailedException, IndexAccessException {
	

		URL adress = null;
		try {
			adress = new URL(url);
		
		} catch (MalformedURLException e) {
			DownloadFailedException e2 = new DownloadFailedException("nested MalformedURLException");
			e2.initCause(e);
		}
		
		//Connection fuer DB
		Connection conn;
		
		//Indexer erstellen
		Indexer tfindexer = new TeamFoundIndexer(indexSync);
		
		try
		{
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			//ProjectVersionen auslesen fuer die Response
			Vector<Integer> newcatstoadd = getAllPar(conn,category);//neue Kats mit elternKats 
			
			// 0. Datenbank auf Existenz der URL checken
			urltabBean urlbean = db.getUrl(conn,adress.toString());
			if(urlbean != null)
			{
				System.out.println("habe Url schon indiziert!");
				Vector<Integer> oldcats = db.getCatsOfUrl(conn,urlbean.getID());
				
				if(oldcats.containsAll(newcatstoadd))
				{
					//brauchen nichts erneueren
					System.out.println("Nichts an der URL hat sich geaendert!");
					AddPageResponse resp = new AddPageResponse(adress.toString());
					return(resp);
				}
				else
				{

					System.out.println("Neue Categorys zu der URL hinzutun");
					HashSet<Integer> allcats = new HashSet<Integer>();
					allcats.addAll(oldcats);
					allcats.addAll(newcatstoadd);
					tfindexer.updateCategory(adress.toString(),allcats);	
					
					//3.DB Aktualisieren
					categoryBean catbean = new categoryBean();
					for(int i=0;i<category.length;i++)
					{
						if(!oldcats.contains(new Integer(category[i])))
						{
							catbean.setID(new Integer(category[i]));
							db.addCatwithParentsToUrl(conn,urlbean,catbean);
						}
					}

					
					//4.Erfolgsmeldung liefern (liste von (projectid, katbaumversion)
					AddPageResponse resp = new AddPageResponse(adress.toString());
					return(resp);

				}

			}
	
			/*Url muss heruntergeladen und neu Indiziert werden*/
			//System.out.println("habe Url noch nicht indiziert!");
			
			int[] categ = new int[newcatstoadd.size()];
			Iterator intit = newcatstoadd.iterator();
			int i = 0;
			while(intit.hasNext())
			{
				categ[i]= ((Integer)intit.next()).intValue();
				i=i+1;
			}
			
			
			// 1. URL herunterladen
			//System.out.println("1. Url herunterladen!");			
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
			AddPageResponse resp = new AddPageResponse(adress.toString());
			
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

	/*
	 * @deprecated
	 */

	@Deprecated public SearchResponse search(String query, int category[]) throws IndexAccessException, ServerInitFailedException
	{
		
		try{
	// 0. Datenbank nach Kategorienversionen durchsuchen
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

	// 1. Im Index Suchen
		
			Indexer tfindexer = new TeamFoundIndexer(indexSync);
			//TODO -> hart den count auf 30 und den Offset auf 0 ??
			Vector<Document> docvec = tfindexer.query(query, category , 50, 0 ); 
	
		
	//2.Antwort bauen
			//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(keywords);
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

	
	public SearchResponse search(String query, int offset, int category[], SessionData tfsession) throws IndexAccessException, ServerInitFailedException, DBAccessException

	{
		
		try
		{
	// 0. Datenbank 
		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

	// Basis-Antwort bauen
		//TODO keywords ?
			String[] keywords = new String[1];
			keywords[0] = query;
			SearchResponse resp = new SearchResponse(keywords);

	// 0.1. Userrechte ueberpruefen (seit Milestone 3)
			
			// alle categorien ueberpruefen
			for( int i = 0; i < category.length; i++)
			{
				// root-cat der cat auslesen
				categoryBean cb = db.getCatByID(conn, category[i]);

				if( cb == null)
				{	// category not found
					resp.tfReturnValue(new Integer(5));
					return(resp);
				}
				if( SessionData.projectdata == null)
				{
					System.out.println("NO PROJECTDATA");
					resp.tfReturnValue(new Integer(-1));
					return(resp);
				}

				if( tfsession == SessionData.guest || (!tfsession.urb.isUser(cb.getRootID()) && !tfsession.urb.isAdmin(cb.getRootID())))
				{
					// GAST
					if( SessionData.projectdata.get(cb.getRootID()).getGuestRead().booleanValue() == false)
					{
						resp.tfReturnValue(new Integer(9));
						return(resp);
					}
				}
			}
			
		
	// 1. Im Index Suchen
		
			Indexer tfindexer = new TeamFoundIndexer(indexSync);
			//TODO -> hart den count auf 30 ?
			Vector<Document> docvec = tfindexer.query(query, category , 50, offset ); 
	
		
	//2.Suchergebnisse in Antwort einbauen
			resp.addSearchResults(docvec);
		
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

	public GetCategoriesResponse getCategories(int rootid) throws ServerInitFailedException 
	{
			
		try
		{
		//0. DatenBAnk verbindung		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

		//3. response fuellen
			GetCategoriesResponse resp = new GetCategoriesResponse();
			
			categoryBean rootbean = db.getCatByID(conn,rootid);

			if( rootbean != null)
			{

				Vector<categoryBean> childvec = db.getAllChildCategorys(conn, rootbean);
			
				//nur zum testen
				//System.out.println("Rootbean:");
				//rootbean.printAll();
				
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
					//System.out.println("\ncurrent parentbean:");
					//parent.printAll();
					
					//nur zum testen
					//System.out.println("\ncurrent child:");
					//catbean.printAll();
					
					//System.out.println("\n");

					
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
			return null;
		}
		
	}

	public AddCategoriesResponse addCategory(String name, int parentCat, String description, SessionData tfsession) throws DBAccessException
	{
		AddCategoriesResponse resp;
		try
		{
		//1. Versionen der KategorieBaeume aus db
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
						db.addUserToAdminsOfProject(conn, tfsession.tfu.getID(), newcat.getID());
						// In DataSession neue Sachen eintragen
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
					resp = new AddCategoriesResponse(
							"",
							new Integer(-1));
					resp.tfReturnValue(new Integer(9));
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


				// checke ob guestaddcat == true oder 
				// user eingeloggt, zu diesem projekt gehoert und projekt useraddcat == true gestzt hat
				// oder user admin des projekts ist
				if( !SessionData.projectdata.get(parentcat.getRootID()).getGuestAddcat().booleanValue())
				{
					if( tfsession == SessionData.guest)
					{
						// not authorized
						resp = new AddCategoriesResponse(
								"",
								new Integer(-1));
						resp.tfReturnValue(new Integer(9));

						conn.close();
						return(resp);
					}
					if( !tfsession.urb.isAdmin(parentcat.getRootID()))
					{
						if( !tfsession.urb.addCat(parentcat.getRootID()))
						{
								// not authorized
								resp = new AddCategoriesResponse(
										"",
										new Integer(-1));
								resp.tfReturnValue(new Integer(9));

								conn.close();
								return(resp);
						}
					}
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
			//TODO Exceptions richtig machen
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
	public GetProjectsResponse getProjects() throws DBAccessException
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
				resp.addProject(cat.getCategory(), cat.getBeschreibung(),cat.getID(),projver.get(cat.getRootID()));
						
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
	public boolean initServer()
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
		try
		{
		//0. DatenBAnk verbindung		
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");

		// TODO ueberpruefen ob clients Version vom Categorytree akatuell
		// Rechte ueberpruefen!
			
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
		try
		{
		//1. Establish connection
			Connection conn;
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");


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
	public boolean checkUser(String user, String pass) throws DBAccessException, ServerInitFailedException
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
	public LoginResponse rejectUser(String user) throws DBAccessException, ServerInitFailedException
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
	public LoginResponse loginUser(String user, String pass, String sessionkey,Date last) throws DBAccessException, ServerInitFailedException
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

}
