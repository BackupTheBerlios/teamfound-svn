/*
 * Created on Mar 27, 2006
 */
package controller.response;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;

public class GetCategoriesResponse extends Response {

	protected Map<Integer, List<Tuple<Integer, Element>>> categories;
	
	protected Element root;
	
	public GetCategoriesResponse() {
		categories = new HashMap<Integer, List<Tuple<Integer, Element>>>();
	}
	
	/**
	 * Generiert das Antwort-XML
	 */	
	public Document getXML() {
		Document d = super.getBaseDocument();
		Element xmlroot = d.getRootElement();
		Element getcat = new Element("getcategories");
		xmlroot.addContent(getcat);
		
		// hole root
		Element root = this.root;
		getcat.addContent(root);
		saveSubcategories(root, new Integer(0));

		return d;
	}
	
	
	private void saveSubcategories(Element el, Integer id) {
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
		} // else: rekursionsabbruch
	}

	/**
	 * Speichert eine Kategorie in der Antwort
	 * @param name
	 * @param description
	 * @param ID
	 * @param parentID
	 */
	public void addCategory(String name, String description, Integer ID, Integer parentID) {
		
		// unterbaum aufbauen
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
		
		if(ID.intValue() == 0) {
			this.root = cat;
		} else {
			// speichern, damit später der richtige kategoriebaum aufgebaut werden kann
			if(categories.containsKey(parentID)) {
				List<Tuple<Integer, Element>> l = categories.get(parentID);
				l.add(new Tuple<Integer, Element>(parentID, cat));
			} else {
				List<Tuple<Integer, Element>> l = new LinkedList<Tuple<Integer, Element>>();
				l.add(new Tuple<Integer, Element>(parentID, cat));
				categories.put(parentID, l);
			}
		}
	}
}
