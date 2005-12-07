/*
 * Created on Dec 6, 2005
 */
package index.teamfound;

import java.io.File;
import java.util.Map;

import index.NewIndexEntry;

/**
 * Eine erste simple Implementation für einen neuen Index-Eintrag
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */
public class SimpleNewIndexEntry implements NewIndexEntry {
	
	protected String url;
	protected File f;
	protected Map headers;

	/**
	 * @param url
	 * @param f
	 * @param headers
	 */
	public SimpleNewIndexEntry(String url, File f, Map headers) {
		this.url = url;
		this.f = f;
		this.headers = headers;
	}

	/**
	 * @return Returns the File
	 */
	public File getFile() {
		return f;
	}

	/**
	 * @return Returns the headers.
	 */
	public Map getHeaders() {
		return headers;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}
}
