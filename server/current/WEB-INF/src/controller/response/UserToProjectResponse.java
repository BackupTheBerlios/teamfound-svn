/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class UserToProjectResponse extends Response {
	
	protected String username;
	protected Integer pid;
	protected String role;
	protected Element add = new Element("adduser");
	
	public UserToProjectResponse( String name, Integer projectid ) {
		super();
		this.username = name;
		this.pid = projectid;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		
		// unterbaum aufbauen
		teamfound.addContent(add);
		Element name = new Element("user");
		add.addContent(name);
		Element id = new Element("projectid");
		add.addContent(id);
		
		// inhalte hinzufügen
		name.addContent(this.username);
		id.addContent(this.pid.toString());

		// fertig
		return d;
	}

	public void setRoleAdmin()
	{
		Element role = new Element("role");
		add.addContent(role);
		role.addContent("projectadmin");
	}
	public void setRoleUser()
	{
		Element role = new Element("role");
		add.addContent(role);
		role.addContent("user");
	}
	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
