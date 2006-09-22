package index.crawler.tests;

import index.NewIndexEntry;
import index.crawler.CrawlerHTTPException;
import index.crawler.teamfound.TeamFoundCrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;



public class CrawlerTest {
	
	public static void main(String[] args) throws CrawlerHTTPException, MalformedURLException, IOException {
		// setup test environment
		tests.Config.testConfig();
		
		Logger log = Logger.getLogger("test");
		
		log.info("test #1 - simple page without frameset");
		TeamFoundCrawler c = new TeamFoundCrawler(3);
		NewIndexEntry cd = c.fetch(new URL("http://localhost/~jonas"));
		
		System.out.println(cd.getContent());
		
		log.info("test #2 - frameset with two frames");
		
		NewIndexEntry cd2 = c.fetch(new URL("http://localhost/~jonas/tf_tests/frameset.html"));
		
		System.out.println(cd2.getContent());
		
		
		log.info("test #3 - testing 404 error");
		try {
			NewIndexEntry cd3 = c.fetch(new URL("http://localhost/~jonas/thispagedoesnotexist"));	
			
		} catch(Exception e) {
			log.info("catched "+e.getClass()+" with message: "+e.getMessage());
			e.printStackTrace();
		}
		
		log.info("test #4 - testing 401/403 error");
		try {
			NewIndexEntry cd3 = c.fetch(new URL("http://localhost/phpmyadmin"));	
			
		} catch(Exception e) {
			log.info("catched "+e.getClass()+" with message: "+e.getMessage());
			e.printStackTrace();
		}
		
		
		log.info("test #5 - testing unknown domain");
		try {
			NewIndexEntry cd3 = c.fetch(new URL("http://odhvcdhvkjdfhvjkdfhvkjdhfkvhdfv.hdh"));	
			
		} catch(Exception e) {
			log.info("catched "+e.getClass()+" with message: "+e.getMessage());
		}
	}
}
