/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

import java.io.IOException;

/**
 * Basic Exception for all Crawler-related issues
 * 
 * @author jonas
 */
public class CrawlerException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5374029259740329561L;
	
	/**
	 * basic constructor 
	 */
	public CrawlerException() {
		super();
	}
	
	/**
	 * basic constructor
	 * @param arg0 exception message
	 */
	public CrawlerException(String arg0) {
		super(arg0);
	}
}
