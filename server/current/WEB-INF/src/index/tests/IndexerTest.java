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


		//Indexer anlegen
		System.out.println("Index anlegen:");
		Config c = new TeamFoundConfig();
		sync.ReadWriteSync s = new sync.ReadWriteSync();
		TeamFoundIndexer tfindexer = new TeamFoundIndexer(c,s);
		
		
		//Index anlegen
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
			String url = new String("http://www.Inhalt.htm");
			File f = new File("/home/moddin/testseiten/Inhalt.htm");
			Map headers = new java.util.HashMap();
			int[] id = new int[2];
			id[0] = 3;
			id[1] = 5;
			SimpleNewIndexEntry tentry = new SimpleNewIndexEntry(url,f,headers,id);
	
			//erstmal ein Entry anlegen
			String url2 = new String("http://www.Regeln.de");
			File f2 = new File("/home/moddin/testseiten/Regel.htm");
			int[] id2 = new int[3];
			id2[0] = 1;
			id2[1] = 3;
			id2[2] = 6;
			SimpleNewIndexEntry tentry2 = new SimpleNewIndexEntry(url2,f2,headers,id2);
		

		try
		{
			URL u1 = new URL("http://www.irgentwas.de");
			
			//eintrag adden
			tfindexer.addUrl(tentry,u1);
			tfindexer.addUrl(tentry2,u1);
			
			System.out.println("Eintrag geadded");
			String frage = new String("Wikipedia");
			
			System.out.println("rufe Suche auf:");
			controller.SearchResponse resp;
			resp = tfindexer.query(frage,id);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}



	}
}
