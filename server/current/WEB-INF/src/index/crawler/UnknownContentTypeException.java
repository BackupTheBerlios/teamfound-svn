/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

/**
 * The Crawler is unable to work with the received content
 * 
 * Functions for new Mime-Types can be added. The restriction of
 * well-known content-types (html only, atm) is mainly a security 
 * feature, as the crawler can control wich contents are going in the index
 * 
 * please note that the crawler only inspects the content-type-header, 
 * not the content itself

 * @author jonas heese
 */
public class UnknownContentTypeException extends CrawlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7106305930923731256L;

	/**
	 * basic constructor
	 */
	public UnknownContentTypeException() {
		super();
	}

	/**
	 * basic constructor
	 * @param arg0 exception  message
	 */
	public UnknownContentTypeException(String arg0) {
		super(arg0);
	}

}
