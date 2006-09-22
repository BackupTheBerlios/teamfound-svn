/*
 * Created on Sep 22, 2006
 */
package controller.response;

import java.util.*;
import db.dbbeans.*;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class GetUsersOfProjectResponse extends Response {
	
	protected Vector<Tuple<tfuserBean,Boolean>> users;
	protected Integer projectid;
	
	public GetUsersOfProjectResponse( Integer _projectid) {
		super();
		projectid=_projectid;
		users = new Vector<Tuple<tfuserBean,Boolean>>();
	}

	public void addUser(tfuserBean user, Boolean isadmin)
	{
		users.add(new Tuple(user, isadmin));
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		
		// <getuser>
		//   <projectid>45</projectid>
		//   <user>
		//      <name>hans</name>
		//      <id>45</id>
		//      <role>user</role>
		//   </user>
		//   <user>
		//      <name>jan</name>
		//      <id>0</id>
		//      <role>admin</role>
		//   <user>
		// </getuser>
		//
		Element xmlgetuser = new Element("getuser");
		teamfound.addContent(xmlgetuser);

		Element xmlprojectid = new Element("projectid");
		xmlprojectid.addContent(projectid.toString());
		xmlgetuser.addContent(xmlprojectid);


		Iterator it = users.iterator();

		while(it.hasNext())
		{
			Tuple t = (Tuple)it.next();
			tfuserBean user = (tfuserBean)t.getFirst();
			Boolean role = (Boolean)t.getSecond();

			Element xmluser = new Element("user");
			xmlgetuser.addContent(xmluser);

			Element xmlname = new Element("name");
			xmlname.addContent(user.getUsername());
			xmluser.addContent(xmlname);

			Element xmlid = new Element("id");
			xmlid.addContent(user.getID().toString());
			xmluser.addContent(xmlid);

			Element xmlrole = new Element("role");
			xmlrole.addContent(role.booleanValue() ? "admin" : "user");
			xmluser.addContent(xmlrole);

		}

		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
