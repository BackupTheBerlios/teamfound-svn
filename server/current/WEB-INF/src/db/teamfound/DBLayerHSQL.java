/**
 * Created 10.12.2005
 *
 * DBlayer fuer HSQLDB 
 * Zur Zeit entwickelt um HSQLDB in teamfound eingebettet laufen zu lassen und
 * nicht als Server.
 *
 * @author Martin Klink
 */

package db.teamfound;

import org.hsqldb.*;
import db.DBLayer;
import db.dbbeans.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBLayerHSQL implements DBLayer
{

	/** 
	* Verbindung zur Datenbank herstellen
	*  wird wohl noch private denke ich
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
		// teamfoundDir/db/ im aktuellen Ausfuehrungsverzeichnis landen
		// dies solle spaeter aus einem property stammen 
		// Vorschlag ein teamfound folder fuer index und db ..
		String dblocation = new String("jdbc:hsqldb:file:teamfoundDir/db/"+database+";ifexists=true");
	
		
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
		String dblocation = new String("jdbc:hsqldb:file:teamfoundDir/db/"+database);
	
		
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
			//String sqlcreate = new String("CREATE TABLE category (id INTEGER IDENTITY,root_id INTEGER, left INTEGER, right INTEGER, name VARCHAR,CONSTRAINT leftset UNIQUE (left), CONSTRAINT rightset UNIQUE (right))");			
			String sqlcreate = new String("CREATE TABLE category (id INTEGER IDENTITY,root_id INTEGER, left INTEGER, right INTEGER, name VARCHAR)");			
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);

			//create URL table
			sqlcreate = "CREATE TABLE indexedurls (id IDENTITY,url VARCHAR,category INTEGER,indexdate DATE,FOREIGN KEY (category) REFERENCES category(id))";
			if(!update(c,sqlcreate))
				System.out.println("error in Statement "+ sqlcreate);
			
			//Grant rights to user
			cuser=("GRANT ALL ON category TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");
									
			cuser=("GRANT ALL ON indexedurls TO " +user);
			if(!update(c,cuser))
				System.out.println("error bei grant to User ");
			
			//create root of the category tree
			// ID1 : 	1 setroot 2
			//if(!update(c,"INSERT INTO category(id,root_id,left,right,name) VALUES(1,1,1,2,'testcat')"))
			//	System.out.println("error bei first Insert ");
			
			c.commit();
	
		}
		catch (SQLException ex2)
		{
			c.rollback(init);
			//existiert wohl schon 
			//dann brauch ich auch nichts anlegen
			System.out.println("Fehler beim DB initialisieren" +ex2);
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
	 *nur als testfunktion zu verwenden..
	 */
	public synchronized void query(Connection conn,String expression) throws  SQLException
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
	* Methode zum ausfuehren aller Create, Drop, Insert, und Update Anfragen
	* 
	* Note: fuer viele updates nacheinader sollte das StatementObject immer
	* wieder verwenden ...
	* 
	* @param user  Nutzername
	* @param pass  Passwort
	* @param database DBname
	*/
	private synchronized boolean update(Connection conn,String expression) throws  SQLException
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
	 *
	 */
	public void addCategory(Connection conn, categoryBean catbean) throws SQLException
	{
		//TODO Inhalt
	}

	/**
	 * Einfuegen einer rootCategory ins Set ..
	 *
	 */
	public void addRootCategory(Connection conn, categoryBean catbean) throws SQLException
	{
		// lock table category , set savepoint
		java.sql.Savepoint addRC;
		//addRC = conn.setSavepoint("addRootCategory");
		
		Statement st = null;
		st = conn.createStatement();    // erstelle statements
		//TODO ist die Bean auch gefuellt			
		String insert = new String("INSERT INTO category(left,right,name) VALUES(1,2,'"+catbean.getCategory()+"')");
		
		//Statement um PrimKey zu kriegen
		String prim = new String("CALL IDENTITY()");
		
		try
		{
			//ausfuehren des inserts
			st.executeUpdate(insert);

			//key holen
			ResultSet rsi = st.executeQuery(prim);
			rsi.next();
			int identity = rsi.getInt(1);
		
			//update von root_id
			String upd = new String("UPDATE category SET root_id = " +identity+ " WHERE id =" +identity);
			st.executeUpdate(upd);

			
			conn.commit();
			
		}
		catch(SQLException e)
		{
			//conn.rollback(addRC);
			//TODO LoggMessage
			System.out.println("AddRoot: "+ e);
			
		}
		
	}

	/**
	 * Hinzufuegen einer Url (entweder schon indiziert oder wird gleich indiziert)
	 */ 
	public void addUrl(Connection conn, urltabBean urlbean) throws SQLException
	{
		//TODO Inhalt
	}

	/**
	 * Category ahand bezeichnung suchen ..
	 *
	 */
	public categoryBean getCategoryByName(Connection conn, String catname) throws SQLException
	{
		//TODO Inhalt
		return(new categoryBean());
	}

	/**
	 * nach Url suchen
	 */ 
	public urltabBean getUrl(Connection conn, String url) throws SQLException
	{
		//TODO Inhalt
		return(new urltabBean());
	}

	/**
	 * CategoryBaum auslesen ..
	 * TODO RueckgabeWert
	 */
	public void getCategoryTree(Connection conn) throws SQLException
	{
		//TODO Inhalt
	}

	
}
