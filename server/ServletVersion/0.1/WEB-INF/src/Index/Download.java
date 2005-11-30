

/*
 * Martin Klink
 * Einfache Klasse die erstmal eine Url downloaden soll!
 */
package Index;
import java.io.*;
import java.net.*;

public class Download
{
	private URL u;
	private InputStream instream = null;
	private BufferedReader dis;
	private String s;
	private FileWriter out;
	//spaeter sollten wir uns darum kuemmern namen zu vergeben
	private File toWriteIn;
	
	public String downloadFile(String todownload)
	{
		try
		{
			//create url
			u = new URL(todownload);
			//create input stream
			instream = u.openStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(instream));
			
			//File und Filewriter erstellen
			//spaeter sollten wir uns darum kuemmern namen zu vergeben
			
			toWriteIn = new File("/tmp/downloaded.html");
			//damit uns nichts verloren geht
			
			//while(toWriteIn.exists())
			//{
			//	java.lang.Thread.sleep(50);	  
			//}
			
			out = new FileWriter(toWriteIn);
			while((s = dis.readLine()) != null)
			{
				out.write(s);	  
			}
			return("/tmp/downloaded.html");
		}
		catch (MalformedURLException mue)
		{
			System.out.println("MalformedURLException");
			mue.printStackTrace();
			return null;
		}
		catch (IOException ioe)
		{
			System.out.println("IOException");
			ioe.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				instream.close();
				out.close();
			}
			catch (IOException ioe) 
			{
				//ignoring
			}
		}
		
		
	}

	
}
