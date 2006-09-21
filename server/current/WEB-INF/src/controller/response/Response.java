/*
 * Created on Mar 25, 2006
 */
package controller.response;

import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import controller.response.ReturnCodes;

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
	protected Element root;

	protected Element teamfound; // damit die subklassen direkt darauf zugreifen koennen
	protected Element server; // damit die subklassen direkt darauf zugreifen koennen
	
	protected Integer ServerStatus = new Integer(0);
//	protected Integer SessionStatus = new Integer(0);
	protected Integer TFStatus = new Integer(0);
	protected String ServerDescription = "OK";
//	protected String SessionDescription = "OK";
	protected String TFDescription = "OK";
	
	//protected String username = "guest";
	//protected String sessionkey;
	
	public Response() {

		root = new Element("response");
		doc = new Document(root);
	}

	public void addElementToRoot(Element xml)
	{
		root.addContent(xml);
	}
	
	
	/**
	 * Gibt das Suchergebnis als XML nach Milestone2-Spezifikation zurück
	 * @return
	 */
	public Document getBaseDocument() {
		
				
		// Add XSLT-Reference
		HashMap piMap = new HashMap( 2 );
		piMap.put( "type", "text/xsl" );
		piMap.put( "href", "transform.xsl" );
		ProcessingInstruction pi = new ProcessingInstruction( "xml-stylesheet", piMap );
		doc.getContent().add( 0, pi );
				
		// Create First-Level-Nodes
		server = new Element("server");
		root.addContent(server);
		teamfound = new Element("teamfound");
		root.addContent(teamfound);

		// SERVER-BLOCK

		Element name = new Element("name");
		name.addContent("TeamFound");
		server.addContent(name);
		Element version = new Element("version");
		version.addContent("0.3");
		server.addContent(version);
		Element interfaceVersion = new Element("interface-version");
		interfaceVersion.addContent("3");
		server.addContent(interfaceVersion);
		
		Element serverstatus = new Element("return-value");
		serverstatus.addContent(ServerStatus.toString());
		server.addContent(serverstatus);
		
		Element serverdesc = new Element("return-description");
		serverdesc.addContent(ServerDescription);
		server.addContent(serverdesc);

		// TEAMFOUND-BLOCK
				
		// return-values
		Element retValue = new Element("return-value");
		retValue.addContent(TFStatus.toString());
		teamfound.addContent(retValue);
		
		Element retDescr = new Element("return-description");
		retDescr.addContent(TFDescription);
		teamfound.addContent(retDescr);
		
		return doc;
	}
	
	public void serverReturnValue(Integer code, String desc) {
		ServerStatus = code;
		ServerDescription= desc; 
	}
/*	public void sessionReturnValue(Integer code) {
		SessionStatus = code;
		SessionDescription= ReturnCodes.getDescription(code);
	}*/
	public void tfReturnValue(Integer code) {
		TFStatus= code;
		TFDescription= ReturnCodes.getDescription(code);
	}
/*	public void setSessionUserName(String n) {
		username = n;
	}*/
/*	public void setSessionKey(String s) {
		sessionkey = s;
	}*/

	public abstract Document getXML();

	public abstract String getHTML();

}
