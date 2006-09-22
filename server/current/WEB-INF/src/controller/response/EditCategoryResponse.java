/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class EditCategoryResponse extends Response {
	
	protected String name;
	protected String description;
	protected Integer ID;
	
	public EditCategoryResponse(String name, String description, Integer catid) {
		super();
		this.name = name;
		this.description = description;
		this.ID = catid;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		
		// unterbaum aufbauen
		Element add = new Element("editcategory");
		teamfound.addContent(add);

		Element ename = new Element("name");
		add.addContent(ename);

		Element edescription = new Element("description");
		add.addContent(edescription);

		Element eid = new Element("id");
		add.addContent(eid);
		
		// inhalte hinzufügen
		ename.addContent(this.name);
		edescription.addContent(this.description);
		eid.addContent(ID.toString());
		
		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
