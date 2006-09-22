package index.crawler.teamfound;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import index.NewIndexEntry;
import index.crawler.Crawler;
import index.crawler.CrawlerDocument;
import index.crawler.CrawlerHTTPException;

/**
 * Downloads URLs for the TeamFound-Indexer, applies some extended parsing issues 
 * such as merging frameset-contents into the resulting document 
 * 
 * @author jonas heese
 *
 */
public class TeamFoundCrawler {
	
	/**
	 * Maximum Depth for frameset-fetching
	 */
	protected int defaultFetchFrameDepth;
	/**
	 * This crawler-instance is used to fetch http-urls
	 */
	protected Crawler crawler;
	/**
	 * log messages are thrown in here
	 */
	protected Logger log;
	
	/**
	 * Basic Constructor
	 * @param defaultFetchFrameDepth Fetch at maximum this much levels of nested Frameset-Documents
	 */
	public TeamFoundCrawler(int defaultFetchFrameDepth) {
		crawler = new Crawler();
		this.defaultFetchFrameDepth = defaultFetchFrameDepth;
		log = Logger.getLogger("tf_crawler");
	}
	
	/**
	 * Fetch an URL
	 * 
	 * If a frameset is encountered, the linked 
	 * framesets are fetched and merged (until a specified nesting-depth) 
	 * Documents containing a frameset are not merged into the returned content 
	 * (as they should not contain valuable content and we do not want to index 
	 * noframes-messages)
	 * 
	 * @param url Url to fetch
	 * @return Content of the url or its Framesets
	 * @throws CrawlerHTTPException The Crawler encountered errors, see HTTPStatusException or UnknownContentTypeException
	 * @throws IOException in case of connection-errors or unknown encodings
	 * @see HTTPStatusException
	 * @see UnknownContentTypeException
	 */
	public NewIndexEntry fetch(URL url) throws CrawlerHTTPException, IOException {
		return parseDoc(crawler.crawl(url), defaultFetchFrameDepth);
	}
	
	/**
	 * Fetch an url, specify a non default maximum frameset-nesting-depth
	 * 
	 * @param url Url to fetch
	 * @param fetchFrameDepth maximum frameset-nesting-depth to fetch (default value is ignored)
	 * @return Content of the Url or its framesets
	 * @throws CrawlerHTTPException The Crawler encountered errors, see HTTPStatusException or UnknownContentTypeException
	 * @throws IOException in case of connection-errors or unknown encodings
	 * @see HTTPStatusException
	 * @see UnknownContentTypeException
	 */
	public NewIndexEntry fetch(URL url, int fetchFrameDepth) throws CrawlerHTTPException, IOException {
		return parseDoc(crawler.crawl(url), fetchFrameDepth);
	}
	
	protected NewIndexEntry parseDoc(CrawlerDocument doc, int fetchFrameDepth) throws CrawlerHTTPException, MalformedURLException, IOException {
		
		if(/*type.contains("html") &&*/ fetchFrameDepth > 0) {
			
			
			String docUrl = doc.getUrl().toString();
			int fileStart = docUrl.lastIndexOf("/");
			docUrl = docUrl.substring(0, fileStart+1);  // das slash zwischen pfad und datei mitnehmen
		
			String content = doc.getContent();
			// inhalt ist html
			Vector<String> frameUrls = new Vector<String>();
			Pattern framePattern = Pattern.compile("<frame .* src=\"(.*)\".*>");
			// testen auf framesets
			if(content.contains("frameset")) {
				log.debug("Frameset gefunden");
				Matcher m = framePattern.matcher(content);
				// url der frames holen
				int idx = 0;
				while(m.find(idx)) {
					String frameUrl = m.group(1);
					
					// wenn die url nicht absolut ist, also nicht mit http beginnt, 
					// setzen wir die url des framesets ohne dateiangabe davor
					
					if(!frameUrl.startsWith("http://")) {
						frameUrl = docUrl + frameUrl;
					}
					
					frameUrls.add(frameUrl);
					log.debug("new frame-url: "+frameUrl);
					idx = m.end();
				}
				
				if(frameUrls.size() > 0) {
					// es wurden frames gefunden
					StringBuffer resultBuf = new StringBuffer();
					Iterator i = frameUrls.iterator();
					while(i.hasNext()) {
						String url = (String)i.next();
						CrawlerDocument cd = crawler.crawl(new URL(url));
						cd = (CrawlerDocument) parseDoc(cd, fetchFrameDepth - 1);
						resultBuf.append(cd.getContent());
					}
					
					doc = new CrawlerDocument(doc.getUrl(), doc.getHeaders(), resultBuf.toString());
				}
			} else {
				log.debug("Frameset nicht gefunden");
			}
			// pre: inhalt aller frames ist nun in this.content
			// TODO das crawlerdocument sollte evtl. noch zwischen einzelnen frames unterscheiden können? evtl  eine subklasse implementieren
		} else {
			log.debug("Frameset parsing recursion-depth exceeded, giving up");
		}
		return doc;
	}
}
