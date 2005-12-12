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
			categoryBean rootcat = new categoryBean();
			rootcat.setCategory("rootcat");
			rootcat.setBeschreibung("TestRootKategorie!");
			rootcat = db.addRootCategory(conn,rootcat);
			
			System.out.println("rootCAT:");
			rootcat.printAll();
		
			//neue cat anlegen
			categoryBean newcat = new categoryBean();
			newcat.setCategory("newcat");
			newcat.setBeschreibung("TestKategorie!");
			
			newcat = db.addCategory(conn,newcat,rootcat);
			
			System.out.println("newCAT:");
			newcat.printAll();


			
			conn.close();
			
			conn2 = db.getConnection("tf","tfpass","anyserver","tfdb");
			db.query(conn2,"SELECT * FROM category");
			
			db.query(conn2,"SELECT * FROM indexedurls");
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
