/*
 * Created on Dec 6, 2005
 */
package controller;

import java.io.IOException;

import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.HitIterator;
import org.apache.lucene.document.Document;
import org.jdom.Element;
import java.util.Vector;
import java.util.Iterator;

/**
 * Erste Idee f�r eine Suchergebnis-R�ckgabe, m�sste noch genauer dr�ber geredet werden
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 *
 */
public class SearchResponse extends Response {

	protected String[] keywords;
	protected int offset = 0;
	protected int number = 30;
	
	protected Element result;
	
	public SearchResponse(String[] keywords) {
		this.keywords = keywords;
		
		
		createResultElement();
	}
	
	public SearchResponse(String[] keywords, int offset, int number) {
		this.keywords = keywords;
		this.offset = offset;
		this.number = number;
		
		createResultElement();
	}
	
	protected void createResultElement() {
		result = new Element("result");
		
		Element offset = new Element("offset");
		offset.addContent(Integer.toString(this.offset));
		result.addContent(offset);
		
		Element count = new Element("count");
		count.addContent(Integer.toString(this.number));
		result.addContent(count);
	}
	
	public void addSearchResults(Vector<Document> hits) throws IOException {
		Iterator it = (Iterator)hits.iterator();
		
		
		//Hit h;
		Document d;
		Element found, url, title, category;
		while(it.hasNext()) {
			d = (Document)it.next();
			found = new Element("found");
			url = new Element("url");
			
			// Felder im Document see SimpleNewIndexEntry.java
			// 1. url -- Die Url als String
			// 2. contents -- die indezierten Suchwoerter die sich aus der Seite ergaben
			// 3. summary  -- ein String den der HtmlParser geliefert hat
			// 4. title  -- wie 3.
			// 5. cats -- von uns tokenized CategoryIds
			
			url.addContent(d.get("url"));
			found.addContent(url);
			
			title = new Element("title");
			title.addContent(d.get("title"));
			found.addContent(title);
			
			//hmm nicht auf die  schnelle, muessen wir wahrscheinlich den String der im 
			//Feld steht aufsplitten oder aus db lesen
			category = new Element("incategory");
			category.addContent("1");
			found.addContent(category);
			
			this.result.addContent(found);
		}
	}
	
	public void testResultAdd() {
		
		Element found, url, title, category;
		for(int i = 0; i < 3; i++) {

			found = new Element("found");
			url = new Element("url");
			url.addContent("http://www.example.com");
			found.addContent(url);
			
			title = new Element("title");
			title.addContent("Sample Title - Fetching real results is not yet implemented");
			found.addContent(title);
			
			category = new Element("incategory");
			category.addContent("1");
			found.addContent(category);
			
			this.result.addContent(found);
		}
		
	}
	
	public org.jdom.Document getXML() {
		Element search = new Element("search");
		Element keywords = new Element("keywords");
		search.addContent(keywords);
		
		Element keyword;
		for(int i = 0; i < this.keywords.length; i++) {
			keyword = new Element("word");
			keyword.addContent(this.keywords[i]);
			keywords.addContent(keyword);
		}
		
		search.addContent(this.result);
		
		
		org.jdom.Document doc = super.getXml();
		Element root = doc.getRootElement();
		
		root.addContent(search);
		
		return doc;
	}
	
}
