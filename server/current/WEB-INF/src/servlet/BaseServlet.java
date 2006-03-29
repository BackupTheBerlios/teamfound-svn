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

import controller.Controller;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.response.ErrorResponse;
import controller.response.Response;
import controller.teamfound.TeamFoundController;

public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = -2766879274061221595L;

	protected Controller ctrl;
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
		commands.put("getprojects", new Integer(5));
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
			resp = launchCommand(request);
		} catch(Exception e) {
			resp = new ErrorResponse(null);
			resp.returnValue(-1, "Internal Error: "+e.getClass());
			e.printStackTrace();
		}
		
		if(!params.containsKey("wants")) {		
			xmlResponse(response, resp);
			
		} else {
			if(request.getParameter("wants").equals("html")) {
				htmlResponse(response, resp);
			} else {
				xmlResponse(response, resp);
			}
		}
	}
		
		
	protected Response launchCommand(HttpServletRequest req) throws IndexAccessException, DownloadFailedException {
		Response r;
		// parameter validieren
		Map params = req.getParameterMap();
		if(!params.containsKey("wants")) {
			r = new ErrorResponse(null);
			r.returnValue(2, "Need Parameter 'wants'");
			return r;
		}
		
		if(!params.containsKey("version")) {
			r = new ErrorResponse(null);
			r.returnValue(2, "Need Parameter 'version'");
			return r;
		} else {
			if(!req.getParameter("version").equals("2")) {
				r = new ErrorResponse(null);
				r.returnValue(3, "Incompatible Interface Version");
				return r;
			}
		}
		
		if(!params.containsKey("command")) {
			r = new ErrorResponse(null);
			r.returnValue(2, "Need Parameter 'command'");
			return r;
		}
		
		// parameter scheinen erstmal ok, schauen wir ob wir das command kennen
		String cmd = req.getParameter("command");
		
		// kommando existent?
		if(!commands.containsKey(cmd)) {
			r = new ErrorResponse(null);
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
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'keyword'");
				return r;
			} else {
				query = req.getParameter("keyword");
			}
			
			if(!params.containsKey("offset")) {
				offset = 0;				
			} else {
				offset = Integer.parseInt(req.getParameter("keyword"));
			}			
			String[] rawcat;
			if(!params.containsKey("category")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'category'");
				return r;		
			} else {
				rawcat = req.getParameterValues("category");
				int[] categories = new int[rawcat.length];
				
				for(int h = 0; h < rawcat.length; h++) {
					categories[h] = Integer.parseInt(rawcat[h]);
				}
				return ctrl.search(query, offset, categories);
			}			

			
			
		case 2:
			String url;
			// suche, es müssen die parameter category und url da sein
			if(!params.containsKey("url")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'url'");
				return r;
			} else {
				url = req.getParameter("url");
			}
			
			String[] rawcat2;
			if(!params.containsKey("category")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'category'");
				return r;		
			} else {
				rawcat2 = req.getParameterValues("category");
				int[] categories = new int[rawcat2.length];
				
				for(int h = 0; h < rawcat2.length; h++) {
					categories[h] = Integer.parseInt(rawcat2[h]);
				}
				return ctrl.addToIndex(url, categories);
			}			
			
			
			
		case 3:
			// getCategories (für eine kategorie)
			int projectID;
			if(!params.containsKey("projectID")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'projectID'");
				return r;
			} else {
				projectID = Integer.parseInt(req.getParameter("projectID"));
			}
			return ctrl.getCategories(projectID);
			
		case 4:
			String name, description;
			int parentID;
			// addCategories
			if(!params.containsKey("name")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'name'");
				return r;
			} else {
				name = req.getParameter("name");
			}			

			if(!params.containsKey("description")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'description'");
				return r;
			} else {
				description = req.getParameter("description");
			}			
			
			if(!params.containsKey("subcategoryof")) {
				r = new ErrorResponse(null);
				r.returnValue(2, "Need Parameter 'subcategoryof'");
				return r;
			} else {
				parentID = Integer.parseInt(req.getParameter("subcategoryof"));
			}			
			
			return ctrl.addCategory(name, parentID, description);
			
		case 5:
			return ctrl.getProjects();
			
			
			
		}
		
		r = new ErrorResponse(null);
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
