/*
 * Created on Mar 27, 2006
 */
package controller.response;

import java.util.List;

import org.jdom.Document;

import tools.Tuple;


public class ErrorResponse extends Response {

	public ErrorResponse(List<Tuple<Integer, Integer>> projectCounters) {
		super(projectCounters);
	}

	public Document getXML() {
		return super.getBaseDocument();
	}

	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return "not yet implemented";
	}

}
