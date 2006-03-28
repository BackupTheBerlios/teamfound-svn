/* Martin Klink 21.3.06
 */


package index.tests;

import org.apache.lucene.document.*;
import java.io.File;
import java.util.Map;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import index.teamfound.*;
import config.Config;
import config.teamfound.TeamFoundConfig;

import java.net.URL;
import java.util.List;

public class IndexerTest
{
	public static void printSearch(String frage, int[] ids, TeamFoundIndexer tfindexer)
	{
			System.out.println("Rufe Suche auf:");
			controller.response.SearchResponse resp;
			try
			{	
				resp = tfindexer.query(frage,ids,30,0);

				org.jdom.Document xmldoc = resp.getXML();
				
				XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
				System.out.println(xmlout.outputString(xmldoc));

			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			
	}
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
			id[0] = 0;
			id[1] = 2;
			SimpleNewIndexEntry tentry = new SimpleNewIndexEntry(url,f,headers,id);
	
			//erstmal ein Entry anlegen
			String url2 = new String("http://www.Regeln.de");
			File f2 = new File("/home/moddin/testseiten/Regel.htm");
			int[] id2 = new int[3];
			id2[0] = 0;
			id2[1] = 1;
			id2[2] = 6;
			SimpleNewIndexEntry tentry2 = new SimpleNewIndexEntry(url2,f2,headers,id2);
		

		try
		{
			URL u1 = new URL("http://www.irgentwas.de");
			
			//eintrag adden
			tfindexer.addUrl(tentry,u1);
			tfindexer.addUrl(tentry2,u1);
			System.out.println("Eintrag geadded");
			
			
			//verschiedene Suchen aufrufen
			
			String frage = new String("Wikipedia");
			
			System.out.println("\nRegeln.htm sollte als ergebnis der Suche kommen:");
			int qid[] = new int[1];
			qid[0] = 1;
			printSearch(frage, qid, tfindexer);

			System.out.println("\nInhalt.htm sollte als ergebnis der Suche kommen:");
			qid[0] = 2;
			printSearch(frage, qid, tfindexer);
			
			System.out.println("\nBeides sollte als ergebnis der Suche kommen:");
			int[] qids = new int[2];
			qids[0] = 1;
			qids[1] = 2;
			printSearch(frage, qids, tfindexer);
		
			System.out.println("\nnur Regel.de sollte als ergebnis der Suche kommen:");
			qids[0] = 1;
			qids[1] = 6;
			printSearch(frage, qids, tfindexer);

			System.out.println("\ndeleteDoc testen:\n");
			Document document = tfindexer.delDoc("http://www.Regeln.de");
			System.out.println(document.toString());
			
		}
		catch (Exception e)
		{
			System.out.println(e);
		}



	}
}
