package controller;
import index.NewIndexEntry;
import index.teamfound.SimpleNewIndexEntry;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.IOException;


/**
 * @author Martin Klink, Jonas Heese
 * Einfache Klasse die erstmal eine Url downloaden soll!
 * 
 * (jonas am 5.12.05):
 * Habe diese Klasse mal etwas umgebaut. Die Übergabe einer URL reicht aus, die 
 * Rückgabe eines Datei-Objektes ebenfalls.
 * 
 * Diese Klasse sollte irgendwann nochmal serialisiert werden, das zwei 
 * simultane Requests, die gleiche Datei herunterzuladen keine Probleme macht.
 * 
 * Genauso toll wäre ein Cache Managment, so das bei obigem Szenario, die 
 * Datei nur einmal heruntergeladen werden würde.
 * (/jonas)
 * 
 * @todo Jedes Request sollte einen eindeutigen dateinamen für die temporäre Datei generieren
 * @todo Sofern Lucene da mitmacht sollte auf die temporäre Datei verzichtet werden
 * 
 */
public class Download
{
	
	protected String tempDir = "/tmp";
	
	
	public NewIndexEntry downloadFile(URL adress) throws DownloadFailedException
	{
		// Habe diese deklarationen mal in die methode gezogen, da es 
		// eigentlich keinen grund gibt, diese Methode nicht halbwegs 
		// threadsafe zu machen (jonas) 
		URLConnection ucon = null;
		DataInputStream instream = null;
		int data;
		FileOutputStream out = null;
		DataOutputStream dout = null;
		File toWriteIn = null;
		NewIndexEntry entry = null;
		
		try
		{
			//create url
			
			//create URLConnection
			ucon = adress.openConnection();
			ucon.connect();
			//ucon.getHeaderFields();
			
			
			//create input stream
			instream = new DataInputStream(ucon.getInputStream());
			
			//BufferedReader dis = new BufferedReader(new InputStreamReader(instream));
			
			//File erstellen
			
			String type = ucon.getContentType();
			if (!type.startsWith("text"))
			{
					// was genau ist hier das problem?
					// @todo bitte mal bessere Excpetion-.Beschreibung nachtragen :)
					throw new DownloadFailedException("uhm, dunno what has happened, but it has failed!");
			}
			
			// @todo Die HTML-Endung hier ist irgendwie unschön, ich schlage vor wie hier schon grundsätzlich
			// gemacht, die URL zu hashen und das abzuspeichern. Ist die Dateiendung hier wichtig? Eigentlich 
			// sollte sowas doch vor dem Download geprüft werden?! So im Sinne, wenn wir eine Dateiart nicht 
			// verarbeiten können, knallt es schon beim Download, nicht erst beim Versuch die zu indexieren.			
			toWriteIn = new File(tempDir+(adress.getHost()+adress.getPath()).hashCode()+".html");
			
			// Hier würde sich evtl. ein Cache-Managment gut machen :)
			toWriteIn.createNewFile();
			
			//outputStream zum schreiben in file
			out = new FileOutputStream(toWriteIn);
			dout = new DataOutputStream(out);
			
			while((data = instream.read())!= -1)
			{
				dout.write(data);	  
			}
			
			entry = new SimpleNewIndexEntry(adress.toString(), toWriteIn, ucon.getHeaderFields());
			
			
		}
		catch (MalformedURLException mue)
		{
			DownloadFailedException e = new DownloadFailedException("Malformed URL");
			e.initCause(mue);
		}
		catch (IOException ioe)
		{
			DownloadFailedException e = new DownloadFailedException("IO-Exception");
			e.initCause(ioe);
		}

		finally
		{
			// tidy up
			try
			{
				// @todo das fliegt uns um die ohren, wenn da noch nullpointer drin sind?! (jonas)
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
		
		// Datei-Objekt zurückgeben, könnte zwar ein Nullpointer sein, in diesem 
		// Fall müsste aber eine Exception geflogen sein.			
		return entry;
	}
}
