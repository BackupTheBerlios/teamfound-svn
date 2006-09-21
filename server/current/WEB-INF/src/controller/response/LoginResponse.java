/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;


public class LoginResponse extends Response {
	
	protected String username;
	protected String sesskey;
	
	public LoginResponse(String name, String sesskey ) {
		super();
		this.username = name;
		this.sesskey = sesskey;
	}
	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		//setSessionUserName(username);
		//setSessionKey(sesskey);

		// unterbaum aufbauen
		Element login = new Element("login");
		teamfound.addContent(login);
		Element user = new Element("user");
		login.addContent(user);
		
		// inhalte hinzufügen
		user.addContent(this.username);
		
		// fertig
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
