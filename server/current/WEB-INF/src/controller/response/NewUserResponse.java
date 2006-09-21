/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class NewUserResponse extends Response {
	
	protected String username;
	
	public NewUserResponse( String name ) {
		super();
		this.username = name;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		
		// unterbaum aufbauen
		Element add = new Element("registeruser");
		teamfound.addContent(add);
		Element name = new Element("user");
		add.addContent(name);
		
		// inhalte hinzufügen
		name.addContent(this.username);
		
		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
