/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author jonas
 *
 * Ein wiedereintrittsfester aber nicht synchronisierter Crawler für verschiedene Inhaltstypen.
 * 
 * Derzeit wird nur HTML unterstützt. Folgende Features implemnentiert diese Klasse:
 * - Download beliebiger erreichbarer Url
 * - Rückgabe eines Dokumententypes mit Informationen zum Download
 * - Dokument enthält kompletten Inhalt
 * - Encoding des Inhaltes wird je nach Inhaltstyp bestimmt (Connection, HTTP-Header, Content-spezifisch)
 * 
 * @author Jonas Heese
 */
public class Crawler {
	/**
	 * http-header from the last request
	 */
	private Map header;
	/**
	 * content from the last request (maybe null in case of http-status != 200)
	 */
	private String content;
	/**
	 * Content-Type from the last request (maybe null in case of http-status != 200)
	 */
	private String type;
	/**
	 * the http-status-code from the last request
	 */
	private int responseCode;
	/**
	 * url to be fetched (or that has benn fetched in the last request
	 */
	private URL actUrl;
	/**
	 * encoding of the content from the last request (maybe null in case of http-status != 200)
	 */
	private String encoding;
	/**
	 * Log-messages are thrown in here 
	 */
	private Logger log;
	/**
	 * pattern to get encoding from html-document
	 */
	private Pattern HTMLEncodingPattern;
	/**
	 * pattern to get encoding out of xml-declaration
	 */
	private Pattern XMLEncodingPattern;
	
	/**
	 * Standardkonstruktor
	 *
	 */
	public Crawler() {
		log = Logger.getLogger("crawler");
		HTMLEncodingPattern = Pattern.compile(".*<meta http-equiv=\"Content-Type\" content=\"text/html; charset=([a-zA-Z0-9-]*)\">.*");
		XMLEncodingPattern = Pattern.compile("<?xml version=\"[0-9.]*\" .* encoding=\"([a-zA-Z0-9_-])\"?>");
	}
	
	
	
	/**
	 * Holt den Inhalt zu einer URL
	 * 
	 * @param url Die URL, welche den Inhalt repräsentiert, der vom Crawler abgeholt werden soll
	 * @return Ein CrawlerDocument mit dem Inhalt sowie weiteren Informationen zu dieser Verbindung
	 * @throws CrawlerHTTPException Falls der Inhaltstyp nicht textbasiert (UnknownContentTypeException) ist oder ein HTTP-Fehler auftritt (HTTPStatusException)
	 * @throws IOException Bei Verbindungsproblemen
	 */
	public CrawlerDocument crawl(URL url) throws CrawlerHTTPException, IOException{
		log.info("fetching url "+url.toString());
		actUrl = url;
		int code = fetchUrl(url);
		switch(code) {
			case HttpURLConnection.HTTP_OK:  // 200
				return buildDocument();
			default:
				throw new HTTPStatusException(Integer.toString(code));		
		}
	}
	
	
	
	
	/**
	 * Baut aus den Informationen dieses Instanz ein Dokument zur Rückgabe
	 * 
	 * @return Ein CrawlerDocument mit dem Inhalt dieser Instanz
	 */
	private CrawlerDocument buildDocument() {
		return new CrawlerDocument(actUrl, header, content);
	}

	/**
	 * Lädt eine URL und gibt den http-code zurück
	 * 
	 * Header und Content werden in den Klassenvariablen header und content 
	 * gespeichert und können von dort entsprechend des Statuscodes bearbeitet
	 * werden.
	 * 
	 * Nach dem Durchlaiuf dieser Methode, sind je nach Rückgabecode folgende ZUstände möglich:
	 * HTTP/200 - header, content, encoding und type sind gesetzt
	 * HTTP/!=200 - header und type sind gesetzt, content und encoding sinn nullpointer
	 * 
	 * @param url Die Url welche geladen werden soll
	 * @return Der HTTP-Statuscode der Anfrage
	 * @throws IOException Bei Verbdingsproblemen
	 */
	protected int fetchUrl(URL url) throws IOException {
		
		// aufräumen
		boolean defaultEncoding = false;
		content = null;
		encoding = null;
		header = null;
		responseCode = 0;
		type = null;
		
		// verbindung aufnehmen
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(true);
		//conn.setConnectTimeout(30);
		conn.setRequestMethod("GET");
		conn.connect();
		
		// header und content-types sichern
		header = conn.getHeaderFields();
		type = conn.getContentType();
		log.debug("["+url.toString()+"] content-type: "+type);
		
		responseCode = conn.getResponseCode();
		log.info("["+url.toString()+"] HTTP "+responseCode);
		
		if(responseCode != 200) {

			return responseCode;
		}
		// encoding bestimmen
		// a) die connection weiss das
		if(conn.getContentEncoding() != null) {
			encoding = conn.getContentEncoding();
		} else {
			// b) wir schauen ob was im content-type steht
			if(type.contains("charset=")) {
				// es steht was drin
				
				// wo steht es?
				int pos = type.indexOf("charset=")+8;
				// und wo hört es wieder auf?
				int end = type.indexOf(" ", pos);
				
				if(end > 0) {
					encoding = type.substring(pos, type.indexOf(" ", pos));
				} else {
					encoding = type.substring(pos);
				}
			} else {
				// no encoding yet, we have to look inside the content
				// for decoding, we will use latin-1as default
				// if the document contains a charset-definition we need to reencode the string later
				encoding = "iso-8859-1";
				defaultEncoding = true;
			}
		}
		
		if(!defaultEncoding) {
			log.info("["+url.toString()+"] encoding: "+encoding);
		} else {
			log.info("["+url.toString()+"] using default encoding: "+encoding);			
		}
			
		// typ des inhaltes prüfen
		if(!type.startsWith("text")) {
			// kein texttyp
			throw new UnknownContentTypeException(type);
		}
		
		// inhalt in einen stringbuffer ziehen
		StringBuffer sbuff = new StringBuffer(conn.getContentLength() > 0 ? conn.getContentLength() : 2000);
		
		InputStreamReader in = new InputStreamReader(conn.getInputStream(), encoding);
	
		//To Jonas: Eingefuegt weil immer leerer Inhalt kam!
			char ch;
			int a;
			while ((a = in.read()) >= 0)
			{
				ch = (char) a;
				sbuff.append(ch);
			}
			in.close();
		////////////////////////////////////////////////////////


		int c, i = 0;
		
		
		content = sbuff.toString();
		log.info("["+url.toString()+"] content-length: "+content.length());
		
		if(defaultEncoding) {
			encoding = getEncodingFromContent(content);
			if(encoding != null) {
				content = reencode(encoding, content.getBytes());
				log.info("["+url.toString()+"] new content-length: "+content.length());
			}
		}
		
		return conn.getResponseCode();
	}





	/**
	 * Diese Methode versucht je nach Inhaltstyp das Encoding desselbigen festzustellen
	 * 
	 * Es konnte über die Connection sowie die HTTP-Header kein Encoding des Inhaltes bestimmt werden. 
	 * Diese Methode kann je nach Inhaltstyp versuchen das Encoding aus dem - mit dem 
	 * Standardencoding dekodierten - Inhalt zu bestimmen, zB. aus dem XML-Header oder 
	 * einem HTML-Meta-Element
	 * 
	 * @param content2 Der per Standardencoding dekodierte Inhalt
	 * @return null wenn kein Encoding gefunden wurde oder den Namen des Encodings
	 * @throws UnknownContentTypeException Dieser Inhaltstyp wird nicht unterstützt
	 */
	private String getEncodingFromContent(String content2) throws UnknownContentTypeException {
		if(type.contains("text/html")) {
			// html
			return getEncodingFromHTML(content2);
		}
		throw new UnknownContentTypeException(type);
	}
	
	/**
	 * Decodiert das übergebene Byte-Array mit dem übergebenen Encoding
	 * @param encoding Der Name des Encodings
	 * @param rawContent EIne Byte-Folge welche decodiert werden soll
	 * @return Der aus dem Byte-Arraymit dem Encoding dekodierte String
	 * @throws UnsupportedEncodingException Das übergebene Encdoign wird nicht unterstützt
	 */
	private String reencode(String encoding, byte[] rawContent) throws UnsupportedEncodingException {
		log.debug("["+actUrl.toString()+"] reencoding, using "+encoding);
		String a = new String(rawContent, encoding);
		return a;
	}
	
	/**
	 * Extrahiert aus der ersten (0) Gruppe des Matchers den Namen des Encodings
	 * @param m Ein ChatSet-Matcher welcher - sofern vorhanden - den Namen des Encodings in der ersten Gruppe trägt
	 * @return Der Name des Encodings oder null, sollte keines gefunden worden sein
	 */
	private String getEncodingFromMatcher(Matcher m) {
		if(m.find()) {
			return m.group(1);
		} else {
			return null;
		}
	}
	
	/**
	 * Versucht das Encoding aus einem HTML-Dokument zu bestimmen
	 * 
	 * @param content2 Das HTML-Dokument
	 * @return Der Name des Encodings oder null
	 */
	private String getEncodingFromHTML(String content2) {
		// test html-pattern 1
		Matcher m;
		String encoding;
		m = HTMLEncodingPattern.matcher(content2);
		if((encoding = getEncodingFromMatcher(m)) != null) {
			return encoding;
		} 
		
		// no html-charset, maybe the xml-pattern will match?
		m = XMLEncodingPattern.matcher(content2);
		if((encoding = getEncodingFromMatcher(m)) != null) {
			return encoding;
		}
		
		log.warn("["+actUrl.toString()+"] no encoding found, default encoding maybe wrong");
		// nothing found
		return null;
	}
	
	
	
	
	
	
}
