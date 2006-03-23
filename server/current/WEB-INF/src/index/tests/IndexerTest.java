/* Martin Klink 21.3.06
 */


package index.tests;

import org.apache.lucene.document.*;
import java.io.File;
import java.util.Map;


import index.teamfound.*;
import config.Config;
import config.teamfound.TeamFoundConfig;
import java.net.URL;

public class IndexerTest
{
	public static void main(String args[])
	{


		//Index anlegen
		System.out.println("Index anlegen:");
		Config c = new TeamFoundConfig();
		sync.ReadWriteSync s = new sync.ReadWriteSync();
		TeamFoundIndexer tfindexer = new TeamFoundIndexer(c,s);
		String pfad = new String("/home/moddin/Uni/OpenSourceSoftEnt/teamfound/server/teamfoundDir");

		try
		{
			tfindexer.createIndex(pfad);
			System.out.println("IndexCreated!");
		}
		catch(Exception e)
		{
			System.out.println(" caught a " + e.getClass() +
			"\n with message: " + e.getMessage());
			
		}

		//Eintrag in Index ablegen
		System.out.println("Doc in Index:");
	
			//erstmal ein Entry anlegen
			String url = new String("http://www.irgentwas.de");
			File f = new File("/tmp/index.html");
			Map headers = new java.util.HashMap();
			int[] id = new int[3];
			id[0] = 1;
			id[1] = 2;
			id[2] = 5;
			SimpleNewIndexEntry tentry = new SimpleNewIndexEntry(url,f,headers,id);
		
		try
		{
			URL u1 = new URL("http://www.irgentwas.de");
			
			//eintrag adden
			tfindexer.addUrl(tentry,u1);
			
			System.out.println("Eintrag geadded");
			
		
		}
		catch (Exception e)
		{
			System.out.println(e);
		}



	}
}
