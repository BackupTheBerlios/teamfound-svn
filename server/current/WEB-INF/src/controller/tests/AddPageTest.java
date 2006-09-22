package controller.tests;

import index.NewIndexEntry;
import index.crawler.CrawlerHTTPException;
import index.crawler.teamfound.TeamFoundCrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import controller.Controller;
import controller.teamfound.TeamFoundController;

public class AddPageTest {
	public static void main(String[] args) throws CrawlerHTTPException, MalformedURLException, IOException, NoSuchAlgorithmException {
		tests.Config.testConfig();
		Logger log = Logger.getLogger("test");
		
		log.info("test #1 - simple page without frameset");
		TeamFoundCrawler c = new TeamFoundCrawler(3);
		NewIndexEntry cd = c.fetch(new URL("http://localhost/~jonas"));
		
		TeamFoundController ctrl = new TeamFoundController(null);
		
		ctrl.updateDocument(cd);
		
	}
}
