/*
 * Created on Dec 6, 2005
 */
package controller;

/**
 * Erste Idee für eine Suchergebnis-Rückgabe, müsste noch genauer drüber geredet werden
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */
public interface SearchResult {

	/**
	 * Gibt das Suchergebnis als XML nach Milestone2-Spezifikation zurück
	 * @return
	 */
	public String getXml();
	
	/**
	 * Gibt das Suchergebnis als HTML (nter Verwendung des Standardtemplates) zurück
	 * @return
	 */
	public String getHtml();
	
	/**
	 * Gibt das Suchergebnis als Html unter Verwendung des angegebenen Templates zurück
	 * 
	 * @param template XSL-Template welches Benutzt werden soll
	 * @return
	 */
	public String getHtml(String template);
}
