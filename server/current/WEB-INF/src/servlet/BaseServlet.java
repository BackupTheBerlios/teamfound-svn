/*
 * Created on Mar 27, 2006
 *
 * extendet Milestone 3 13.9.06
 *
 * @author Jonas Heese
 * @author Martin Klink
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.Controller;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.ServerInitFailedException;
import controller.DBAccessException;
import controller.response.ErrorResponse;
import controller.response.Response;
import controller.response.LoginResponse;
import controller.teamfound.TeamFoundController;
import controller.SessionData;

import java.util.Properties;
import config.teamfound.TeamFoundConfig;
import java.io.InputStream;


public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = -2766879274061221595L;
	public static final String interfaceversion = "3";

	protected Controller ctrl;
	protected XMLOutputter xmlout;
	protected Map<String, Integer> commands;

	public void init() throws ServletException {
		
		try 
		{
			// Pfad ist relative zu Servlet root /conf/teamfound.properties wird gesucht 
			InputStream is = getServletContext().getResourceAsStream("/conf/teamfound.properties");
			Properties tfprops  = new Properties();
			tfprops.load(is);
			is.close();
			TeamFoundConfig.initstatic(tfprops);
			ctrl = new TeamFoundController();
		}
		catch (Exception e) 
		{
			ServletException a = new ServletException("Could not read Properties!");
			a.initCause(e);
			throw(a);
		}
			
		xmlout = new XMLOutputter(Format.getPrettyFormat());
		
		commands = new HashMap<String, Integer>();
		
		commands.put("search", new Integer(1));
		commands.put("addpage", new Integer(2));
		commands.put("getcategories", new Integer(3));
		commands.put("addcategory", new Integer(4));
		commands.put("getprojects", new Integer(5));
		
		/*------------------------------
		 * Neue Kommandos Milestone3
		 * -----------------------------*/
		commands.put("register", new Integer(6));
		commands.put("login", new Integer(7));
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		

		//Session auslesen falls eine existiert
		HttpSession session = request.getSession(false);
		//wenn sessin existiert SessionData auslesen
		SessionData tfsession = null;
		if(session != null)
		{	
			tfsession = SessionData.getSessionData(session.getId());
			if( tfsession == null)
			{
				tfsession = SessionData.guest;
			}
		}
		else
		{
			// Gast session initialisieren
			tfsession = SessionData.guest;
		}
		
		Map params = request.getParameterMap();
		Response resp;
		try {
			resp = launchCommand(request, tfsession);
		} catch(Exception e) {
			resp = new ErrorResponse(null);
			resp.serverReturnValue(-1, "Internal Error: "+e.getClass());
			e.printStackTrace();
		}

		// TODO: steht erstmal drinn da login/logout tfsession aendern
		session = request.getSession(false);
		if(session != null)
		{	
			tfsession = SessionData.getSessionData(session.getId());
			if( tfsession == null)
			{
				tfsession = SessionData.guest;
			}
		}
		else
		{
			// Gast session initialisieren
			tfsession = SessionData.guest;
		}

		resp.addElementToRoot(tfsession.getXMLBlock());

		xmlResponse(response, resp, request.getParameter("pt"), request.getParameter("pt2"));
	}
		
		
	protected Response launchCommand(HttpServletRequest req, SessionData tfsession) throws IndexAccessException, DownloadFailedException, ServerInitFailedException, DBAccessException {
		Response r;
		// parameter validieren
		Map params = req.getParameterMap();
		/*if(!params.containsKey("want")) {
			r = new ErrorResponse(null);
			r.serverReturnValue(2, "Need Parameter 'want'");
			return r;
		}*/
		
		if(!params.containsKey("version")) {
			r = new ErrorResponse(null);
			r.serverReturnValue(2, "Need Parameter 'version'");
			return r;
		} else {
			if(!req.getParameter("version").equals(interfaceversion)) {
				r = new ErrorResponse(null);
				r.serverReturnValue(3, "Incompatible Interface Version");
				return r;
			}
		}
		
		if(!params.containsKey("command")) {
			r = new ErrorResponse(null);
			r.serverReturnValue(2, "Need Parameter 'command'");
			return r;
		}
		
		// parameter scheinen erstmal ok, schauen wir ob wir das command kennen
		String cmd = req.getParameter("command");
		
		// kommando existent?
		if(!commands.containsKey(cmd)) {
			r = new ErrorResponse(null);
			r.serverReturnValue(-1, "Command not found");
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
				r.serverReturnValue(2, "Need Parameter 'keyword'");
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
				r.serverReturnValue(2, "Need Parameter 'category'");
				return r;		
			} else {
				rawcat = req.getParameterValues("category");
				int[] categories = new int[rawcat.length];
				
				for(int h = 0; h < rawcat.length; h++) {
					categories[h] = Integer.parseInt(rawcat[h]);
				}
					return ctrl.search(query, offset, categories, tfsession);
			}			

			
			
		case 2:
			String url;
			// suche, es müssen die parameter category und url da sein
			if(!params.containsKey("url")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'url'");
				return r;
			} else {
				url = req.getParameter("url");
			}
			
			String[] rawcat2;
			if(!params.containsKey("category")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'category'");
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
			if(!params.containsKey("projectid")) 
			{
				//r = new ErrorResponse(null);
				//r.serverReturnValue(2, "Need Parameter 'projectid'");
				//return r;
				projectID=0;
			} 
			else 
			{
				projectID = Integer.parseInt(req.getParameter("projectid"));
			}
			return ctrl.getCategories(projectID);
			
		case 4:
			String name, description;
			int parentID;
			// addCategory
			if(!params.containsKey("name")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'name'");
				return r;
			} else {
				name = req.getParameter("name");
			}			

			if(!params.containsKey("description")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'description'");
				return r;
			} else {
				description = req.getParameter("description");
			}			
			
			if(!params.containsKey("subcategoryof")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'subcategoryof'");
				return r;
			} else {
				parentID = Integer.parseInt(req.getParameter("subcategoryof"));
			}			
			
			return ctrl.addCategory(name, parentID, description, tfsession);
			
		case 5:
			return ctrl.getProjects();
			
		case 6:
			String user, pass;
			if(!params.containsKey("user")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			} else {
				user = req.getParameter("user");
			}			

			if(!params.containsKey("pass")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'pass'");
				return r;
			} else {
				pass = req.getParameter("pass");
			}			
			
			return ctrl.newUser(user, pass);
		
		case 7:
			String uniforgeuser;
			if(!params.containsKey("user")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			} else {
				user = req.getParameter("user");
			}			

			if(!params.containsKey("pass")) {
				r = new ErrorResponse(null);
				r.serverReturnValue(2, "Need Parameter 'pass'");
				return r;
			} else {
				pass = req.getParameter("pass");
			}
			
			if(!params.containsKey("uniforgeuser")) {
				uniforgeuser = "no";
			} else {
				uniforgeuser = req.getParameter("uniforgeuser");
			}

			if (uniforgeuser.equals("yes"))
			{
				//TODO Abfrage Uniforge oder wie auch immer
			}
			if(ctrl.checkUser(user, pass)) //will nur eine Session anlegen wenn alles stimmt
			{
				//1. Session anlegen
				HttpSession session = req.getSession();
				String sessionkey = session.getId();
				Date last = new Date(session.getLastAccessedTime());
				
				//2. Response schicken
				return ctrl.loginUser(user, pass, sessionkey, last);
			}
			else
			{
				return ctrl.rejectUser(user);
			}

			
		}
		
		r = new ErrorResponse(null);
		r.serverReturnValue(-1, "Unknown command");
		return r;
		
	}
	
	
	protected void xmlResponse(HttpServletResponse response, Response tfResponse, String xsltpassthrough, String xsltpassthrough2) throws IOException 
	{
		Document d = tfResponse.getXML();

		if( xsltpassthrough != null)
		{
			Element root = d.getRootElement();
			Element epassthrough = new Element("xsltpassthrough");
			epassthrough.addContent(xsltpassthrough);
			root.addContent(epassthrough);
		}
		if( xsltpassthrough2 != null)
		{
			Element root = d.getRootElement();
			Element epassthrough = new Element("xsltpassthrough2");
			epassthrough.addContent(xsltpassthrough2);
			root.addContent(epassthrough);
		}

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
