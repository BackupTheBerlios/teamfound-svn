

/*
 * Martin Klink
 * Einfache Klasse die erstmal eine Url downloaden soll!
 */
package Index;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;


public class Download
{
	private URL u;
	private URLConnection ucon;
	private DataInputStream instream = null;
	private int data;
	private FileOutputStream out;
	private DataOutputStream dout;
	//spaeter sollten wir uns darum kuemmern namen zu vergeben
	private File toWriteIn;
	
	
	
	public String downloadFile(String todownload, String filename)
	{
		try
		{
			//create url
			u = new URL(todownload);

			//create URLConnection
			ucon = u.openConnection();
			ucon.connect();
			//ucon.getHeaderFields();
			
			
			//create input stream
			instream = new DataInputStream(ucon.getInputStream());
			
			//BufferedReader dis = new BufferedReader(new InputStreamReader(instream));
			
			//File erstellen
			
			String type = ucon.getContentType();
			if (!type.startsWith("text"))
			{
					  return("-1");
			}
					  
			String fn = new String("/tmp/"+filename+".html"); 
			toWriteIn = new File(fn);
			
			
			//outputStream zum schreiben in file
			out = new FileOutputStream(toWriteIn);
			dout = new DataOutputStream(out);
			
			while((data = instream.read())!= -1)
			{
				dout.write(data);	  
			}
			
			return(fn);
		}
		catch (MalformedURLException mue)
		{
			System.out.println("MalformedURLException");
			mue.printStackTrace();
			return("-1");
		}
		catch (IOException ioe)
		{
			System.out.println("IOException");
			ioe.printStackTrace();
			return ("-1");
		}
		finally
		{
		
			try
			{
				instream.close();
				dout.flush();
				dout.close();
				out.flush();
				out.close();
			}
			catch (IOException ioe) 
			{
				//ignoring
			}
		}
		
		
	}

	
}
