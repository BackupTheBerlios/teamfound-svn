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

import config.teamfound.TeamFoundConfig; 

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
	
		String depth = TeamFoundConfig.getConfValue("framedepth");
		if(depth != null)
		{
			crawler = new TeamFoundCrawler((new Integer(depth)).intValue());
		}
		else
			crawler = new TeamFoundCrawler(3);
		
		// TODO controller richtig konfigurieren
		ctrl = new TeamFoundController();
		
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
				List<URL> pages = ctrl.getOldURL();
				
				Iterator<URL> i = pages.iterator();
				
				while(i.hasNext()) {
					URL url = i.next();
					// TODO wie sieht das mit den kategorien aus?
				//	try {
						//doc = crawler.fetch(url);
						//switch(ctrl.updateDocument(doc)) {
						switch(ctrl.updateDocument(url)) {
						case 1:
							//log.info("Updated url "+doc.getUrl().toString());
							log.info("Updated url "+url.toString());
							break;
						case 0:
							//log.info("Url "+doc.getUrl().toString()+" has not changed");
							log.info("Url "+url.toString()+" has not changed");
							break;
						case -1:
							//log.error("Error while updating url "+doc.getUrl().toString()+" see tf-controller-log for more info");
							log.error("Error while updating url "+url.toString()+" see tf-controller-log for more info");
							break;
						}
				//	} catch(IOException e) {
				//		log.error(e);
				//	}
				}
				
				String wait = TeamFoundConfig.getConfValue("updatewait");
				if(wait != null)
				{
					sleep((new Long(wait)).longValue()*60*1000);
				}
				else
					// TODO diesen wert in die properties ziehen
					sleep(300000);
			}
		} catch(InterruptedException e) {
			// thread wurde angehalten, er kann einfach beendet werden
		}
	}

}
