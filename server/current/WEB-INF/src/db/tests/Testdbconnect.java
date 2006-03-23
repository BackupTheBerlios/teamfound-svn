package db.tests;

import db.DBLayer;
import db.dbbeans.*;
import db.teamfound.DBLayerHSQL;
import java.sql.Connection;
import config.Config;
import config.teamfound.TeamFoundConfig;

public class Testdbconnect
{
	public static void main(String args[])
	{

		Config c = new TeamFoundConfig();
		DBLayer db;
		db = new DBLayerHSQL(c);
		DBLayer db2 = new DBLayerHSQL(c);

		Connection conn;
		Connection conn2;
		try
		{
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			//conn.setAutoCommit(false);
			
			//neuen rootcat anlegen
			System.out.println("lege neue RootCat an");
			categoryBean rootcat = new categoryBean();
			rootcat.setCategory("rootcat");
			rootcat.setBeschreibung("TestRootKategorie!");
			rootcat = db.addRootCategory(conn,rootcat);
			
			System.out.println("rootCAT:");
			rootcat.printAll();
		
			System.out.println("\n");
			
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			categoryBean oldcat = new categoryBean();
			oldcat.setCategory("newcat1");
			oldcat.setBeschreibung("TestKategorie1!");
			
			oldcat = db.addCategory(conn,oldcat,rootcat);
			
			System.out.println("newCAT1:");
			oldcat.printAll();

			System.out.println("\n");
			
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			categoryBean newcat = new categoryBean();
			newcat.setCategory("newcat2");
			newcat.setBeschreibung("TestKategorie2!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT2:");
			oldcat.printAll();

			System.out.println("\n");
			
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			newcat.setCategory("newcat3");
			newcat.setBeschreibung("TestKategorie3!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT3:");
			oldcat.printAll();

			System.out.println("\n");
		
	
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			newcat.setCategory("newcat4");
			newcat.setBeschreibung("TestKategorie4!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT4:");
			oldcat.printAll();
			

			System.out.println("\n");
		
	
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			newcat.setCategory("newcat5");
			newcat.setBeschreibung("TestKategorie5!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT5:");
			oldcat.printAll();

			System.out.println("\n");
		
				

			//neue url anlegen
			System.out.println("lege neue url an");
			urltabBean newurl = new urltabBean("www.abs.de");
			
			newurl = db.addUrl(conn,newurl);
			
			System.out.println("newUrl:");
			newurl.printAll();

			System.out.println("\n");

			//neue url mit cat  anlegen
			System.out.println("lege neue url mit category an");
			urltabBean newurl2 = new urltabBean("www.urlmitCategory.de");
			
			newurl2 = db.addUrl(conn,newurl2,newcat);
			
			System.out.println("newUrl:");
			newurl2.printAll();
			System.out.println("die CAT dazu:");
			newcat.printAll();
			System.out.println("\n");
			
			//alle Eltern zu cat suchen
			System.out.println("Alle Eltern zu Cat suchen");
			System.out.println("CAT :");
			newcat.printAll();
			
			categoryBean b;
			java.util.Vector<categoryBean> vb = db.findAllParents(conn,oldcat);
			if (vb.size() == 0)
				System.out.println("Vector ist leer");
			else
			{
				for (int i=0; i < vb.size(); i++)
				{
					System.out.println("\nEltern CAT "+i+" :");
					(vb.get(i)).printAll();
					//b.printAll();
				}
			}
			
			System.out.println("\n");

			//eine url zu einer cat  zuordnen
			
			//erstmal neue cat anlegen
			System.out.println("lege neue Cat an");
			newcat.setCategory("newcat6");
			newcat.setBeschreibung("TestKategorie6!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT6:");
			oldcat.printAll();

			System.out.println("\n");
		

			System.out.println("eine Url mit zu Cat zuordenen mit allen Elternkategorien:");
			db.addCatwithParentsToUrl(conn,newurl2,oldcat);
			
			System.out.println("die Url:");
			newurl2.printAll();
			System.out.println("die CAT dazu:");
			oldcat.printAll();
			System.out.println("\n");

			//Kathegorie anhand bezeichnung suchen
			System.out.println("Kathegorie anhand bezeichnung suchen:");
			categoryBean cbean = db.getCategoryByName(conn,oldcat.getCategory(),rootcat);
			
			System.out.println("Kathegoriename war " +oldcat.getCategory()+" :");
			System.out.println("die gefunden CAT dazu:");
			if (cbean == null)
				System.out.println("nichts gefunden!");
			else
				cbean.printAll();
			System.out.println("\n");

			//nach einer URL suchen
			System.out.println("Nach einer URL suchen:");
			urltabBean ubean = db.getUrl(conn,"www.abc.de");
			
			System.out.println("URL war www.abc.de :");
			System.out.println("die gefunden URLBean dazu:");
			if(ubean == null)
				 System.out.println("nichts gefunden!");
			else
				ubean.printAll();
			System.out.println("\n");
			
			System.out.println("Nach einer URL suchen:");
			ubean = db.getUrl(conn,"www.abs.de");
			
			System.out.println("URL war www.abs.de :");
			System.out.println("die gefunden URLBean dazu:");
			if(ubean == null)
				 System.out.println("nichts gefunden!");
			else
				ubean.printAll();
			System.out.println("\n");

			System.out.println("Nach einer URL suchen:");
			ubean = db.getUrl(conn,"www.urlmitCategory.de");
			
			System.out.println("URL war www.urlmitCategory.de :");
			System.out.println("die gefunden URLBean dazu:");
			if(ubean == null)
				 System.out.println("nichts gefunden!");
			else
				ubean.printAll();
			System.out.println("\n");

			//nach URL mit category suchen
			System.out.println("Nach einer URL in der rootcategory suchen:");
			ubean = db.getUrl(conn,"www.abs.de",rootcat);
			
			System.out.println("URL war www.abs.de :");
			System.out.println("die gefunden URLBean dazu:");
			if(ubean == null)
				 System.out.println("nichts gefunden!");
			else
				ubean.printAll();
			System.out.println("\n");

			System.out.println("Nach einer URL in der rootcategory suchen:");
			ubean = db.getUrl(conn,"www.urlmitCategory.de",rootcat);
			
			System.out.println("URL war www.urlmitCategory.de :");
			System.out.println("die gefunden URLBean dazu:");
			if(ubean == null)
				 System.out.println("nichts gefunden!");
			else
				ubean.printAll();
			System.out.println("\n");

			//Versionsnummer des  categorytree suchen
			System.out.println("Nach Versionsnummer des trees suchen:");
			Integer temp = db.getVersionNumber(conn,rootcat);
			
			System.out.println("die gefundene Version dazu:");
			if(temp == null)
				 System.out.println("nichts gefunden!");
			else
				System.out.println(temp.toString());
			System.out.println("\n");

			
			
// Alles nochmal ausgeben
			System.out.println("\n");
			System.out.println("Alle Tabellen abfragen:");
			conn.close();
	
			
			
			conn2 = db.getConnection("tf","tfpass","anyserver","tfdb");
			
			//Test
			//erstmal neue cat anlegen
			newcat.setCategory("KindvonRoot");
			newcat.setBeschreibung("KindvonRoot");
			categoryBean cat = new categoryBean(); 
			cat.setID(0);
			newcat = db.addCategory(conn2,newcat,cat);
		
			newcat.setCategory("KindvonTestKategorie1");
			newcat.setBeschreibung("KindvonTestKategorie1");
			cat.setID(1);
			newcat = db.addCategory(conn2,newcat,cat);

			newcat.setCategory("NOcheinKindvonTestKategorie1");
			newcat.setBeschreibung("NochEinKindvonTestKategorie1");
			cat.setID(1);
			newcat = db.addCategory(conn2,newcat,cat);


			System.out.println("Select fuer die CategoryTree:");
			db.query(conn2,"SELECT a.left,COUNT(a.left) as level FROM category AS a, category AS b WHERE a.root_id = 0 AND b.root_id = 0 AND a.left BETWEEN b.left AND b.right GROUP BY a.left ORDER BY a.left ");
			
			System.out.println("Select fuer die CategoryTree:");
			db.query(conn2,"SELECT a.id,a.left,a.right,a.name,a.beschreibung,COUNT(*) as level FROM category AS a, category AS b WHERE a.root_id = 0 AND b.root_id = 0 AND a.left BETWEEN b.left AND b.right GROUP BY a.id,a.left,a.right,a.name,a.beschreibung ORDER BY a.left");
		
			System.out.println("\n");
			System.out.println("getCategoryTree Test:");
			String test = db.getCategoryTree(conn2,rootcat);
			System.out.println(test);
			
			System.out.println("\n");
			System.out.println("category table:");
			db.query(conn2,"SELECT * FROM category");
			
			System.out.println("url table :");
			db.query(conn2,"SELECT * FROM indexedurls");

			System.out.println("urltocategory :");
			db.query(conn2,"SELECT * FROM urltocategory");

			System.out.println("categoryversion :");
			db.query(conn2,"SELECT * FROM categoryversion");
			conn2.close();
		}
		catch (Exception e)
		{
			System.out.println("probleme beim Ausfuehren der Query: "+e);
		}
		
		
		try
		{	
			db.shutdown("anyserver","tfdb");
		}
		catch (Exception e)
		{
			System.out.println("probleme beim shutdown: "+e);
		}
	}

}
