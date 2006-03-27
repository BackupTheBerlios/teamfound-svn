/*
 * Created on Mar 27, 2006
 */
package controller.response;

import org.jdom.Document;
import org.jdom.Element;


public class AddPageResponse extends Response {
	
	protected String url;
	
	public AddPageResponse(String url) {
		this.url = url;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		
		Element root = d.getRootElement();
		
		
		Element addPage = new Element("addPage");
		root.addContent(addPage);
		
		Element url = new Element("url");
		url.addContent(this.url);
		
		addPage.addContent(url);
		
		return d;	
	}
}
