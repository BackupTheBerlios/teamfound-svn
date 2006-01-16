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
import org.hsqldb.jdbc.jdbcConnection;
import db.DBLayer;
import db.dbbeans.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.Math;

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

			
			//create root of the category tree
			// ID1 : 	1 setroot 2
			//if(!update(c,"INSERT INTO category(id,root_id,left,right,name) VALUES(1,1,1,2,'testcat')"))
			//	System.out.println("error bei first Insert ");
			
			c.commit();
	
		}
		catch (SQLException ex2)
		{
			c.rollback();
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
	* Methode zum ausfuehren aller Create, Drop, Insert, und Update Anfragen
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
	 * in der Bean muss die rootCategory und links und rechts des 
	 * Knotens stehen an den wir anhaengen wollen
	 *	Bsp.          1A6
	 *			2B3			4C5
	 * Wir wollen an B was anhaengen dann brauchen wir A und 2,3
	 *
	 *
	 * 
	 * TODO man braucht nur rootCat und den namen der Oberkategorie
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

			//Rootid holen
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
			return null;
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
			conn.commit();
			
			return(re);
			
		}
		catch(SQLException e)
		{
			conn.rollback();
			//TODO LoggMessage statt print
			System.out.println("AddRoot: "+ e);
			return null;
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
			return; 
		}
		
	}
	/** 
	* Loescht einen TeilBaum aus dem Category-Baum (nested Set) 
	* TODO alle Zuordnungne zu Urls entfernen 
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
			return; 
		}
		
	}
	/**
	 * Hinzufuegen einer Url (entweder schon indiziert oder wird gleich indiziert)
	 * ohne zugehoeriger Category
	 */ 
	public void addUrl(Connection conn, urltabBean urlbean) throws SQLException
	{
		//TODO Inhalt
	}

	/**
	 * Hinzufuegen einer Url (entweder schon indiziert oder wird gleich indiziert)
	 * mit zugehoeriger Category
	 */ 
	public void addUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException
	{
		//TODO Inhalt
	}

	/**
	 * Hinzufuegen einer Category zu einer Url
	 */ 
	public void addCatToUrl(Connection conn, urltabBean urlbean, categoryBean catbean) throws SQLException
	{
		//TODO Inhalt
	}

	/**
	 * Category anhand bezeichnung suchen ..
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
