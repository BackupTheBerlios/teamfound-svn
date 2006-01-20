package db.tests;

import db.DBLayer;
import db.dbbeans.*;
import db.teamfound.DBLayerHSQL;
import java.sql.Connection;

public class Testdbconnect
{
	public static void main(String args[])
	{
		DBLayer db;
		db = new DBLayerHSQL();
		DBLayer db2 = new DBLayerHSQL();

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
		
			//neue cat anlegen
			System.out.println("lege neue Cat an");
			newcat.setCategory("newcat6");
			newcat.setBeschreibung("TestKategorie6!");
			
			oldcat = db.addCategory(conn,newcat,oldcat);
			
			System.out.println("newCAT6:");
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

			

			System.out.println("\n");
			System.out.println("Alle Tabellen abfragen:");
			conn.close();
			
			conn2 = db.getConnection("tf","tfpass","anyserver","tfdb");
			
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
