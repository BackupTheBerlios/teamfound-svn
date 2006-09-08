/*
 * Created on Mar 29, 2006
 */
package controller.response;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;

public class GetProjectsResponse extends Response {

	protected List<Element> projects;
	
	public GetProjectsResponse(List<Tuple<Integer,Integer>> projectCounters) {
		super(projectCounters);
		projects = new LinkedList<Element>();
	}
	
	public void addProject(String pname, String pdescription, Integer pID) {
		Element project = new Element("project");
		
		Element name = new Element("name");
		Element description = new Element("description");
		Element ID = new Element("id");
		
		project.addContent(name);
		project.addContent(description);
		project.addContent(ID);
		
		name.addContent(pname);
		description.addContent(pdescription);
		ID.addContent(pID.toString());
		
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
		
		Element root = d.getRootElement();
		
		root.addContent(projects);
		
		return d;
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return null;
	}

}
