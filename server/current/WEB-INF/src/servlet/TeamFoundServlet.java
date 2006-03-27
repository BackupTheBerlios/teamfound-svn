/*
 * Created on Mar 27, 2006
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.response.ErrorResponse;
import controller.response.Response;
import controller.teamfound.TeamFoundController;

public class TeamFoundServlet extends HttpServlet {
	private static final long serialVersionUID = -2766879274061221595L;

	protected TeamFoundController ctrl;
	protected XMLOutputter xmlout;
	protected Map<String, Integer> commands;
	
	
	

	public void init() throws ServletException {
		ctrl = new TeamFoundController();
		xmlout = new XMLOutputter(Format.getPrettyFormat());
		
		commands = new HashMap<String, Integer>();
		
		commands.put("search", new Integer(1));
		commands.put("addpage", new Integer(2));
		commands.put("getcategories", new Integer(3));
		commands.put("addcategories", new Integer(4));
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		Map params = request.getParameterMap();
		Response resp;
		try {
			resp = launchCommand(params);
		} catch(Exception e) {
			resp = new ErrorResponse();
			resp.returnValue(-1, "Internal Error: "+e.getClass());
		}
		
		if(!params.containsKey("wants")) {		
			xmlResponse(response, resp);
			
		} else {
			if(params.get("wants").equals("html")) {
				htmlResponse(response, resp);
			} else {
				xmlResponse(response, resp);
			}
		}
	}
		
		
	protected Response launchCommand(Map params) throws IndexAccessException, DownloadFailedException {
		Response r;
		// parameter validieren
		
		if(!params.containsKey("wants")) {
			r = new ErrorResponse();
			r.returnValue(2, "Need Parameter 'wants'");
			return r;
		}
		
		if(!params.containsKey("version")) {
			r = new ErrorResponse();
			r.returnValue(2, "Need Parameter 'version'");
			return r;
		}
		
		if(!params.containsKey("command")) {
			r = new ErrorResponse();
			r.returnValue(2, "Need Parameter 'command'");
			return r;
		} else {
			if(!params.get("command").equals("2")) {
				r = new ErrorResponse();
				r.returnValue(3, "Incompatible Interface Version: "+params.get("command"));
				return r;
			}
		}
		
		// parameter scheinen erstmal ok, schauen wir ob wir das command kennen
		String cmd = (String)params.get("command");
		
		// kommando existent?
		if(!commands.containsKey(cmd)) {
			r = new ErrorResponse();
			r.returnValue(-1, "Command not found");
			return r;
		}
		Integer i = commands.get(cmd);
		
		switch(i.intValue()) {
		case 1:
			String query;
			int offset;
			// suche, es müssen die parameter keyword und offset da sein
			if(!params.containsKey("keyword")) {
				r = new ErrorResponse();
				r.returnValue(2, "Need Parameter 'keyword'");
				return r;
			} else {
				query = (String)params.get("keyword");
			}
			
			if(!params.containsKey("offset")) {
				offset = 0;				
			} else {
				offset = Integer.parseInt((String)params.get("keyword"));
			}			
			return ctrl.search(query, offset);
			
		case 2:
			String url;
			int category;
			// suche, es müssen die parameter category und url da sein
			if(!params.containsKey("url")) {
				r = new ErrorResponse();
				r.returnValue(2, "Need Parameter 'url'");
				return r;
			} else {
				url = (String)params.get("url");
			}
			
			if(!params.containsKey("category")) {
				category = 0;				
			} else {
				category = Integer.parseInt((String)params.get("category"));
			}			
			return ctrl.addToIndex(url, category);
			
		case 3:
			// keine weiteren parameter
			return ctrl.getCategories();
			
		case 4:
			String name, description;
			int parentID;
			// addCategories
			if(!params.containsKey("name")) {
				r = new ErrorResponse();
				r.returnValue(2, "Need Parameter 'name'");
				return r;
			} else {
				name = (String)params.get("name");
			}			

			if(!params.containsKey("description")) {
				r = new ErrorResponse();
				r.returnValue(2, "Need Parameter 'description'");
				return r;
			} else {
				description = (String)params.get("description");
			}			
			
			if(!params.containsKey("subcategoryof")) {
				r = new ErrorResponse();
				r.returnValue(2, "Need Parameter 'subcategoryof'");
				return r;
			} else {
				parentID = Integer.parseInt((String)params.get("subcategoryof"));
			}			
			
			return ctrl.addCategory(name, parentID, description);
		}
		
		r = new ErrorResponse();
		r.returnValue(-1, "Unknown command");
		return r;
	}
	
	
	protected void xmlResponse(HttpServletResponse response, Response tfResponse) throws IOException {
		Document d = tfResponse.getXML();
		response.setContentType("application/xml");
		PrintWriter out = response.getWriter();
		out.println(xmlout.outputString(d));
	}
	

	
	private void htmlResponse(HttpServletResponse response, Response resp) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println(resp.getHTML());
		
	}

}
