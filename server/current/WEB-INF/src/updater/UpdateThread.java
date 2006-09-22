/*
 * Created on Sep 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package updater;

import index.NewIndexEntry;
import index.crawler.teamfound.TeamFoundCrawler;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import controller.teamfound.TeamFoundController;

/**
 * @author jonas
 *
 */
public class UpdateThread extends Thread {

	protected boolean running;
	protected TeamFoundCrawler crawler;
	protected TeamFoundController ctrl;
	protected Logger log;

	
	public UpdateThread() {
		super();
		
		// TODO max frame fetch depth aus den proerties holen
		crawler = new TeamFoundCrawler(3);
		// TODO controller richtig konfigurieren
		ctrl = new TeamFoundController(null);
		
		log = Logger.getLogger("update");
	}

	public void run() {
		try {
			// wake up every couple minutes and update pending documents 
			NewIndexEntry doc;
			while(running) {
				/*
				 * 1. get a list of pages to update
				 */
				List<URL> pages = null;
				
				Iterator<URL> i = pages.iterator();
				
				while(i.hasNext()) {
					URL url = i.next();
					// TODO wie sieht das mit den kategorien aus?
					try {
						doc = crawler.fetch(url);
						switch(ctrl.updateDocument(doc)) {
						case 1:
							log.info("Updated url "+doc.getUrl().toString());
							break;
						case 0:
							log.info("Url "+doc.getUrl().toString()+" has not changed");
							break;
						case -1:
							log.error("Error while updating url "+doc.getUrl().toString()+" see tf-controller-log for more info");
							break;
						}
					} catch(IOException e) {
						log.error(e);
					}
				}
				
				// TODO diesen wert in die properties ziehen
				sleep(3600);
			}
		} catch(InterruptedException e) {
			// thread wurde angehalten, er kann einfach beendet werden
		}
	}

}
