/**
 * Created 10.12.2005
 *
 * DBlayer fuer HSQLDB 
 * entwickelt um HSQLDB in teamfound eingebettet laufen zu lassen und
 * nicht als Server.
 *
 * @author Martin Klink
 */

package db.teamfound;

import db.DBLayer;
import db.dbbeans.*;
import tools.Tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.Vector;
import java.util.List;
import java.util.HashSet;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Date; 
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator; 
import config.teamfound.TeamFoundConfig;
import controller.SessionData;

import org.apache.log4j.Logger;

public class DBLayerHSQL implements DBLayer
{

	protected Logger log;
	public DBLayerHSQL()
	{
		log = Logger.getLogger("tf-db");
	}
	
	/** 
	* Verbindung zur Datenbank herstellen
	*
	* @param user  Nutzername
	* @param pass  Passwort
	* @param server Servername
	* @param database DBname
	*/ 
	public Connection getConnection(String user, String pass, String server, String database)
		throws  java.lang.ClassNotFoundException,
				java.lang.InstantiationException,
				java.lang.IllegalAccessException,
				java.sql.SQLException
	{
		// Im standalone Modus haben wir keinen Server damit ist diese Variable unintressant.
		// Zurzeit wuerde die Datenbank(name im String database) unter
		// xxxx/db/ landen (xxxx stammt aus properties file)
		// Vorschlag ein teamfound folder fuer index und db ..
		String dblocation = new String("jdbc:hsqldb:file:"+TeamFoundConfig.getConfValue("tfpath")+"/db/"+database+";ifexists=true");
	
		
		//get JDBC Driver
		Class.forName("org.hsqldb.jdbcDriver" );
		//get Connection
		try
		{
			Connection c = DriverManager.getConnection(dblocation, user, pass);
			c.setAutoCommit(false);//will selbst SavePoints und commits definieren!			 
			return(c);
		}
		catch(java.sql.SQLException e)
		{
			//DB scheint nicht zu existieren
			createDB(user,pass,database);
		}

		Connection c = DriverManager.getConnection(dblocation, user, pass);
		return(c);
	}
	/** 
	* Erstellt Datenbank und legt unsrere TeamfoundTabellen an. 
	* Wird nur gerufen wenn keine DB existiert.
	*
	* @param user  Nutzername
	* @param pass  Passwort
	* @param database DBname
	*/
	private void createDB(String user, String pass, String database)
		throws  java.lang.ClassNotFoundException,
				java.lang.InstantiationException,
				java.lang.IllegalAccessException,
				java.sql.SQLException
	{
		String dblocation = new String("jdbc:hsqldb:file:"+TeamFoundConfig.getConfValue("tfpath")+"/db/"+database);
	
		
		//get JDBC Driver
		Class.forName("org.hsqldb.jdbcDriver" );
		//get Connection
		Connection c = DriverManager.getConnection(dblocation, "sa", "");
		
		//wollen alles oder nichts
		java.sql.Savepoint init; 
		init = c.setSavepoint("initialisierung");
		
		try
		{
			//Password vom admin aendern
			if(!update(c,"ALTER USER sa SET PASSWORD tf567X"))
				log.error("error changepassword ");
			

			//User anlegen
			String cuser = new String("CREATE USER "+user+" PASSWORD "+pass);
			if(!update(c,cuser))
					log.error("error bei create User ");
			
			//create Table tfuser fuer Usermanagement
			String sqlcreate = "CREATE TABLE tfuser (id INTEGER IDENTITY,username VARCHAR, pass VARCHAR,sessionkey VARCHAR, lastaction DATETIME, serveradmin BOOLEAN)";
			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);

			//create Category table
			sqlcreate = new String("CREATE TABLE category (id INTEGER IDENTITY,root_id INTEGER, left INTEGER, right INTEGER, name VARCHAR,beschreibung VARCHAR, owner INTEGER, FOREIGN KEY(owner) REFERENCES tfuser(id))");			
			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);

			//create URL table
			sqlcreate = "CREATE TABLE indexedurls (id INTEGER IDENTITY,url VARCHAR,indexdate DATE, rating INTEGER)";
			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);

			//create ForeignKeyTable Url <-> Category
			sqlcreate = "CREATE TABLE urltocategory (id INTEGER IDENTITY,url INTEGER,category INTEGER,rating INTEGER, originalcat INTEGER, FOREIGN KEY (category) REFERENCES category(id), FOREIGN KEY (url) REFERENCES indexedurls(id))";
			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);

			//Project-Daten
			//nicht jede Category sondern nur jede root Cat braucht eine Version
			sqlcreate = "CREATE TABLE projectdata(id INTEGER IDENTITY,rootid INTEGER,version INTEGER, useruseradd BOOLEAN, userurledit BOOLEAN, usercatedit BOOLEAN, useraddurl BOOLEAN, useraddcat BOOLEAN, guestread BOOLEAN, guesturledit BOOLEAN, guestcatedit BOOLEAN, guestaddurl BOOLEAN, guestaddcat BOOLEAN)";//,FOREIGN KEY (rootid) REFERENCES category(root_id))";

			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);
			
			
			//create ForeignKeyTable User <-> Project
			sqlcreate = "CREATE TABLE tfusertoproject (id INTEGER IDENTITY,userid INTEGER, rootid INTEGER, isadmin BOOLEAN)";//, FOREIGN KEY (userid) REFERENCES tfuser(id), FOREIGN KEY (rootid) REFERENCES projectdata(rootid))";
			if(!update(c,sqlcreate))
				log.error("error in Statement "+ sqlcreate);


			
			//Grant rights to user
			
			cuser=("GRANT ALL ON category TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");
									
			cuser=("GRANT ALL ON indexedurls TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");
			
			cuser=("GRANT ALL ON urltocategory TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");

			cuser=("GRANT ALL ON projectdata TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");

			cuser=("GRANT ALL ON tfuser TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");

			cuser=("GRANT ALL ON tfusertoproject TO " +user);
			if(!update(c,cuser))
				log.error("error bei grant to User ");


			c.commit();
	
		}
		catch (SQLException ex2)
		{
			c.rollback();
			//existiert wohl schon 
			//dann brauch ich auch nichts anlegen
			log.error("Fehler beim DB initialisieren" +ex2);
			throw(ex2);
		}
		

		//close Connection
		c.close();
	}
	
	//Wenn wir hsqldb embedded laufen lassen muessen wir selbst dafuer sorgen
	//dass beim beenden von teamfound auch die Datenbank sauber runtergefahren
	//und alles persistent gespeichert wird.
	//Achtung : Geht nur wenn keine anderen Connections mehr existieren
	//			wir muessen darauf achten dass alle threads am ende immer ihre 
	//			Connection schliessen!
	/** 
	* Auswirkung nur bei embedded DBMode.Sichert persistenz!Muss beim beenden von Teamfound aufgerufen werden.
	* 
	* @param server Servername
	* @param database DBname
	* TODO Methode die shutdown Compact aufruft und sozusagen dafuer sorgt dass bei langem betrieb alles richtig gespeichert wird!
	*/
	
	public void shutdown(String server,String database) 
		throws 	SQLException,
				java.lang.ClassNotFoundException,
				java.lang.InstantiationException,
				java.lang.IllegalAccessException
			   
	{
		Connection conn = getConnection("sa","tf567X",server,database);
		Statement st = conn.createStatement();

		// db schreibt alles aus dem Memory physich auf und macht ein clean
		// ohne sowas haben wir  unclean shutdown                    
		st.execute("SHUTDOWN");
		conn.close();    // nur wenn keine anderen Connections bestehen						
	}


	/**
	 * dummie fuer select Anfragen
	 * nur als testfunktion zu verwenden..
	 * Schreibt einfach Ergebnisse an Konsole.
	 */
	public void query(Connection conn,String expression) throws  SQLException
	{
		Statement st = null;
		ResultSet rs = null;

		st = conn.createStatement();         
		rs = st.executeQuery(expression);    // query ausfuehren

		// result ausgeben
		ResultSetMetaData meta   = rs.getMetaData();
		int colmax = meta.getColumnCount();
		int i;
		Object o = null;
		for (; rs.next(); ) 
		{
			for (i = 0; i < colmax; ++i) 
			{
				o = rs.getObject(i + 1);    //SQL erte Spalte ist 1 nicht 0
				if(!(o==null))
				System.out.print(o.toString() + " ");
			}
			System.out.println(" ");
		}
		
		
		st.close();    
		// NOTE!! if you close a statement the associated ResultSet is
		// closed too
		// so you should copy the contents to some other object.
		// the result set is invalidated also  if you recycle an Statement
		// and try to execute some other query before the result set has been
		// completely examined.
	}

	/** 
	* Methode zum ausfuehren einzelner Create, Drop, Insert, und Update Anfragen
	* 
	* Note: fuer viele updates nacheinader sollte das StatementObject immer
	* wieder verwenden ...
	* 
	* @param user  Nutzername
	* @param pass  Passwort
	* @param database DBname
	*/
	private boolean update(Connection conn,String expression) throws  SQLException
	{
		Statement st = null;

		st = conn.createStatement();    // statements

		int i = st.executeUpdate(expression);    // run the query

			if (i == -1) 
		{
			return false;
			//spaeter mal Loggeintrag
			//db error while executing: + expression
		}
		st.close();
		return true;
	}

	/**
	 * Einfuegen einer Category ins Set ..
	 * von der uebergeordneten Kategorie reicht die ID der rest wird 
	 * in diser Methode ausgelesen! 
	 * 
	 *	Bsp.          1A6
	 *			2B3			4C5
	 * Wir wollen an B was anhaengen dann brauchen wir A und 2,3
	 *
	 *
	 */
	public categoryBean addCategory(Connection conn, categoryBean catbean, categoryBean metacat) throws SQLException
	{
		//Bean fuer Return anlegen
		categoryBean re;
		re = catbean;
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		//zu welchem RootKnoten gehoert die neue Kategorie
		String getRootId = new String("select root_id from category where id = "+metacat.getID());
		Integer rootid;
		//rechts von der ElternKategorie
		String getMetaRight = new String ("select right from category where id ="+ metacat.getID());
		
		conn.setSavepoint("addcat");
		try
		{	
			//Rootid holen
			ResultSet rsi = st.executeQuery(getRootId);
			rsi.next();
			rootid = rsi.getInt(1);

			
			//Right holen
			rsi = st.executeQuery(getMetaRight);
			rsi.next();
			metacat.setRight(rsi.getInt(1));
		
			//bei allen Knoten die zur selben Wurzel gehoeren und deren linker Wert > 
			//dem rechten der Metakategorie ist muss links um 2 erhoeht werden		
			String updateLeft = new String("UPDATE category SET left = left+2 WHERE root_id = "+rootid+" and left > "+metacat.getRight());
		
			//bei allen Knoten die zur selben Wurzel gehoeren und deren rechter Wert >= 
			//dem rechten der Metakategorie ist muss rechts um 2 erhoeht werden  
			String updateRight = new String("UPDATE category SET right = right+2 WHERE root_id = "+rootid+" and right >= "+metacat.getRight());
	
			//Einfuegen des neuen Knotens
			String insertNew = new String("INSERT INTO category (root_id, left, right, name, beschreibung) VALUES ("+rootid+","+metacat.getRight()+","+(metacat.getRight()+1)+",'"+catbean.getCategory()+"','"+catbean.getBeschreibung()+"')");
	
			//ausfuehren der Updates und des Inserts
			st.executeUpdate(updateLeft);	
			st.executeUpdate(updateRight);	
			st.executeUpdate(insertNew);	
			
			//erzeugte id holen
			rsi = st.executeQuery("CALL IDENTITY()");
			rsi.next();
			int identity = rsi.getInt(1);

			//update der projectdata
			String updateCatVersion = new String("UPDATE projectdata SET version = version+1 WHERE rootid = "+rootid);
			st.executeUpdate(updateCatVersion);	
			
			conn.commit();
			
			//Bean fuer Rueckgabe bereit machen
			re.setID(identity);
			re.setRootID(rootid);
			re.setLeft(metacat.getRight());
			re.setRight(metacat.getRight()+1);
			
			return(re);
		}
		catch(SQLException e)
		{
			conn.rollback();
			//logging machen
			log.error("AddCategory :"+e);
			throw(e);
		}
		
	}

	
	/**
	 * Ersetzt Name und Beschreibung der Category
	 */
	public void updateCat(Connection conn, categoryBean catbean) throws SQLException
	{
		PreparedStatement st = null;
		Statement stc = conn.createStatement();
		st = conn.prepareStatement("UPDATE category set name = ?, beschreibung = ? where id = ?");    // erstelle statements
		try
		{	
			st.setString(1, catbean.getCategory());
			st.setString(2, catbean.getBeschreibung());
			st.setInt(3, catbean.getID());

			//ausfuehren der Updates und des Inserts
			st.executeUpdate();	
			
			//update der projectdata
			String updateCatVersion = new String("UPDATE projectdata SET version = version+1 WHERE rootid = "+catbean.getRootID());
			stc.executeUpdate(updateCatVersion);	
			
			conn.commit();

		}
		catch(SQLException e)
		{
			//logging machen
			log.error("UpdateCategory :"+e);
			throw(e);
		}
	}

	
	/**
	 * Einfuegen einer rootCategory ins Set ..
	 *
	 */
	public categoryBean addRootCategory(Connection conn, categoryBean catbean) throws SQLException
	{
		//Bean fuer Return anlegen
		categoryBean re = catbean;
		Statement st = null;
		st = conn.createStatement();    // erstelle statements

		PreparedStatement newcat = conn.prepareStatement("INSERT INTO category(left,right,name,beschreibung) VALUES(1,2,?,?)");
		newcat.setString(1, catbean.getCategory());
		newcat.setString(2, catbean.getBeschreibung());
		
		//returnbean fuellen
		re.setLeft(1);
		re.setRight(2);
		
		//Statement um PrimKey zu kriegen
		String prim = new String("CALL IDENTITY()");
		
		try
		{

			//ausfuehren des inserts
			newcat.executeUpdate();

			//erzeugte id holen
			ResultSet rsi = st.executeQuery(prim);
			rsi.next();
			int identity = rsi.getInt(1);
			
			//returnbean fuellen
			re.setID(identity);
		    re.setRootID(identity);
			
			//update von root_id
			String upd = new String("UPDATE category SET root_id = " +identity+ " WHERE id =" +identity);
			st.executeUpdate(upd);			

			//neuer Eintrag in CategoryVersionTabelle
			String insert = new String("INSERT INTO projectdata(rootid,version,useruseradd, userurledit, usercatedit, useraddurl, useraddcat , guestread , guesturledit , guestcatedit , guestaddurl , guestaddcat ) VALUES("+identity+",1, false, false , false, true, true,true , false, false, false ,false)");
			st.executeUpdate(insert);			

			return(re);
			
		}
		catch(SQLException e)
		{
			log.error("AddRoot: "+ e);
			throw(e);
		}
		
	}

	/** 
	* Loescht ein Blatt aus dem Category-Baum (nested Set) 
	* Achtung keine Fehlermeldung falls es kein Blatt war und
	* in dem fall wird einfach gar nichts gemacht ....
	* TODO exception fuer den beschriebenen Fall
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public boolean deleteLeafCategory(Connection conn, categoryBean catbean) throws SQLException
	{
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		//TODO pruefen ob alles schon in der Bean gespeichert ist
		//Benoetigte Informationen (Root Left und Right) auslesen
		String getBeanInfo = new String("select root_id,left,right from category where id = "+catbean.getID());
		conn.setSavepoint("deleteLeaf");
		try
		{	
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setRootID(rs.getInt(1));
			catbean.setLeft(rs.getInt(2));
			catbean.setRight(rs.getInt(3));
			//pruefen ob es wirklich ein Leaf ist
			if(catbean.getLeft()+1 != catbean.getRight())
			{
				log.info("DeleteLeafCategory: Zu loeschende Category ist kein Blatt!");
				return false;
			}

			//Category loeschen
			String stmnt = new String("DELETE FROM category WHERE id = "+catbean.getID());
			st.executeUpdate(stmnt);

			//die restlichen Knoten anpassen
			stmnt = ("UPDATE category SET left = left-2 WHERE root_id = "+catbean.getRootID()+" AND left > "+catbean.getRight());
			st.executeUpdate(stmnt);
			
			stmnt  = ("UPDATE category SET right = right-2 WHERE root_id = "+catbean.getRootID()+" AND right > "+catbean.getRight());
			st.executeUpdate(stmnt);

			//Zugordnungen zu Urls entfernen
			//TODO sollte diese Methode ueberhaupt gehen wenn urls zugeordnet sind?
			//stmnt = ("DELETE FROM urlstocategory WHERE id = "+catbean.getID());
			//st.executeUpdate(stmnt);

			//Falls Projekt muss auch der Eintrag aus der Projekttabelle raus und die Zuordnung der User
			if(catbean.getID().equals(catbean.getRootID()))
			{
				stmnt = new String("DELETE FROM projectdata WHERE rootid = "+catbean.getID());
				st.executeUpdate(stmnt);
				
				stmnt = new String("DELETE FROM tfusertoproject WHERE rootid = "+catbean.getID());
				st.executeUpdate(stmnt);

			}

			conn.commit();
			return true;
		}
		catch(SQLException e)
		{
			conn.rollback();
			//logging machen
			log.error("deleteLeafCategory :"+e);
			throw(e); 
		}
		
	}
	/** 
	* Loescht einen TeilBaum aus dem Category-Baum (nested Set) 
	* TODO alle Zuordnungne zu Urls entfernen oder soll das uebergeordnet geregelt werden? 
	* Ist es ueberhaupt in unserem Prog erlaubt ?
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public void deletePartialTree(Connection conn, categoryBean catbean) throws SQLException
	{
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		//TODO pruefen ob alles schon in der Bean gespeichert ist
		//Benoetigte Informationen (Root Left und Right) auslesen
		String getBeanInfo = new String("select root_id,left,right from category where id = "+catbean.getID());
		conn.setSavepoint("deletePartialTree");
		try
		{	
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setRootID(rs.getInt(1));
			catbean.setLeft(rs.getInt(2));
			catbean.setRight(rs.getInt(3));

			//wert um den verschoben wird berechnen
			double move = java.lang.Math.floor((catbean.getRight() - catbean.getLeft())/2);
			move = 2 * (1 + move);

			//Categorien loeschen
			String stmnt = new String("DELETE FROM category WHERE root_id = "+catbean.getRootID()+" AND left BETWEEN "+catbean.getLeft()+" AND "+catbean.getRight());
			st.executeUpdate(stmnt);

			//die restlichen Knoten anpassen
			stmnt = ("UPDATE category SET left = left - "+move+" WHERE root_id = "+catbean.getRootID()+" AND left > "+catbean.getRight());
			st.executeUpdate(stmnt);
			
			stmnt  = ("UPDATE category SET right = right - "+move+" WHERE root_id = "+catbean.getRootID()+" AND right > "+catbean.getRight());
			st.executeUpdate(stmnt);

			conn.commit();
			return;
		}
		catch(SQLException e)
		{
			conn.rollback();
			//logging machen
			log.error("deletepartialTree :"+e);
			throw(e); 
		}
		
	}

	/** 
	* Loescht eine beliebigen Knoten aus dem Category-Baum (nested Set) 
	* TODO alle Zuordnungne zu Urls entfernen 
	* Ist es ueberhaupt in unserem Prog erlaubt ?
	* 
	* @param conn  Connection
	* @param catbean  categoryBean
	*
	*/
	public void deleteCategory(Connection conn, categoryBean catbean) throws SQLException
	{
		//TODO
	}
	
		
	/**
	 * Hinzufuegen einer Url(nur Url in der Bean benoetigt) 
	 * ohne zugehoerige Category
	 * liefert bean mit der ID und dem erzeugten Datum zurueck
	 * Achtung: keine Ueberpruefung ob URL bereits existiert!
	 *
	 * Deprecated! addUrl mit Category benutzen
	 * 
	 * @param conn  Connection
	 * @param urlbean urltabBean
	 * 
	 **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean) throws SQLException
	{
		//Bean fuer Return anlegen
		urltabBean re = urlbean;

		//Datum erzeugen mit den Werten von "jetzt"
		java.util.Date d = new java.util.Date();
		java.sql.Date dat = new java.sql.Date(d.getTime());
		re.setDate(dat);
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			
		
		//insert statement
		String insert = new String("INSERT INTO indexedurls(url,indexdate) VALUES('"+re.getUrl()+"','"+dat.toString()+"')");
		
		
		//Statement um PrimKey zu kriegen
		String prim = new String("CALL IDENTITY()");
		
		// lock table category , set savepoint
		conn.setSavepoint("addurl");

		try
		{

			//ausfuehren des inserts
			st.executeUpdate(insert);

			//erzeugte id holen
			ResultSet rsi = st.executeQuery(prim);
			rsi.next();
			int identity = rsi.getInt(1);
			
			//returnbean fuellen
			re.setID(identity);
			
			conn.commit();
			
			return(re);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			log.error("AddUrl: "+ e);
			throw(e);
		}
		
	}


	/**
	 * Gibt alle URLs einer Kategorie zurueck
	 */
	public Vector<String> getAllUrlsInCategory(Connection conn, Integer category) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement("select i.url from indexedurls as i, urltocategory as u where u.originalcat = ? and u.url = i.id and u.originalcat = u.category");
		ps.setInt(1, category);

		Vector<String> v = new Vector<String>();
		try
		{
			ResultSet r = ps.executeQuery();
			while(r.next())
			{
				v.add(r.getString(1));
			}
			return v;
		}
		catch(SQLException e)
		{
			log.error("getAllUrlsInCategory: "+ e);
			throw(e);
		}
	}


	/**
	  *Hinzufuegen einer Url (nur Url in der Bean benoetigt)
	  *mit zugehoeriger Category(auch hier nur ID benoetigt)
	  *
	  *Die Funktion ueberprueft ob die Url existiert und 
	  *fuegt in dem Fall nur den Link auf die Kathegorie hinzu.
	  * 
	  *Es werden automatisch Links auf alle Elternkathegorien mit hinzugefuegt.
	  **/
	public urltabBean addUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException
	{
		//Bean fuer Return anlegen
		urltabBean re = urlbean;

		//Datum erzeugen mit den Werten von "jetzt"
		java.util.Date d = new java.util.Date();
		java.sql.Date dat = new java.sql.Date(d.getTime());
		re.setDate(dat);
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			
		
		//insert statement
		PreparedStatement insindex = conn.prepareStatement("INSERT INTO indexedurls(url,indexdate) VALUES(?, ?)");
		insindex.setString(1, re.getUrl());
		insindex.setString(2, dat.toString());
			
		//Statement um PrimKey zu kriegen
		String prim = new String("CALL IDENTITY()");
		
		// lock table category , set savepoint
		conn.setSavepoint("addurl");

		try
		{
			//erstmal kucken ob die URL schon in der DB ist
			urltabBean tmp;
			tmp = getUrl(conn, urlbean.getUrl());
			if(tmp== null) //URL ist noch nciht indiziert
			{
			
				//ausfuehren des URL inserts
				insindex.executeUpdate();

				//erzeugte id holen
				ResultSet rs = st.executeQuery(prim);
				rs.next();
				int identity = rs.getInt(1);
			
				//returnbean fuellen
				re.setID(identity);
			}
			else //URL ist schon in der DB
			{
				//wir muessen also die ausgelesene urltabBean zurueckliefern
				re = tmp;
			}
			
		//Verbindungen auf der Kathegorien auf URL setzen
			
			//erstmal alle Eltern auslesen
			java.util.Vector<categoryBean> catvec = findAllParents(conn, catbean);
			//die Kathegorie selbst in den Vector schreiben damit haben wir alle Kathegorien
			//zu denen die Url gehoert
			catvec.add(catbean);
			
			//auslesen ob die URL schon irgentwelschen Kathegorien angehoert
			String vorhandene = new String("SELECT * FROM urltocategory WHERE url = "+urlbean.getID());
			ResultSet rsi = st.executeQuery(vorhandene);
			//ueberpruefen ob die Url schon einer der Cats zugeordnet sind
			//und wenn ja diese aus dem Vector rausnehmen
			while(rsi.next())
			{
				for(int i=0; i<catvec.size(); i++)
				{
					if( catvec.get(i).getID() == rsi.getInt("category"))
					{
						catvec.remove(i);
					}
				}
			}
			
			
			PreparedStatement ins = conn.prepareStatement("INSERT INTO urltocategory(url,category,originalcat) VALUES(?,?,?)");
			//Alle Zuordnungen durchfueheren 
			java.util.Iterator it = catvec.iterator();
			while(it.hasNext())
			{
				categoryBean c = (categoryBean)it.next();
				ins.setInt(1,re.getID());
				ins.setInt(2,c.getID());
				ins.setInt(3,catbean.getID());
				//Verbindung auf Category setzen
				ins.executeUpdate();
			}	


			conn.commit();
			
			return(re);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			log.error("AddUrl: "+ e);
			throw(e);
		}
	}

	/**
	 * Hinzufuegen einer Category zu einer Url
	 * genau diese Category wird der URL zugeordnet(keine zuordnung der Parentkategorien)
	 */ 
	public void addCatToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException
	{

		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO Beans auch gefuellt			
		
		// lock table category , set savepoint
		conn.setSavepoint("addtourl");

		try
		{
			//Verbindung auf Category setzen
			PreparedStatement insert = conn.prepareStatement("INSERT INTO urltocategory(url,category,originalcat) VALUES(?,?,?)");
			insert.setInt(1,urlbean.getID());
			insert.setInt(2,catbean.getID());
			insert.setInt(3,catbean.getID());
			insert.executeUpdate();
			
			conn.commit();
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			log.error("AddCatToUrl: "+ e);
			throw(e);
		}
	}
	/**
	 * Alle Eltern zur Category finden 
	 * in der CategoryBean wird nur die ID benoetigt 
	 */ 
	public java.util.Vector<categoryBean> findAllParents(Connection conn, categoryBean catbean) throws SQLException
	{
				
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			

		//wir brauchen auf jeden left und right der category
		String getBeanInfo = new String("select left,right,root_id from category where id = "+catbean.getID());

		try
		{
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setLeft(rs.getInt("left"));
			catbean.setRight(rs.getInt("right"));
			catbean.setRootID(rs.getInt("root_id"));
			
			//find statement
			String find = new String("SELECT * FROM category where left < "+catbean.getLeft()+" and right > "+catbean.getRight() + " AND root_id = " + catbean.getRootID());
			ResultSet rsi = st.executeQuery(find);
			
			java.util.Vector<categoryBean> revec = new java.util.Vector<categoryBean>();

			//results in Return Array schreiben
			while(rsi.next())
			{
				categoryBean re = new categoryBean();
				
				re.setID(rsi.getInt(1));
				
				re.setRootID(rsi.getInt(2));
				
				re.setLeft(rsi.getInt(3));
				
				re.setRight(rsi.getInt(4));
				
				re.setCategory(rsi.getString(5));
				
				re.setBeschreibung(rsi.getString(6));
				
				if(!revec.add(re))
					log.error("an Vector adden fehlgeschlagen!");
			}
			
			return(revec);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("FindAllParents: "+ e);
			throw(e);
		}
	}

	/**
	 *	Parent zur Category finden 
	 * 	in der CategoryBean wird nur die ID benoetigt 
	 */ 
	public categoryBean findParent(Connection conn, categoryBean catbean) throws SQLException
	{



		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			

		//wir brauchen auf jeden left und right der category
		String getBeanInfo = new String("select left,right,root_id from category where id = "+catbean.getID());

		try
		{
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setLeft(rs.getInt("left"));
			catbean.setRight(rs.getInt("right"));
			catbean.setRootID(rs.getInt("root_id"));
			
			//find statement
			String find = new String("SELECT * FROM category where left < "+catbean.getLeft()+" and right > "+catbean.getRight() + " AND root_id = " + catbean.getRootID());
			ResultSet rsi = st.executeQuery(find);
			
			categoryBean re = new categoryBean();

			//results in Return Array schreiben
			while(rsi.next())
			{
				re.setID(rsi.getInt(1));
				
				re.setRootID(rsi.getInt(2));
				
				re.setLeft(rsi.getInt(3));
				
				re.setRight(rsi.getInt(4));
				
				re.setCategory(rsi.getString(5));
				
				re.setBeschreibung(rsi.getString(6));
				
			}
			
			return(re);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("DBLayerHSQL.FindParent: "+ e);
			throw(e);
		}

		
	}


	
	/**
	 * Hinzufuegen einer Category zu einer Url
	 * Es werden auch alle ElternCategorien mit zugeordnet
	 *
	 * Die IDs von Category und Url reichen aus ...
	 *
	 * Die Funktion erkennt schon vorhandene Verbindungen !
	 * 
	 */ 
	public void addCatwithParentsToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException
	{
		java.util.Vector<categoryBean> catvec = findAllParents(conn, catbean);
		catvec.add(catbean);
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		// lock table category , set savepoint
		conn.setSavepoint("addcptourl");

		try
		{
			String vorhandene = new String("SELECT * FROM urltocategory WHERE url = "+urlbean.getID());
			ResultSet rsi = st.executeQuery(vorhandene);
			//ueberpruefen ob die Url schon einer der Cats zugeordnet sind
			//und wenn ja diese aus dem Vector rausnehmen
			while(rsi.next())
			{
				for(int i=0; i<catvec.size(); i++)
				{
					if( catvec.get(i).getID() == rsi.getInt("category"))
					{
						catvec.remove(i);
					}
				}
			}
			
			
			//Alle Zuordnungen durchfueheren 
			java.util.Iterator it = catvec.iterator();
			while(it.hasNext())
			{
				categoryBean c = (categoryBean)it.next();
				//Verbindung auf Category setzen
				PreparedStatement ins = conn.prepareStatement("INSERT INTO urltocategory(url,category,originalcat) VALUES(?,?,?)");
				ins.setInt(1,urlbean.getID());
				ins.setInt(2,c.getID());
				ins.setInt(3,catbean.getID());
				ins.executeUpdate();
			
			}	
			conn.commit();
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			log.error("AddCatwithParentsToUrl: "+ e);
			throw(e);
		}
	}

	/**
	 * Category anhand bezeichnung suchen ..
	 * wir brauchen als Information in welchem Projekt nach der Kathegorie gesucht wird.
	 * (nur id muss in Bean stehen)
	 *
	 * wenn keine Kathegorie gefunden wird, ist return null
	 * 
	 * Es kann in mehreren Projekten auch gleichlautende Kathegorien geben.
	 * Wichtig:Gleichlautend bedeuted in so einem Fall nicht,
	 * dass es diesselbe Kathegorie ist!
	 *
	 */
	public categoryBean getCategoryByName(Connection conn, String catname, categoryBean rootbean) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT * FROM category WHERE root_id = "+rootbean.getID()+" AND	name = '"+catname+"'");
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				categoryBean re = new categoryBean(
						rsi.getInt("id"),
						rsi.getInt("root_id"),
						rsi.getString("name"),
						rsi.getString("beschreibung"),
						rsi.getInt("left"),
						rsi.getInt("right"),
						rsi.getInt("owner")
						);
				return(re);
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetCategorybyName: "+ e);
			throw(e);
		}

		
	}

	/**
	 * nach Url suchen -> liefert gefuellte urltabBean
	 * Returnwert ist Null wenn die Url nicht in der Datenbank ist
	 */ 
	public urltabBean getUrl(Connection conn, String url) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT id,url,indexdate FROM indexedurls WHERE url = '"+url+"'");
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				urltabBean re = new urltabBean(
						rsi.getString(2),
						rsi.getDate(3),
						rsi.getInt(1));
				return(re);
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetUrl ohne RootCategory: "+ e);
			throw(e);
		}
	}

	/**
	 * nach Kategorien der Url suchen
	 * @return ids der kategorien
	 * @param id der Url
	 */ 
	public Vector<Integer> getCatsOfUrl(Connection conn, Integer urlid) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT url,category FROM urltocategory WHERE url = '"+urlid.toString()+"'");
			ResultSet rsi = st.executeQuery(search);
			
			
			Vector<Integer> re = new Vector<Integer>();
			while(rsi.next())
			{
				re.add(rsi.getInt(2));
			}

			return(re);
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("DBLayerHSQL: getCatsOfUrl "+ e);
			throw(e);
		}
	}

	
	/**
	 * nach Url suchen -> liefert gefuellte urltabBean
	 * Returnwert ist Null wenn die Url nicht in der Datenbank ist
	 * oder nicht dem Mituebergebenen Projekt zugeordnet ist.
	 * (die categoryBean kann z.b. der Root des Projektes sein um 
	 * rauszufinden ob die URL in dem Projekt ist. 
	 * Es wird nur die ID der Kathegorie benoetigt)
	 */ 
	public urltabBean getUrl(Connection conn, String url, categoryBean rootbean) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT i.id,i.url,i.indexdate FROM indexedurls i,urltocategory u WHERE i.url = '"+url+"' AND i.id = u.url AND u.category = "+rootbean.getID());
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				urltabBean re = new urltabBean(
						rsi.getString("url"),
						rsi.getDate("indexdate"),
						rsi.getInt("id"));
				return(re);
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetUrl mit RootCategory: "+ e);
			throw(e);
		}
	}
	
	/**
	 * CategoryBaum auslesen ..
	 * Wir brauchen die ID der Rootbean, da wir fuer verschiedene Projekte
	 * verschiedene Baeume speichern koennen
	 * Zurueckgeliefert wird ein String,der die vollstaendigen XML Darstellung liefert!
	 */
	public String getCategoryTree(Connection conn,categoryBean rootbean) throws SQLException
	{
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			//wir ordnen die ergebnisse mal ein wenig vor 
			//String search = new String("SELECT * FROM category WHERE root_id = "+rootbean.getID()+ " GROUP BY left");

			String search = new String("SELECT a.id, a.left, a.right, a.name, a.beschreibung,count(*) AS level FROM category AS a, category AS b WHERE a.root_id = "+rootbean.getID()+ " AND b.root_id = "+rootbean.getID()+" AND a.left BETWEEN b.left AND b.right GROUP BY a.id, a.left, a.right, a.name, a.beschreibung ORDER BY a.left");

			//this.query(conn,search);
			
			ResultSet rsi = st.executeQuery(search);
			rsi.next();
			//startlevel im Baum
			int start=	rsi.getInt(6);
			String result = new String();
			result = buildTree(rsi,result,start);
			return(result);
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetCategoryTree: "+ e);
			throw(e);
		}
		
	}
	
	//hilfe fuer getCategoryTree
	private String buildTree(ResultSet rsi,String xmlreturn,int startlevel) throws SQLException
	{
			
		//muss immer neue Cat anfangen (name beschr id wird benoetigt)
		xmlreturn = beginCatXML(xmlreturn, rsi.getString(4),rsi.getString(5),rsi.getInt(1));
			
		//wenn der Baum noch ein Level weitergeht
		if(rsi.next())
		{
			//welches level haben wir den jetzt
			int currentlevel = rsi.getInt(6);
			
			//wenn ich ein level im Baum absteige brauchen wir eine neue Subcategory
			if(currentlevel > startlevel)
			{
				//subcats folgen
				xmlreturn = beginSubCatXML(xmlreturn);
				
				//rekursiv buildTree aufrufen
				xmlreturn = buildTree(rsi, xmlreturn,currentlevel);
			}
			if(currentlevel == startlevel)
			{
				//muessen den Geschwisterknoten schliessen
				xmlreturn = endCatXML(xmlreturn);
				//und jetzt wieder rekusiv weitermachen
				xmlreturn = buildTree(rsi, xmlreturn,currentlevel);
			}
			if(currentlevel < startlevel)
			{
				//wir muessen soviele kategorien und Subkategorien schliessen wie level im
				//Baum nach oben gesprungen wurden
				for(int i = currentlevel; i<startlevel;i++)
				{
					xmlreturn = endCatXML(xmlreturn);
					xmlreturn = endSubCatXML(xmlreturn);
				}
				//muessen den Geschwisterknoten schliessen
				xmlreturn = endCatXML(xmlreturn);
				//rekursiv buildTree aufrufen (jetzt kaeme sozusagen den Geschwisterknoten drann dessen Bruder geschlossen wurde)
				xmlreturn = buildTree(rsi, xmlreturn,currentlevel);
			}
		}
		else
		{
			//wenn es der letzte eintrag ist muessen wir noch alles bis rootcat schliessen
			//und das level der rootcat ist immer eins
			for(int i=1; i<startlevel;i++)
			{
					xmlreturn = endCatXML(xmlreturn);
					xmlreturn = endSubCatXML(xmlreturn);
			}

				xmlreturn = endCatXML(xmlreturn);
		}
		return(xmlreturn);
	}
	//nur hilfe fuer  buildTree
	private String beginCatXML(String bisher,String name, String beschreibung,int id)
	{
		String re = new String(bisher +" <category> <name>"+name+"</name> <description>"+beschreibung+"</description> <id>"+id+"</id> ");
		return re;		
	}
	//nur hilfe fuer  buildTree
	private String endCatXML(String bisher)
	{
		return(bisher +" </category> "); 		
	}
	//nur hilfe fuer  buildTree
	private String beginSubCatXML(String bisher)
	{
		return(bisher +" <subcategories> "); 		
	}
	//nur hilfe fuer  buildTree
	private String endSubCatXML(String bisher)
	{
		return(bisher +" </subcategories> "); 		
	}

	/**
	 * Liefert die Versionsnummer fuer den Baum der zu diesem RootKnoten gehoert.
	 * Achtung geht nur mit wirklicher RootKategorie
	 * 
	 */ 
	public Integer getVersionNumber(Connection conn,categoryBean rootbean) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT version FROM projectdata WHERE rootid = "+rootbean.getID());
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				return(rsi.getInt(1));
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetVersionNumber: "+ e);
			throw(e);
		}
	}
	/**
	 * Liefert Tuple <Integer,Integer> 
	 * der erste int ist die ID der RootCAT der Zweite ist die Version des Baumes
	 * @deprecated as of version 0.3
	 */
	@Deprecated public HashSet<Tuple<Integer,Integer>> getAllVersions(Connection conn) throws SQLException
	{
		PreparedStatement st = null;
		st = conn.prepareStatement("SELECT rootid,version FROM projectdata WHERE guestread = ?");    // erstelle statements
		st.setBoolean(1,true);
		try
		{
			ResultSet rsi = st.executeQuery();

			Tuple<Integer,Integer> versiontuple;
			HashSet<Tuple<Integer,Integer>> versionlist = new HashSet<Tuple<Integer,Integer>>();
			while(rsi.next())
			{
				versiontuple = new Tuple<Integer,Integer>(rsi.getInt(1), rsi.getInt(2));
				versionlist.add(versiontuple);
			}
			//TODO hier soll mal richtig uebergeben werden
			Integer userid = null;
			if(userid != null)
			{
				userRightBean ubean = getRights(conn,userid);
				HashMap uproj = ubean.getUserProjects();
				HashMap aproj = ubean.getAdminProjects();

				java.util.Collection  projvec = uproj.values();
				Iterator vecit = projvec.iterator();
				projectdataBean db;
				while(vecit.hasNext())
				{
					db = (projectdataBean)vecit.next();
					versiontuple = new Tuple<Integer,Integer>(db.getRootID(),db.getVersion());
					versionlist.add(versiontuple);

				}

				java.util.Collection  projcol = aproj.values();
				Iterator colit = projcol.iterator();
				while(colit.hasNext())
				{
					db = (projectdataBean)colit.next();
					versiontuple = new Tuple<Integer,Integer>(db.getRootID(),db.getVersion());
					versionlist.add(versiontuple);

				}

			}

		
			return(versionlist);
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetVersionNumber: "+ e);
			throw(e);
		}
	}

	/**
	 * Liefert Map <Integer,Integer> 
	 * der erste int ist die ID der RootCAT der Zweite ist die Version des Baumes
	 * Also alle Versionsnummer der Projecte mit ProjektID
	 */
	public Map<Integer,Integer> getAllVersion(Connection conn) throws SQLException
	{
		PreparedStatement st = null;
		st = conn.prepareStatement("SELECT rootid,version FROM projectdata");   
		try
		{
			ResultSet rsi = st.executeQuery();

			HashMap<Integer,Integer> versionlist = new HashMap<Integer,Integer>();
			while(rsi.next())
			{
				versionlist.put(rsi.getInt("rootid"),rsi.getInt("version"));
			}

		
			return(versionlist);
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetVersionNumber: "+ e);
			throw(e);
		}
	}


	/**
	 * Liest alle WurzelKategorien aus.
	 * (ob diese nun als Projekte oder sonstewas indentifiziert werden)
	 */
	public Vector<categoryBean> getAllRootCats(Connection conn)throws SQLException
	{
		String getcats = new String("select id,root_id,left,right,name,beschreibung,owner from category where id=root_id");
	
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			ResultSet rsi = st.executeQuery(getcats);
			Vector<categoryBean> result = new Vector<categoryBean>();
			
			while(rsi.next())
			{
				categoryBean cat = new categoryBean( 
						rsi.getInt(1), 
						rsi.getInt(2),
						rsi.getString(5),
						rsi.getString(6),
						rsi.getInt(3),
						rsi.getInt(4),
						rsi.getInt("owner"));
				result.add(cat);
			}
			return(result);
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetVersionNumber: "+ e);
			throw(e);
		}
		
	}

	/**
	 * liefert alle Kinder der Kategorie
	 * !die categoryBean muss left und right enthalten
	 * 
	 */
	public Vector<categoryBean> getAllChildCategorys(Connection conn, categoryBean rootbean)throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			


		try
		{
			
			//find statement
			String find = new String("SELECT * FROM category where left > "+rootbean.getLeft()+" and right < "+rootbean.getRight() + " AND root_id = " + rootbean.getID());
			ResultSet rsi = st.executeQuery(find);
			
			java.util.Vector<categoryBean> revec = new java.util.Vector<categoryBean>();

			//results in Return Array schreiben
			while(rsi.next())
			{
				categoryBean re = new categoryBean();
				
				re.setID(rsi.getInt(1));
				
				re.setRootID(rsi.getInt(2));
				
				re.setLeft(rsi.getInt(3));
				
				re.setRight(rsi.getInt(4));
				
				re.setCategory(rsi.getString(5));
				
				re.setBeschreibung(rsi.getString(6));
				
				if(!revec.add(re))
					log.error("an Vector adden fehlgeschlagen!");
			}
			
			return(revec);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetAllChildCats: "+ e);
			throw(e);
		}
	
	}
	
	/**
	 * liefert ganze categoryBean zu der Id
	 *
	 */
	public categoryBean getCatByID(Connection conn, int id) throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT * FROM category WHERE id = "+id);
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				categoryBean re = new categoryBean(
						rsi.getInt("id"),
						rsi.getInt("root_id"),
						rsi.getString("name"),
						rsi.getString("beschreibung"),
						rsi.getInt("left"),
						rsi.getInt("right"),
						rsi.getInt("owner"));
				return(re);
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("GetCategorybyName: "+ e);
			throw(e);
		}
	}

	/**
	 * gibt true zurueck falls die DB schon existiert
	 */
	public boolean initialized(Connection conn)throws SQLException
	{
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		
		try
		{
			String search = new String("SELECT * FROM category");
			ResultSet rsi = st.executeQuery(search);
			
			if(rsi.next())
			{
				return(true);
			}
			else
				return false;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("initialized: "+ e);
			throw(e);
		}
	}

	//////////////////////////////
	//
	//        N E U Milestone 3
	//
	//////////////////////////////

	/**
	 * Adminprojekte des Users
	 * @return Liste der projectadminBeans des users
	 * @param userid des Users
	 */ 
	public Vector<tfusertoprojectBean> getAdminProjectsForUser(Connection conn, Integer userid) throws SQLException
	{
		PreparedStatement st = null;
		st = conn.prepareStatement("Select * from projectadmin where userid = ? AND isadmin = ?");   
		try
		{	
			st.setInt(1, userid.intValue());
			st.setBoolean(2, true);

			ResultSet rsi = st.executeQuery();	
			
			java.util.Vector<tfusertoprojectBean> revec = new java.util.Vector<tfusertoprojectBean>();

			//results in Return Array schreiben
			while(rsi.next())
			{
				tfusertoprojectBean re = new tfusertoprojectBean();
				
				re.setID(rsi.getInt("id"));
				re.setRootID(rsi.getInt("rootid"));
				re.setUserID(rsi.getInt("userid"));
				re.setAdmin(rsi.getBoolean("isadmin"));
				
				if(!revec.add(re))
					log.error("an Vector adden fehlgeschlagen!");
			}
			
			return(revec);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getAdminProjectsforUser: "+ e);
			throw(e);
		}
	
	
	}

	/**
	 * erzeugt eine tfuserBean aus der aktuellen Zeile des ResultSets
	 * @param resultset 
	 * @return tfuserBean
	 */
	private tfuserBean create_tfuserBean_from_ResultSet(ResultSet rsi) throws SQLException
	{
		tfuserBean re = new tfuserBean();

		re.setID(rsi.getInt("id"));
		re.setUsername(rsi.getString("username"));
		re.setPass(rsi.getString("pass"));
		re.setSessionkey(rsi.getString("sessionkey"));
		re.setServeradmin(rsi.getBoolean("serveradmin"));
		re.setLastaction(rsi.getTimestamp("lastaction"));

		return re;
	}


	/**
	 * User by ID
	 * @return tfuserBean des Users
	 * @param userid des Users
	 */ 
	public tfuserBean getUserByID(Connection conn, Integer userid) throws SQLException
	{
		PreparedStatement st = null;
		st = conn.prepareStatement("Select * from tfuser where id = ?");   
		try
		{	
			st.setInt(1, userid.intValue());

			ResultSet rsi = st.executeQuery();	
			
			tfuserBean re = new tfuserBean();
			
			if(rsi.next())
			{
				re = create_tfuserBean_from_ResultSet(rsi);
			}	
			return(re);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getUserByID: "+ e);
			throw(e);
		}

	}
	
	/**
	 * User by name
	 * @return tfuserBean des Users
	 * @param String name  des Users
	 */ 
	public tfuserBean getUserByName(Connection conn, String name) throws SQLException
	{
		PreparedStatement st = conn.prepareStatement("Select * from tfuser where username = ?");   
		try
		{	
			st.setString(1, name);

			ResultSet rsi = st.executeQuery();	
			
			tfuserBean re = new tfuserBean();
			
			if(rsi.next()) // first call to next positions to 1st row!
			{
				re.setID(rsi.getInt("id"));
				re.setUsername(rsi.getString("username"));
				re.setPass(rsi.getString("pass"));
				re.setSessionkey(rsi.getString("sessionkey"));
				re.setServeradmin(rsi.getBoolean("serveradmin"));
				re.setLastaction(rsi.getTimestamp("lastaction"));
				return(re);
			}	
			else
				return null;
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getUserByName: "+ e);
			throw(e);
		}

	}


	/**
	 * Liefert alle indizierten Dokumente, deren Indizierung
	 * laenger her ist als das uebergebene Datum
	 *
	 * @return Vector<urltabBean> die indizierten URLs
	 * @param Date Datum 
	 */ 
	public Vector<urltabBean> getOlderDocs(Connection conn, java.util.Date datum) throws SQLException
	{
		java.sql.Date dat = new java.sql.Date(datum.getTime());
		

		PreparedStatement st = null;
		st = conn.prepareStatement("Select * from indexedurls where indexdate < ?");   
		try
		{	
			st.setDate(1, dat);

			ResultSet rsi = st.executeQuery();	
			
			java.util.Vector<urltabBean> revec = new java.util.Vector<urltabBean>();

			//results in Return Array schreiben
			while(rsi.next())
			{
				urltabBean re = new urltabBean();
				
				re.setID(rsi.getInt("id"));
				
				re.setUrl(rsi.getString("url"));
				re.setDate(rsi.getDate("indexdate"));
				
				
				if(!revec.add(re))
					log.error("an Vector adden fehlgeschlagen!");
			}
			
			return(revec);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getOlderDocs: "+ e);
			throw(e);
		}

	}

	
	/**
	 * Setz das Datum eines Eintrages in der indexedUrls Tabelle auf
	 * das jetzige Serverdatum
	 * 
	 */ 
	public void refreshIndexDate(Connection conn, Integer id) throws SQLException
	{
		java.sql.Date dat = new java.sql.Date(new java.util.Date().getTime());
		

		PreparedStatement st = null;
		st = conn.prepareStatement("UPDATE indexedurls SET indexdate = ? where id = ?");   
		try
		{	
			st.setDate(1, dat);
			st.setInt(2, id.intValue());

			//ausfuehren der Updates 
			st.executeUpdate();	
			
			conn.commit();
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("refreshIndexDate: "+ e);
			throw(e);
		}

	}


	/**
	 * User by Sessionkey (null if sessionkey is not found)
	 * @return tfuserBean des Users
	 * @param sessionkey 
	 */ 
	public tfuserBean getUserBySessionkey(Connection conn, String sessionkey) throws SQLException
	{
		
		PreparedStatement st = null;
		st = conn.prepareStatement("Select * from tfuser where sessionkey = ?");   
		try
		{	
			st.setString(1,sessionkey );

			ResultSet rsi = st.executeQuery();	
			
			tfuserBean re = new tfuserBean();
			
			if(rsi.next())
			{
				re.setID(rsi.getInt("id"));
				re.setUsername(rsi.getString("username"));
				re.setPass(rsi.getString("pass"));
				re.setSessionkey(rsi.getString("sessionkey"));
				re.setServeradmin(rsi.getBoolean("serveradmin"));
				re.setLastaction(rsi.getTimestamp("lastaction"));
				return(re);
			}	
			else
				return(null);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getUserByID: "+ e);
			throw(e);
		}

	}

	/**
	 * Aktualisiere lastaction (setzt lastaction auf jetzige Zeit/Datum
	 * @param sessionkey 
	 */ 
	public void updateLastActionForUserID(Connection conn, Integer userid) throws SQLException
	{
		java.sql.Timestamp dat = new java.sql.Timestamp(new java.util.Date().getTime());
		

		PreparedStatement st = null;
		st = conn.prepareStatement("UPDATE tfuser SET lastaction = ? where id = ?");   
		try
		{	
			st.setTimestamp(1, dat);
			st.setInt(2, userid.intValue());

			//ausfuehren der Updates 
			st.executeUpdate();	
			
			
			conn.commit();
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("updateLastActionForUserID: "+ e);
			throw(e);
		}

	}
	
	/**
	 * beim anlegen neuer Session anlegen 
	 * Sessionkey, lastaccessed fuer einen user in die Datenbank
	 * 
	 */ 
	public void newSession(Connection conn,String user, String pass, String sessionkey, Date last) throws SQLException
	{
		java.sql.Timestamp dat = new java.sql.Timestamp(last.getTime());
		

		PreparedStatement st = null;
		st = conn.prepareStatement("UPDATE tfuser SET sessionkey = ?,lastaction = ? where username = ? and pass = ?");   
		try
		{
			st.setString(1,sessionkey);
			st.setTimestamp(2, dat);
			st.setString(3, user);
			st.setString(4, pass);

			//ausfuehren der Updates 
			st.executeUpdate();	
			
			conn.commit();
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("updateLastActionForUserID: "+ e);
			throw(e);
		}
	}

	/**
	 * Erzeuge einen neuen User
	 * @return tfuserBean mit neu vergebener ID aus Datenbank
	 * @param tfuserBean
	 */ 
	public tfuserBean createNewUser(Connection conn, tfuserBean tfuser) throws SQLException
	{
		//TODO existenz der Usernamen pruefen
		PreparedStatement pst = null;
		pst = conn.prepareStatement("INSERT INTO tfuser (username,pass,serveradmin) values (?,?,?)");   
		Statement st = null;
		st = conn.createStatement();
				
		conn.setSavepoint("createnewuser");
		try
		{	
			pst.setString(1, tfuser.getUsername());
			pst.setString(2, tfuser.getPass());
			if(tfuser.getServeradmin() != null)
			{	
				pst.setBoolean(3,tfuser.getServeradmin().booleanValue());
			}
			else
			{
				pst.setBoolean(3,false);
			}
			//ausfuehren der Updates 
			pst.executeUpdate();	
			ResultSet rs = st.executeQuery("CALL IDENTITY()");
			rs.next();
			int identity = rs.getInt(1);
			tfuser.setID(identity);
			
			conn.commit();
			return(tfuser);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			log.error("updateLastActionForUserID: "+ e);
			throw(e);
		}



	}

	/**
	 * Gib einem Nutzer adminrechte fuer ein Projekt
	 * Achtung der Nutzer muss bereits dem Project angehoeren!
	 * @param userid
	 * @param rootid
	 * @return boolean true es wurde was geaendert, false kein Eintrag hat stattgefunden
	 */ 
	public boolean addUserToAdminsOfProject(Connection conn, Integer userid, Integer rootid) throws SQLException
	{
		PreparedStatement pst = null;
		pst = conn.prepareStatement("UPDATE tfusertoproject SET isadmin = true WHERE userid = ? AND rootid = ?");   

		try
		{
			//Values in statements
			pst.setInt(1, userid.intValue());
			pst.setInt(2, rootid.intValue());
			//ausfuehren der Updates 
			if(pst.executeUpdate()> 0)	
				return(true);
			else
				return(false);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("addUserToAdminsOfProject: "+ e);
			throw(e);
		}


	}

	/**
	 * Einen Nutzer dem Projekt zuordnen
	 * @param userid
	 * @param rootid 
	 *@return true User wurde Eingetragen, false kein Eintrag
	 */ 
	public boolean addUserToProject(Connection conn, Integer userid, Integer rootid) throws SQLException
	{
		PreparedStatement check = null;
		check = conn.prepareStatement("SELECT count(*) FROM tfusertoproject WHERE userid = ? AND rootid = ?");
		check.setInt(1, userid.intValue());
		check.setInt(2, rootid.intValue());


		PreparedStatement pst = null;
		pst = conn.prepareStatement("INSERT INTO tfusertoproject (userid,rootid,isadmin) VALUES(?,?,?)");   
			pst.setInt(1, userid.intValue());
			pst.setInt(2, rootid.intValue());
			pst.setBoolean(3, false);

		try
		{
			int count = 0;
			ResultSet rsi = check.executeQuery();	
			if(rsi.next())
			{
				count =	rsi.getInt(1);
			}
			if(count < 1)
			{
				if(pst.executeUpdate()>0)
					return true;
				else
					return false;
			}
			return true;
			
		}
		catch(SQLException e)
		{

			//TODO LoggMessage statt print
			log.error("addUserToProject: "+ e);
			throw(e);
		}


	}

	/**
	 * Setzt die Rechte fuer ein Project neu
	 * 
	 * @param projectdataBean
	 */ 
	public void setProjectdata(Connection conn, projectdataBean projectdata) throws SQLException
	{

		PreparedStatement st = null;
		st = conn.prepareStatement("UPDATE projectdata SET useruseradd =?, userurledit =?, usercatedit =?, useraddurl =?, useraddcat =?, guestread =?, guesturledit =?, guestcatedit =?, guestaddurl = ?, guestaddcat = ?  where rootid = ?");   
		try
		{	
			st.setBoolean(1,projectdata.getUserUseradd().booleanValue());
			st.setBoolean(2,projectdata.getUserUrledit().booleanValue());
			st.setBoolean(3,projectdata.getUserCatedit().booleanValue());
			st.setBoolean(4,projectdata.getUserAddurl().booleanValue());
			st.setBoolean(5,projectdata.getUserAddcat().booleanValue());
			st.setBoolean(6,projectdata.getGuestRead().booleanValue());
			st.setBoolean(7,projectdata.getGuestUrledit().booleanValue());
			st.setBoolean(8,projectdata.getGuestCatedit().booleanValue());
			st.setBoolean(9,projectdata.getGuestAddurl().booleanValue());
			st.setBoolean(10,projectdata.getGuestAddcat().booleanValue());
			st.setInt(11,projectdata.getRootID().intValue());

			//ausfuehren der Updates 
			st.executeUpdate();	
			conn.commit();
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("setProjectdata: "+ e);
			throw(e);
		}


	}

	/**
	 * Gibt die Rechte fuer ein Project zurueck
	 * 
	 * @param rootid
	 * @return projectdataBean
	 */ 
	public projectdataBean getProjectdata(Connection conn, Integer rootid) throws SQLException
	{
		PreparedStatement st = null;
		st = conn.prepareStatement("Select * from projectdata where rootid = ?");   
		try
		{	
			st.setInt(1, rootid.intValue());

			ResultSet rsi = st.executeQuery();	
			
			projectdataBean re = new projectdataBean();
			if(rsi.next())
			{
				re.setRootID(rootid);	
				re.setID(new Integer(rsi.getInt("id")));
				re.setVersion(new Integer(rsi.getInt("version")));	
				re.setUserUseradd(new Boolean(rsi.getBoolean("useruseradd")));	
				re.setUserUrledit(new Boolean(rsi.getBoolean("userurledit")));	
				re.setUserCatedit(new Boolean(rsi.getBoolean("usercatedit")));	
				re.setUserAddurl(new Boolean(rsi.getBoolean("useraddurl")));	
				re.setUserAddcat(new Boolean(rsi.getBoolean("useraddcat")));	
				re.setGuestRead(new Boolean(rsi.getBoolean("guestread")));	
				re.setGuestUrledit(new Boolean(rsi.getBoolean("guesturledit")));	
				re.setGuestCatedit(new Boolean(rsi.getBoolean("guestcatedit")));	
				re.setGuestAddurl(new Boolean(rsi.getBoolean("guestaddurl")));	
				re.setGuestAddcat(new Boolean(rsi.getBoolean("guestaddcat")));	
			}
			
			return(re);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getProjectdata: "+ e);
			throw(e);
		}


	}
	/**
	 * Alle Rechte eines Users auslesen
	 * Achtung : Admin und User werden in der UserRightBean getrennt behandelt
	 * d.h. wenn man Admin ist, ist man nciht mehr als User gelistet
	 *
	 * @param userid
	 * @return
	 */
	public userRightBean getRights(Connection conn, Integer userid) throws SQLException
	{
		PreparedStatement stuser = null;
		//stuser = conn.prepareStatement("Select rootid from projectdata where rootid = (SELECT rootid FROM tfusertoproject WHERE userid = ? AND isadmin =?)");
		stuser = conn.prepareStatement("SELECT rootid FROM tfusertoproject WHERE userid = ? AND isadmin =?");
		stuser.setInt(1, userid.intValue());
		stuser.setBoolean(2, false);

		try
		{	
			ResultSet rsi = stuser.executeQuery();	
			
			HashMap<Integer,projectdataBean> pmap = new HashMap<Integer,projectdataBean>();
			while(rsi.next())
			{
				pmap.put(rsi.getInt("rootid"),SessionData.projectdata.get(rsi.getInt("rootid")));
			}
			
			stuser.setBoolean(2, true);
			rsi = stuser.executeQuery();
			HashMap<Integer,projectdataBean> adminmap = new HashMap<Integer,projectdataBean>();
			while(rsi.next())
			{
				adminmap.put(rsi.getInt("rootid"),SessionData.projectdata.get(rsi.getInt("rootid")));
			}
			userRightBean rbean = new userRightBean(pmap,adminmap);
			
			return(rbean);
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getRights: "+ e);
			throw(e);
		}



	}

	/**
	 * projectdata zu einer Kategorie holen 
	 * (nicht zu einem Project !)
	 * @param catid
	 * @return projectdataBean
	 */
	public projectdataBean getProjectDataToCat(Connection conn, Integer catid) throws SQLException
	{
		PreparedStatement st = null;
		//st = conn.prepareStatement("SELECT * FROM projectdata WHERE rootid = (SELECT root_id FROM category WHERE id = ?)");
		st = conn.prepareStatement("SELECT * FROM projectdata as pd, category as c WHERE pd.rootid = c.root_id and c.id = ?");
		st.setInt(1,catid.intValue());
	try
		{	

			ResultSet rsi = st.executeQuery();	
			
			projectdataBean re = new projectdataBean();
			if(rsi.next())
			{
				re.setRootID(new Integer(rsi.getInt("rootid")));	
				re.setID(new Integer(rsi.getInt("id")));
				re.setVersion(new Integer(rsi.getInt("version")));	
				re.setUserUseradd(new Boolean(rsi.getBoolean("useruseradd")));	
				re.setUserUrledit(new Boolean(rsi.getBoolean("userurledit")));	
				re.setUserCatedit(new Boolean(rsi.getBoolean("usercatedit")));	
				re.setUserAddurl(new Boolean(rsi.getBoolean("useraddurl")));	
				re.setUserAddcat(new Boolean(rsi.getBoolean("useraddcat")));	
				re.setGuestRead(new Boolean(rsi.getBoolean("guestread")));	
				re.setGuestUrledit(new Boolean(rsi.getBoolean("guesturledit")));	
				re.setGuestCatedit(new Boolean(rsi.getBoolean("guestcatedit")));	
				re.setGuestAddurl(new Boolean(rsi.getBoolean("guestaddurl")));	
				re.setGuestAddcat(new Boolean(rsi.getBoolean("guestaddcat")));	
			}
			
			return(re);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getProjectdataToCat: "+ e);
			throw(e);
		}

	}


	/**
	 * Sortiert alle Kategorien aus auf die kein ReadAccess besteht
	 * userid = null -> er ist nur gast
	 * liefert ein array mit den Elementen auf die ReadAccess besteht
	 */
	
	public int[] checkCatReadAccess(Connection conn, int[] category, Integer userid) throws SQLException
	{
		try
		{
			userRightBean userright = null;
			if(userid != null)
				userright = getRights(conn, userid);


			HashSet<Integer> setint = new HashSet<Integer>();
			projectdataBean pbean;
		
			for(int i=0; i<category.length; i++)
			{
				//auf GuestRead pruefen
				pbean = getProjectDataToCat(conn,category[i]);
				if(pbean.getGuestRead().booleanValue())
					setint.add(category[i]);

				//auf User pruefen
				if(userid != null)
				{
					categoryBean catbean = getCatByID(conn,category[i]);
					Integer projectid = catbean.getRootID();
					if(userright.isUser(projectid))
						setint.add(category[i]);
					if(userright.isAdmin(projectid))
						setint.add(category[i]);
				}
			}	
			Integer[] iarray = new Integer[setint.size()] ;
			setint.toArray(iarray);
			int[] returnarray = new int[iarray.length];
			for(int i=0; i<iarray.length; i++)
			{
				returnarray[i] = iarray[i].intValue();
			}
			return(returnarray);	
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("checkCatReadAccess : "+ e);
			throw(e);
		}


	}


	public java.util.concurrent.ConcurrentHashMap<Integer, projectdataBean> getAllProjectData(Connection conn) throws SQLException
	{
		java.util.concurrent.ConcurrentHashMap<Integer, projectdataBean> retmap = new java.util.concurrent.ConcurrentHashMap<Integer, projectdataBean>();
		PreparedStatement st = null;
		st = conn.prepareStatement("SELECT * FROM projectdata");
		try
		{	

			ResultSet rsi = st.executeQuery();	
			
			while(rsi.next())
			{
				projectdataBean re = new projectdataBean();
				re.setRootID(new Integer(rsi.getInt("rootid")));	
				re.setID(new Integer(rsi.getInt("id")));
				re.setVersion(new Integer(rsi.getInt("version")));	
				re.setUserUseradd(new Boolean(rsi.getBoolean("useruseradd")));	
				re.setUserUrledit(new Boolean(rsi.getBoolean("userurledit")));	
				re.setUserCatedit(new Boolean(rsi.getBoolean("usercatedit")));	
				re.setUserAddurl(new Boolean(rsi.getBoolean("useraddurl")));	
				re.setUserAddcat(new Boolean(rsi.getBoolean("useraddcat")));	
				re.setGuestRead(new Boolean(rsi.getBoolean("guestread")));	
				re.setGuestUrledit(new Boolean(rsi.getBoolean("guesturledit")));	
				re.setGuestCatedit(new Boolean(rsi.getBoolean("guestcatedit")));	
				re.setGuestAddurl(new Boolean(rsi.getBoolean("guestaddurl")));	
				re.setGuestAddcat(new Boolean(rsi.getBoolean("guestaddcat")));	

				retmap.put(re.getRootID(),re);
			}
			
			return(retmap);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getProjectDataToCat: "+ e);
			throw(e);
		}
	}


	/**
	 * Einen Nutzer aus dem Projekt entferenen
	 * @param userid
	 * @param rootid 
	 */ 
	public void removeFromProject(Connection conn, Integer userid, Integer rootid) throws SQLException
	{

		PreparedStatement pst = null;
		pst = conn.prepareStatement("delete FROM tfusertoproject WHERE userid=? AND rootid=?");   
			pst.setInt(1, userid.intValue());
			pst.setInt(2, rootid.intValue());

		try
		{
			pst.executeUpdate();
		    conn.commit();	
		}
		catch(SQLException e)
		{

			//TODO LoggMessage statt print
			log.error("removeUserFromProject: "+ e);
			throw(e);
		}

	}

	/**
	 * User des Projekts zurueckgeben
	 * @param projectid
	 *@return liste der user
	 */ 
	public Vector<Tuple<tfuserBean,Boolean>> getUsersOfProject(Connection conn, Integer projectid) throws SQLException
	{
		PreparedStatement check = conn.prepareStatement("SELECT tf.*, proj.isadmin FROM tfuser as tf, tfusertoproject as proj WHERE proj.rootid = ? and proj.userid = tf.id");
		check.setInt(1, projectid);

		try
		{
			ResultSet rsi = check.executeQuery();	
			
			Vector ret = new Vector<Tuple<tfuserBean,Boolean>>();
			while(rsi.next())
			{
				tfuserBean user = create_tfuserBean_from_ResultSet(rsi);
				Boolean role = new Boolean(rsi.getBoolean("isadmin"));
				
				ret.add( new Tuple<tfuserBean,Boolean>(user,role));
			}
			return ret;
		}
		catch(SQLException e)
		{

			//TODO LoggMessage statt print
			log.error("getUsersOfProject: "+ e);
			throw(e);
		}


	}

	/**
	 * Eintrag aus Kategorie loeschen
	 * @param url
	 * @param category
	 * @return true/false - je nachdem ob url und cat gefunden wurden oder nicht
	 */ 
	public Boolean removePage(Connection conn, Integer category, String url) throws SQLException
	{
		PreparedStatement check = conn.prepareStatement("delete from urltocategory where urltocategory.originalcat = ? and urltocategory.url = (select id from indexedurls where indexedurls.url = ?)");
		check.setInt(1, category);
		check.setString(2, url);

		try
		{
			int i = check.executeUpdate();	
			conn.commit();
			
			if(i > 0)
			{	return new Boolean(true);
			}
			return new Boolean(false);
		}
		catch(SQLException e)
		{

			//TODO LoggMessage statt print
			log.error("removePage: "+ e);
			throw(e);
		}


	}

	/**
	 * URL aus indexedurls loeschen
	 * @param url
	 * @return true/false - je nachdem ob url und cat gefunden wurden oder nicht
	 */ 
	public Boolean deleteIndexedUrl(Connection conn, String url) throws SQLException
	{
		PreparedStatement check = conn.prepareStatement("delete from indexedurls where url = ?");
		check.setString(1, url);

		try
		{
			int i = check.executeUpdate();	
			conn.commit();
			
			if(i > 0)
			{	return new Boolean(true);
			}
			return new Boolean(false);
		}
		catch(SQLException e)
		{

			//TODO LoggMessage statt print
			log.error("deleteIndexedUrl: "+ e);
			throw(e);
		}


	}

	/**
	 * alle kategorien des projekts mit der uebergebenen url zurueckgeben
	 * @param url
	 * @param projectid 
	 * @return Vektor<Integer> der gefunden kategorien (distinct)
	 */ 
	public Vector<Integer> getCatsWithUrlInProject(Connection conn, String url, Integer projectid) throws SQLException
	{
		PreparedStatement check = conn.prepareStatement("select distinct u.originalcat from urltocategory as u, category as c, indexedurls as i where c.root_id = ? and c.id = u.category and u.url = i.id and i.url = ?");
		check.setInt(1, projectid);
		check.setString(2, url);

		try
		{
			ResultSet r = check.executeQuery();	
			Vector<Integer> v = new Vector<Integer>();
			while(r.next())
			{
				v.add(r.getInt(1));
			}
			return v;

		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			log.error("getCatsWithUrlInProject: "+ e);
			throw(e);
		}
	}
}
