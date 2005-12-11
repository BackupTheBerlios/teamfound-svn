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
		//Connection conn2;
		try
		{
			conn = db.getConnection("tf","tfpass","anyserver","tfdb");
			//conn.setAutoCommit(false);
			
			db.addRootCategory(conn,new categoryBean("testcat"));
			
			db.query(conn,"SELECT * FROM category");
			
			db.query(conn,"SELECT * FROM indexedurls");

			//conn2 = db2.getConnection("tf","tfpass","anyserver","tfdb");
			
			//System.out.println("conn1 hashcode: " + conn.hashCode());
			
			//System.out.println("conn2 hashcode: " + conn2.hashCode());
			
			conn.close();
			//conn2.close();
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
