/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class AddCategoriesResponse extends Response {
	
	protected String name;
	protected Integer ID;
	
	public AddCategoriesResponse(List<Tuple<Integer,Integer>> projectCounters, String name, Integer newID) {
		super(projectCounters);
		this.name = name;
		this.ID = newID;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		Element xmlroot = d.getRootElement();
		
		// unterbaum aufbauen
		Element add = new Element("addcategory");
		xmlroot.addContent(add);
		Element name = new Element("name");
		add.addContent(name);
		Element gotid = new Element("gotid");
		add.addContent(gotid);
		
		// inhalte hinzufügen
		name.addContent(this.name);
		gotid.addContent(ID.toString());
		
		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
