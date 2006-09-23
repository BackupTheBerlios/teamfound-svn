/* Martin Klink 21.3.06
 * tests the creation of an IndexEntry and the 
 * getdocument() method
 */
/*

package index.tests;

import index.teamfound.*;
import org.apache.lucene.document.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TestDocCreation
{
	public static void main(String args[]) throws MalformedURLException
	{
		String url = new String("www.irgentwas.de");
		File f = new File("/home/moddin/testseiten/Inhalt.htm");
		Map headers = new java.util.HashMap();
		int[] id = new int[3];
		id[0] = 1;
		id[1] = 2;
		id[2] = 5;
			
		
		SimpleNewIndexEntry tentry = new SimpleNewIndexEntry(new URL(url),f,headers,id);
		try
		{
			System.out.println("Inhalt des Docs ausgeben:");
			Document doc = tentry.getdocument();
			System.out.println(doc.toString());
		}
		catch(Exception e)
		{
			System.out.println(" caught a " + e.getClass() +
			"\n with message: " + e.getMessage());
			
		}
	}

}
*/
