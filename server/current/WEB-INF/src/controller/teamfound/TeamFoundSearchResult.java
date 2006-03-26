/*
 * Martin Klink
 * Created on Mar 21, 2006
 */
package controller.teamfound;

import controller.SearchResponse;
import controller.SearchResult;

public class TeamFoundSearchResult implements SearchResult {

	
	public TeamFoundSearchResult() 
	{
		
	}
	
	/**
	 * Gibt das Suchergebnis als XML nach Milestone2-Spezifikation zurück
	 * @return
	 */
	public String getXml()
	{
		return(new String());
	}
	
	/**
	 * Gibt das Suchergebnis als HTML (nter Verwendung des Standardtemplates) zurück
	 * @return
	 */
	public String getHtml()
	{
		return(new String());
	}
	
	/**
	 * Gibt das Suchergebnis als Html unter Verwendung des angegebenen Templates zurück
	 * 
	 * @param template XSL-Template welches Benutzt werden soll
	 * @return
	 */
	public String getHtml(String template)
	{

		return(new String());
	}
	

}
