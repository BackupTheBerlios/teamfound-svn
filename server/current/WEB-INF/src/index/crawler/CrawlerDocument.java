/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

import index.NewIndexEntry;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * @author jonas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CrawlerDocument implements NewIndexEntry {

	protected String content;
	protected URL url;
	protected Map headers;

	public CrawlerDocument(URL actUrl, Map headers, String content) {
		this.url = actUrl;
		this.content = content;
		this.headers = headers;
	}
	
	/**
	 * @return Returns the content.
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @return Returns the url.
	 */
	public URL getUrl() {
		return url;
	}


	public Map getHeaders() {
		return headers ;
	}
	
}
