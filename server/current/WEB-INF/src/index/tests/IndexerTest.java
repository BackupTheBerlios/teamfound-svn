/* Martin Klink 21.3.06
 */


package index.tests;

import org.apache.lucene.document.*;
import java.io.File;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;


import index.teamfound.*;
import config.Config;
import config.teamfound.TeamFoundConfig;

import java.net.URL;

public class IndexerTest
{
	public static void printSearch(String frage, int[] ids, TeamFoundIndexer tfindexer)
	{
			System.out.println("Rufe Suche auf:");
			//controller.response.SearchResponse resp;
			Vector<Document> docvec;
			try
			{	
				docvec = tfindexer.query(frage,ids,30,0);
				Iterator it = docvec.iterator();
				Document doc; 
				while(it.hasNext())
				{
					doc = (Document)it.next();
					System.out.println(doc.toString());
				}

			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			
	}
	public static void main(String args[])
	{
		try
		{
			java.io.File fp = new java.io.File("/home/build/teamfound.properties");

			java.io.FileInputStream pin = new java.io.FileInputStream(fp);

			java.util.Properties props = new java.util.Properties();

			props.load(pin);
		
			TeamFoundConfig.initstatic(props);
			System.out.println(TeamFoundConfig.getConfValue("tfpath"));
		

			//Indexer anlegen
			System.out.println("Index anlegen:");
			sync.ReadWriteSync s = new sync.ReadWriteSync();
			TeamFoundIndexer tfindexer = new TeamFoundIndexer(s);
			
		
			//Index anlegen
			String pfad = new String("/home/moddin/build/teamfoundDir");
/*
			tfindexer.createIndex(pfad);
			System.out.println("IndexCreated!");
			
*/
		//Eintrag in Index ablegen
//			System.out.println("Doc in Index:");
	
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
		

/*			URL u1 = new URL("http://www.irgentwas.de");
			
			//eintrag adden
			tfindexer.addUrl(tentry,u1);
			tfindexer.addUrl(tentry2,u1);
			System.out.println("Eintrag geadded");
			
			
			//verschiedene Suchen aufrufen
*/			
			String frage = new String("Black");
			
//			System.out.println("\nRegeln.htm sollte als ergebnis der Suche kommen:");
			System.out.println("\nsollte ein ergebnis finden id 1 :");
			int qid[] = new int[1];
			qid[0] = 0;
			printSearch(frage, qid, tfindexer);

//			System.out.println("\nInhalt.htm sollte als ergebnis der Suche kommen:");
			System.out.println("\nsollte kein ergebnis finden id 2 :");
			qid[0] = 2;
			printSearch(frage, qid, tfindexer);
			
//			System.out.println("\nBeides sollte als ergebnis der Suche kommen:");
			System.out.println("\nsollte ein ergebnis finden id 1 3 :");
			int[] qids = new int[2];
			qids[0] = 1;
			qids[1] = 3;
			printSearch(frage, qids, tfindexer);
		
//			System.out.println("\nnur Regel.de sollte als ergebnis der Suche kommen:");
			System.out.println("\nsollte ein ergebnis finden 1 2  :");
			qids[0] = 1;
			qids[1] = 2;
			printSearch(frage, qids, tfindexer);

//			System.out.println("\ndeleteDoc testen:\n");
//			Document document = tfindexer.delDoc("http://www.Regeln.de");
//			System.out.println(document.toString());
			
		}
		catch (Exception e)
		{
			System.out.println(e);
		}



	}
}
