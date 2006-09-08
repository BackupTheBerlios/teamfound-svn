/*
 * Created on Mar 25, 2006
 */
package controller.tests;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.response.SearchResponse;

public class SearchResultTest {
	public static void main(String[] args) {
		
		String[] kw = {"keyword1", "keyword2"};
		
		SearchResponse result = new SearchResponse(null, kw);
		result.testResultAdd();
		
		Document d = result.getXML();
		
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		System.out.println(xmlout.outputString(d));
		
	}
}
