/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class UpdatePageResponse extends Response {
	
	protected String url;
	
	public UpdatePageResponse(List<Tuple<Integer,Integer>> projectCounters, String url) {
		super(projectCounters);
		this.url = url;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		
		Element root = d.getRootElement();
		
		
		Element addPage = new Element("updatePage");
		root.addContent(addPage);
		
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
