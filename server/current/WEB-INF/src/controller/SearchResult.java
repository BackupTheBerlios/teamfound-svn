/*
 * Created on Dec 6, 2005
 */
package controller;

/**
 * Erste Idee f�r eine Suchergebnis-R�ckgabe, m�sste noch genauer dr�ber geredet werden
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */
public interface SearchResult {

	/**
	 * Gibt das Suchergebnis als XML nach Milestone2-Spezifikation zur�ck
	 * @return
	 */
	public String getXml();
	
	/**
	 * Gibt das Suchergebnis als HTML (nter Verwendung des Standardtemplates) zur�ck
	 * @return
	 */
	public String getHtml();
	
	/**
	 * Gibt das Suchergebnis als Html unter Verwendung des angegebenen Templates zur�ck
	 * 
	 * @param template XSL-Template welches Benutzt werden soll
	 * @return
	 */
	public String getHtml(String template);
}
