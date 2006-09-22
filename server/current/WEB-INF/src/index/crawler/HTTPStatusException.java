/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package index.crawler;

/**
 * An unrecoverable HTTP-Error has been received
 * @author jonas
 */
public class HTTPStatusException extends CrawlerHTTPException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4597313492033465102L;

	/**
	 * basic constructor
	 */
	public HTTPStatusException() {
		super();
	}

	/**
	 * basic constructor
	 * @param exception-message
	 */
	public HTTPStatusException(String arg0) {
		super(arg0);
	}

}
