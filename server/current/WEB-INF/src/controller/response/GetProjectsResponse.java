/*
 * Created on Mar 29, 2006
 */
package controller.response;

import java.util.*;
import org.jdom.*;
import tools.Tuple;
import db.dbbeans.projectdataBean;
import controller.SessionData;

public class GetProjectsResponse extends Response {

	protected List<Element> projects;
	
	public GetProjectsResponse() {
		super();
		projects = new LinkedList<Element>();
	}

	private static String boolean2text(Boolean b)
	{
		if(b.booleanValue())
			return "yes";
		return "no";
	}
	
	public void addProject(String pname, String pdescription, Integer pID, Integer ver, SessionData session) {
		Element project = new Element("project");
		
		Element name = new Element("name");
		Element description = new Element("description");
		Element ID = new Element("id");
		Element version = new Element("version");
		
		project.addContent(name);
		project.addContent(description);
		project.addContent(ID);
		project.addContent(version);
		
		name.addContent(pname);
		description.addContent(pdescription);
		ID.addContent(pID.toString());
		if( ver != null)
		{
			version.addContent(ver.toString());
		}


		// RECHTE ausgeben
		projectdataBean pdb = SessionData.projectdata.get(pID);

		// user rechte
		if( session != SessionData.guest)
		{
			if( session.urb.isUser(pID) || session.urb.isAdmin(pID))
			{
				// ok, ist user :)
				Element useruseradd = new Element("useruseradd");
				useruseradd.addContent(boolean2text(pdb.getUserUseradd()));
				project.addContent(useruseradd);

				Element userurledit = new Element("userurledit");
				userurledit.addContent(boolean2text(pdb.getUserUrledit()));
				project.addContent(userurledit);

				Element usercatedit = new Element("usercatedit");
				usercatedit.addContent(boolean2text(pdb.getUserCatedit()));
				project.addContent(usercatedit);

				Element useraddurl = new Element("useraddurl");
				useraddurl.addContent(boolean2text(pdb.getUserAddurl()));
				project.addContent(useraddurl);

				Element useraddcat = new Element("useraddcat");
				useraddcat.addContent(boolean2text(pdb.getUserAddcat()));
				project.addContent(useraddcat);

				Element isadmin = new Element("isadmin");
				isadmin.addContent(boolean2text(session.urb.isAdmin(pID)));
				project.addContent(isadmin);

			}
		}

		// guest rechte
		Element guestread = new Element("guestread");
		guestread.addContent(boolean2text(pdb.getGuestRead()));
		project.addContent(guestread);

		Element guesturledit = new Element("guesturledit");
		guesturledit.addContent(boolean2text(pdb.getGuestUrledit()));
		project.addContent(guesturledit);

		Element guestcatedit = new Element("guestcatedit");
		guestcatedit.addContent(boolean2text(pdb.getGuestCatedit()));
		project.addContent(guestcatedit);

		Element guestaddurl = new Element("guestaddurl");
		guestaddurl.addContent(boolean2text(pdb.getGuestAddurl()));
		project.addContent(guestaddurl);

		Element guestaddcat = new Element("guestaddcat");
		guestaddcat.addContent(boolean2text(pdb.getGuestAddcat()));
		project.addContent(guestaddcat);
		
		projects.add(project);
	}
	
	
	@Override
	public Document getXML() {
		
		Document d = super.getBaseDocument();
		
		Element projects = new Element("projects");
		
		Iterator<Element> li = this.projects.iterator();

		while(li.hasNext()) {
			projects.addContent(li.next());
		}
		
		//Element root = d.getRootElement();
		
		teamfound.addContent(projects);
		
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return null;
	}

}
