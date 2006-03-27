/*
 * Created on Mar 27, 2006
 */
package controller.response;

import org.jdom.Document;


public class ErrorResponse extends Response {

	public Document getXML() {
		return super.getBaseDocument();
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
