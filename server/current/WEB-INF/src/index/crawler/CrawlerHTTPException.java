/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

/**
 * basic-exception class for http-related issues within the crawler
 * 
 * @author jonas heese
 */
public class CrawlerHTTPException extends CrawlerException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3665035798781444724L;

	/**
	 * basic constructor
	 */
	public CrawlerHTTPException() {
		super();
	}

	/**
	 * basic constrcutor
	 * @param arg0 exception message
	 */
	public CrawlerHTTPException(String arg0) {
		super(arg0);
	}

	
}
