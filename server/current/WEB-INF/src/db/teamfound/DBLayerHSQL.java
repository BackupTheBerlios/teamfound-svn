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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Vector;

import config.Config;

public class DBLayerHSQL implements DBLayer
{

	private Config tfconfig;
	
	public DBLayerHSQL(Config c)
	{
		tfconfig = c;
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
		String dblocation = new String("jdbc:hsqldb:file:"+tfconfig.getConfValue("tfpath")+"/db/"+database+";ifexists=true");
	
		
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
		String dblocation = new String("jdbc:hsqldb:file:"+tfconfig.getConfValue("tfpath")+"/db/"+database);
	
		
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
				System.out.println("error changepassword ");
			

			//User anlegen
			String cuser = new String("CREATE USER "+user+" PASSWORD "+pass);
			if(!update(c,cuser))
					System.out.println("error bei create User ");
			
			//create Category table
			String sqlcreate = new String("CREATE TABLE category (id INTEGER IDENTITY,root_id INTEGER, left INTEGER, right INTEGER, name VARCHAR,beschreibung VARCHAR)");			
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);

			//create URL table
			sqlcreate = "CREATE TABLE indexedurls (id INTEGER IDENTITY,url VARCHAR,indexdate DATE)";
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);

			//create ForeignKeyTable Url <-> Category
			sqlcreate = "CREATE TABLE urltocategory (id INTEGER IDENTITY,url INTEGER,category INTEGER, FOREIGN KEY (category) REFERENCES category(id), FOREIGN KEY (url) REFERENCES indexedurls(id))";
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);

			//create ForeignKeyTable Url <-> Category
			//nicht jede Category sondern nur jede root Cat braucht eine Version
			sqlcreate = "CREATE TABLE categoryversion (id INTEGER IDENTITY,rootid INTEGER,version INTEGER, FOREIGN KEY (rootid) REFERENCES category(id))";
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);
			
			
			//Grant rights to user
			
			cuser=("GRANT ALL ON category TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");
									
			cuser=("GRANT ALL ON indexedurls TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");
			
			cuser=("GRANT ALL ON urltocategory TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");

			cuser=("GRANT ALL ON categoryversion TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");
			
			c.commit();
	
		}
		catch (SQLException ex2)
		{
			c.rollback();
			//existiert wohl schon 
			//dann brauch ich auch nichts anlegen
			System.out.println("Fehler beim DB initialisieren" +ex2);
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
	 * 
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

			//update der categoryversion
			String updateCatVersion = new String("UPDATE categoryversion SET version = version+1 WHERE rootid = "+rootid);
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
			System.out.println("AddCategory :"+e);
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
		//TODO ist die Bean auch gefuellt			
		String insert = new String("INSERT INTO category(left,right,name,beschreibung) VALUES(1,2,'"+catbean.getCategory()+"','"+catbean.getBeschreibung()+"')");
		
		//returnbean fuellen
		re.setLeft(1);
		re.setRight(2);
		
		//Statement um PrimKey zu kriegen
		String prim = new String("CALL IDENTITY()");
		
		// lock table category , set savepoint
		conn.setSavepoint("addrootcot");

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
		    re.setRootID(identity);	
			
			//update von root_id
			String upd = new String("UPDATE category SET root_id = " +identity+ " WHERE id =" +identity);
			st.executeUpdate(upd);			

			//neuer Eintrag in CategoryVersionTabelle
			insert = new String("INSERT INTO categoryversion(rootid,version) VALUES("+identity+",1)");
			st.executeUpdate(insert);			
			
			conn.commit();
			
			return(re);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			System.out.println("AddRoot: "+ e);
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
	public void deleteLeafCategory(Connection conn, categoryBean catbean) throws SQLException
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
				System.out.println("DeleteLeafCategory: Zu loeschende Category ist kein Blatt!");
				return;
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
			stmnt = ("DELETE FROM urlstocategory WHERE id = "+catbean.getID());
			st.executeUpdate(stmnt);

			conn.commit();
			return;
		}
		catch(SQLException e)
		{
			conn.rollback();
			//logging machen
			System.out.println("deleteLeafCategory :"+e);
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
			System.out.println("deleteLeafCategory :"+e);
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
			System.out.println("AddUrl: "+ e);
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
		String insert = new String("INSERT INTO indexedurls(url,indexdate) VALUES('"+re.getUrl()+"','"+dat.toString()+"')");
		
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
				st.executeUpdate(insert);

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
			
			
			//Alle Zuordnungen durchfueheren 
			java.util.Iterator it = catvec.iterator();
			while(it.hasNext())
			{
				categoryBean c = (categoryBean)it.next();
				//Verbindung auf Category setzen
				String ins = new String("INSERT INTO urltocategory(url,category) VALUES("+re.getID()+","+c.getID()+")");
				st.executeUpdate(ins);
			
			}	


			conn.commit();
			
			return(re);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			System.out.println("AddUrl: "+ e);
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
			String insert = new String("INSERT INTO urltocategory(url,category) VALUES("+urlbean.getID()+","+catbean.getID()+")");
			st.executeUpdate(insert);
			
			conn.commit();
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			System.out.println("AddCatToUrl: "+ e);
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
		String getBeanInfo = new String("select left,right from category where id = "+catbean.getID());

		try
		{
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setLeft(rs.getInt(1));
			catbean.setRight(rs.getInt(2));
			
			//find statement
			String find = new String("SELECT * FROM category where left < "+catbean.getLeft()+" and right > "+catbean.getRight());
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
					System.out.println("an Vector adden fehlgeschlagen!");
			}
			
			return(revec);
			
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			System.out.println("FindAllParents: "+ e);
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
		String getBeanInfo = new String("select left,right from category where id = "+catbean.getID());

		try
		{
			//Category Info holen
			ResultSet rs = st.executeQuery(getBeanInfo);			
			// result in categoryBean schreiben
			rs.next();
			catbean.setLeft(rs.getInt(1));
			catbean.setRight(rs.getInt(2));
			
			//find statement
			String find = new String("SELECT * FROM category where left < "+catbean.getLeft()+" and right > "+catbean.getRight());
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
			System.out.println("DBLayerHSQL.FindParent: "+ e);
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
				String ins = new String("INSERT INTO urltocategory(url,category) VALUES("+urlbean.getID()+","+c.getID()+")");
				st.executeUpdate(ins);
			
			}	
			conn.commit();
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			System.out.println("AddCatwithParentsToUrl: "+ e);
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
						rsi.getInt("right"));
				return(re);
			}
			else
				return null;
									
		}
		catch(SQLException e)
		{
			//TODO LoggMessage statt print
			System.out.println("GetCategorybyName: "+ e);
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
			System.out.println("GetUrl ohne RootCategory: "+ e);
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
			System.out.println("DBLayerHSQL: getCatsOfUrl "+ e);
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
			System.out.println("GetUrl mit RootCategory: "+ e);
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
			System.out.println("GetCategoryTree: "+ e);
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
			String search = new String("SELECT version FROM categoryversion WHERE rootid = "+rootbean.getID());
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
			System.out.println("GetVersionNumber: "+ e);
			throw(e);
		}
	}

	
}
