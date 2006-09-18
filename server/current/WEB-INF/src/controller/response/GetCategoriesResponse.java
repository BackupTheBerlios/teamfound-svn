/*
 * Created on Mar 27, 2006
 */
package controller.response;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;

public class GetCategoriesResponse extends Response {

	protected Map<Integer, List<Tuple<Integer, Element>>> categories;
	
	protected Element root;

	private Integer rootID;
	
	public GetCategoriesResponse(HashSet<Tuple<Integer,Integer>> projectCounters) {
		super(projectCounters);
		categories = new HashMap<Integer, List<Tuple<Integer, Element>>>();
	}
	
	/**
	 * Generiert das Antwort-XML
	 */	
	public Document getXML() {
		Document d = super.getBaseDocument();
		//Element xmlroot = d.getRootElement();
		Element getcat = new Element("getcategories");
		teamfound.addContent(getcat);
		
		// hole root
		Element root = this.root;
		getcat.addContent(root);
		saveSubcategories(root, rootID);

		return d;
	}
	
	
	private void saveSubcategories(Element el, Integer id) {
		System.out.println("handling category "+id);
		// haben wir überhaupt unterkategorien?
		if(categories.containsKey(id)) {
			Element subcat = new Element("subcategories");
			List<Tuple<Integer, Element>> l = categories.get(id);
			Iterator<Tuple<Integer, Element>> i = l.iterator();
			while(i.hasNext()) {
				Tuple<Integer, Element> t = i.next();
				subcat.addContent(t.getSecond());
				saveSubcategories(t.getSecond(), t.getFirst());
			}
			el.addContent(subcat);			
		} // else: rekursionsabbruch
		else {
			System.out.println("no subcats");
		}
	}

	protected Element getElement(String name, String description, Integer ID, Integer parentID) {
//		 unterbaum aufbauen
		Element cat = new Element("category");
		Element elname = new Element("name");
		cat.addContent(elname);
		Element eldescription = new Element("description");
		cat.addContent(eldescription);
		Element id = new Element("id");
		cat.addContent(id);
		

		// inhalt einfügen
		elname.addContent(name);
		eldescription.addContent(description);
		id.addContent(ID.toString());
		
		return cat;
	}
	
	/**
	 * Speichert die Wurzel des Baumes
	 * 
	 * Es kann immer nur eine Wurzel pro Antwort gespeichert werden! Bei erneutem Aufruf dieser Methode wird die alte Wurzel überschrieben!
	 * 
	 * @param name
	 * @param description
	 * @param ID
	 * @param parentID
	 */
	public void addRoot(String name, String description, Integer ID, Integer parentID) {
		root = getElement(name, description, ID, parentID);
		rootID = ID;
	}
	
	/**
	 * Speichert eine Kategorie in der Antwort
	 * @param name
	 * @param description
	 * @param ID
	 * @param parentID
	 */
	public void addCategory(String name, String description, Integer ID, Integer parentID) {
		
		Element cat = getElement(name, description, ID, parentID);
		
		
		// speichern, damit später der richtige kategoriebaum aufgebaut werden kann
		if(categories.containsKey(parentID)) {
			List<Tuple<Integer, Element>> l = categories.get(parentID);
			l.add(new Tuple<Integer, Element>(ID, cat));
		} else {
			List<Tuple<Integer, Element>> l = new LinkedList<Tuple<Integer, Element>>();
			l.add(new Tuple<Integer, Element>(ID, cat));
			
			
			categories.put(parentID, l);
		}
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}
}
