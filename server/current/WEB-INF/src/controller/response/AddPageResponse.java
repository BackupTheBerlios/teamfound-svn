/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class AddPageResponse extends Response {
	
	protected String url;
	
	public AddPageResponse( String url) {
		super();
		this.url = url;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		
		//Element root = d.getRootElement();
		
		Element addPage = new Element("addPage");
		teamfound.addContent(addPage);
		
		Element url = new Element("url");
		url.addContent(this.url);
		
		addPage.addContent(url);

		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "Not yet implemented";
	}
}
