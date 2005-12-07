/*
 * Created on Dec 6, 2005
 */
package index.teamfound;

import java.net.URL;

import sync.ReadWriteSync;

import controller.IndexAccessException;
import controller.SearchResult;
import index.Indexer;
import index.NewIndexEntry;

/**
 * Indexer für Milestone2-Spezifikation
 * 
 * Benutzt Apache-Lucene als unterliegende Bibliothek
 * 
 * Dieses Objekt ist Threadsafe, siehe ReadWriteSync
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 * @see sync.ReadWriteSync
 */
public class TeamFoundIndexer implements Indexer {

	protected ReadWriteSync sync;
	
	public TeamFoundIndexer() {
		sync = new ReadWriteSync();
	}
	
	public void addUrl(NewIndexEntry entry, URL adress) throws IndexAccessException {
		try {
			sync.doWrite();
		} catch(InterruptedException e) {
			IndexAccessException e2 = new IndexAccessException("nested InterruptedException");
			e2.initCause(e);
			throw e2;
		}
		
		// *****************
		// hier muss der entsprechende Code zum einfügen in den index hin 
		// *****************
		
		// achtung! mit aufruf der nächsten Zeile endet der write-lock!
		sync.endWrite();

	}

	public SearchResult query(String query) throws IndexAccessException {
		try {
			sync.doRead();
		} catch(InterruptedException e) {
			IndexAccessException e2 = new IndexAccessException("nested InterruptedException");
			e2.initCause(e);
			throw e2;
		}
		
		// *****************
		// hier muss der entsprechende Code zum auslesen aus dem index hin
		// *****************
		
		// achtung! mit aufruf der nächsten Zeile endet der read-lock!
		sync.endRead();	
		
		// hier muss natürlich irgendwann mal das richtige suchergebnis zurückgegeben werden 
		return null;
	}

}
