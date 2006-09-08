/*
 * Created on Mar 25, 2006
 */
package controller.response;

import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import tools.Tuple;

/**
 * Implementiert die Rückgabe des Servers
 * 
 * Dabei werden folgende Werte bereits in dieser Version gesetzt:
 * - interface-version
 * - server (name+version)
 * 
 * Werden keine anderen Werte gesetzt, so wird return-value auf 0, return-description auf OK gesetzt
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */


public abstract class Response {
	
	protected Document doc;
	
	protected int returnStatus = 0;
	protected String returnDescription;
	
	protected List<Tuple<Integer, Integer>> pCounters;
	
	public Response(List<Tuple<Integer,Integer>> projectCounters) {
		pCounters = projectCounters;
	}
	
	
	/**
	 * Gibt das Suchergebnis als XML nach Milestone2-Spezifikation zurück
	 * @return
	 */
	public Document getBaseDocument() {
		
		Element root = new Element("response");
		doc = new Document(root);
		
		Element interfaceVersion = new Element("interface-version");
		interfaceVersion.addContent("2");
		root.addContent(interfaceVersion);
		
		Element server = new Element("server");
		Element name = new Element("name");
		name.addContent("TeamFound");
		server.addContent(name);
		Element version = new Element("version");
		version.addContent("0.2");
		server.addContent(version);
		root.addContent(server);
		
		// return-values
		Element retValue = new Element("return-value");
		retValue.addContent(Integer.toString(returnStatus));
		root.addContent(retValue);
		
		// projectCounter
		if(pCounters != null) {
			Element pc = new Element("project-counter");
			Iterator<Tuple<Integer, Integer>> i = pCounters.iterator();
			
			root.addContent(pc);
			
			Element project, projectID, projectC;
			Tuple<Integer, Integer> t;
			while(i.hasNext()) {
				t = i.next();
				
				project = new Element("project");
				projectID = new Element("projectID");
				projectC = new Element("count");
				project.addContent(projectID);
				project.addContent(projectC);
				
				projectID.addContent(t.getFirst().toString());
				projectC.addContent(t.getSecond().toString());
				
				pc.addContent(project);
			}
		}
		
		Element retDescr = new Element("return-description");
		if(returnDescription == null) {
			retDescr.addContent("OK");
		} else {
			retDescr.addContent(returnDescription);
		}
		root.addContent(retDescr);
		
		return doc;
		
	}
	
	public void returnValue(int code, String description) {
		returnStatus = code;
		returnDescription = description;
	}
	
	public abstract Document getXML();

	public abstract String getHTML();

}
