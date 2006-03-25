/*
 * Created on Dec 6, 2005
 */
package controller;

import controller.DownloadFailedException;
import controller.IndexAccessException;

import config.Config;

/**
 * Interface f�r einen Controller nach Milestone2-Spezifikation
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 */
public interface Controller {

	
	/**
	 * Eine URL zum Index hinzuf�gen
	 * 
	 * @todo Kategorien hinzuf�gen
	 */
	public void addToIndex(String url) throws DownloadFailedException, IndexAccessException;
	
	/**
	 * Nach einer Query im Index suchen
	 * 
	 * @todo Das Ergebnis m�sste genauer spezifiziert werden, siehe Interface SearchResult
	 * @see controller.SearchResponse
	 */
	public SearchResponse search(String query) throws IndexAccessException;

	
}

