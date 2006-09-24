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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import updater.UpdateThread;

import controller.Controller;
import controller.DownloadFailedException;
import controller.IndexAccessException;
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
	protected config.teamfound.TeamFoundConfig conf;	
	protected UpdateThread updater;
	protected Logger servletLog;
	protected Logger requestLog;
	


	public void init() throws ServletException {

		try 
		{
			// configure log4j
			String logpath = getServletContext().getRealPath("logging.properties");
			PropertyConfigurator.configure(logpath);
			servletLog = Logger.getLogger("servlet");
			requestLog = Logger.getLogger("requests");
		
			servletLog.info("Servlet ini");
			//conf im Servlet root lesen

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
			servletLog.fatal(a);
			throw(a);
			
		}
			
		// wir starten nun einen nebenläufigen thread der sich darum kümmert die seiten in der datenbank aktuell zu halten
		updater = new UpdateThread();
		updater.start();
		servletLog.info("Started Update-Thread");
		
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
		commands.put("editpermissions", new Integer(8));
		commands.put("editcategory", new Integer(9));
		commands.put("adduser", new Integer(10));
		commands.put("deluser", new Integer(11));
		commands.put("getuser", new Integer(12));
		commands.put("logout", new Integer(13));
		commands.put("removepage", new Integer(14));

		// NOCH FEHLEN
		//commands.put("removecategory", new Integer(x));

		
		servletLog.info("Servlet ini done");
	}
	
	public void destroy() {
		// updater-thread anhalten
		servletLog.info("stopping Updater");
		updater.interrupt();
		try {
			updater.join();
		servletLog.info("Updater stopped");
		} catch (InterruptedException e) {
			e.printStackTrace();
			servletLog.error("could not wait for update-thread");
		}	
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		requestLog.info("request from "+request.getRemoteAddr());


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
			resp = new ErrorResponse();
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
		
		
	protected Response launchCommand(HttpServletRequest req, SessionData tfsession) throws IndexAccessException, DownloadFailedException, DBAccessException {
		Response r;
		
		// parameter validieren
		Map params = req.getParameterMap();
		/*if(!params.containsKey("want")) {
			r = new ErrorResponse(null);
			r.serverReturnValue(2, "Need Parameter 'want'");
			requestLog.warn("["+req.getRemoteAddr()+"] missing parameter 'want'");
			return r;
		}*/
		
		if(!params.containsKey("version")) {
			r = new ErrorResponse();
			r.serverReturnValue(2, "Need Parameter 'version'");
			requestLog.warn("["+req.getRemoteAddr()+"] missing parameter 'version'");
			return r;
		} else {
			if(!req.getParameter("version").equals(interfaceversion)) {
				r = new ErrorResponse();
				r.serverReturnValue(3, "Incompatible Interface Version");
				requestLog.warn("["+req.getRemoteAddr()+"] incompatible interface: "+req.getParameter("version"));

				return r;
			}
		}
		
		if(!params.containsKey("command")) {
			r = new ErrorResponse();
			r.serverReturnValue(2, "Need Parameter 'command'");
			requestLog.warn("["+req.getRemoteAddr()+"] missing parameter 'command'");
			return r;
		}
		
		// parameter scheinen erstmal ok, schauen wir ob wir das command kennen
		String cmd = req.getParameter("command");
		
		// kommando existent?
		if(!commands.containsKey(cmd)) {
			r = new ErrorResponse();
			r.serverReturnValue(-1, "Command not found");
			requestLog.error("["+req.getRemoteAddr()+"] unknown command '"+cmd+"'");

			return r;
		}
		Integer i = commands.get(cmd);

		requestLog.info("["+req.getRemoteAddr()+"] executing command '"+cmd+"'");

		switch(i.intValue()) {
		case 1: // search
			String query;
			int offset;
			// suche, es müssen die parameter keyword und offset da sein
			if(!params.containsKey("keyword")) {
				if( ! params.containsKey("getall")) {
					r = new ErrorResponse();
					r.serverReturnValue(2, "Need Parameter 'keyword'");
					return r;
				}
				else
				{
					if(req.getParameter("getall").equals("yes"))
					{
						query=null;
					}
					else
					{
						r = new ErrorResponse();
						r.serverReturnValue(2, "Wrong Value in Parameter 'getall'");
						return r;
					}
				}
			}
				
			query = req.getParameter("keyword");
			
			if(!params.containsKey("offset")) {
				offset = 0;				
			} else {
				offset = Integer.parseInt(req.getParameter("offset"));
			}			
			String[] rawcat;
			if(!params.containsKey("category")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'category'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'category'");
				return r;		
			} else {
				rawcat = req.getParameterValues("category");
				int[] categories = new int[rawcat.length];
				
				for(int h = 0; h < rawcat.length; h++) {
					categories[h] = Integer.parseInt(rawcat[h]);
				}
				return ctrl.search(query, offset, categories, tfsession);
			}			

		case 2: // addpage
			String url;
			// suche, es müssen die parameter category und url da sein
			if(!params.containsKey("url")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'url'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'url'");
				return r;
			} else {
				url = req.getParameter("url");
			}
			
			String[] rawcat2;
			if(!params.containsKey("category")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'category'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'category'");
				return r;		
			} else {
				rawcat2 = req.getParameterValues("category");
				int[] categories = new int[rawcat2.length];
				
				for(int h = 0; h < rawcat2.length; h++) {
					categories[h] = Integer.parseInt(rawcat2[h]);
				}
				return ctrl.addToIndex(url, categories, tfsession);
			}			
			
			
			
		case 3: // getcategories
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
			return ctrl.getCategories(projectID, tfsession);
			
		case 4: // addcategory
			String name, description;
			int parentID;
			// addCategory
			if(!params.containsKey("name")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'name'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'name'");

				return r;
			} else {
				name = req.getParameter("name");
			}			

			if(!params.containsKey("description")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'description'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'description'");

				return r;
			} else {
				description = req.getParameter("description");
			}			
			
			if(!params.containsKey("subcategoryof")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'subcategoryof'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'subcategoryof'");

				return r;
			} else {
				parentID = Integer.parseInt(req.getParameter("subcategoryof"));
			}			
			
			return ctrl.addCategory(name, parentID, description, tfsession);
			
		case 5: // getprojects
			return ctrl.getProjects(tfsession);
			
		case 6: // register
			String user, pass;
			if(!params.containsKey("user")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			} else {
				user = req.getParameter("user");
			}			

			if(!params.containsKey("pass")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'pass'");
				return r;
			} else {
				pass = req.getParameter("pass");
			}			
			
			return ctrl.newUser(user, pass);
		
		case 7: // login
			String uniforgeuser;
			if(!params.containsKey("user")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			} else {
				user = req.getParameter("user");
			}			

			if(!params.containsKey("pass")) {
				r = new ErrorResponse();
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

		case 8 : // editpermissions
			Boolean useruseradd, userurledit, usercatedit, useraddurl, useraddcat, guestread, guesturledit, guestcatedit, guestaddurl, guestaddcat    ;

			if(!params.containsKey("projectid")) 
			{
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'projectid'");
				return r;
			}
			else
			{
				projectID = Integer.parseInt(req.getParameter("projectid"));
			}


			if(!params.containsKey("useruseradd")) {
				useruseradd = new Boolean(false);
			} else {
				useruseradd = getBooleanFromParam(req.getParameter("useruseradd"));
			}
			if(!params.containsKey("userurledit")) {
				userurledit = new Boolean(false);
			} else {
				userurledit = getBooleanFromParam(req.getParameter("userurledit"));
			}
			if(!params.containsKey("usercatedit")) {
				usercatedit = new Boolean(false);
			} else {
				 usercatedit= getBooleanFromParam(req.getParameter("usercatedit"));
			}
			if(!params.containsKey("useraddurl")) {
				useraddurl =new Boolean(false);
			} else {
				useraddurl = getBooleanFromParam(req.getParameter("useraddurl"));
			}
			if(!params.containsKey("useraddcat")) {
				useraddcat = new Boolean(false);
			} else {
				useraddcat = getBooleanFromParam(req.getParameter("useraddcat"));
			}
			if(!params.containsKey("guestread")) {
				guestread = new Boolean(false);
			} else {
				guestread = getBooleanFromParam(req.getParameter("guestread"));
			}
			if(!params.containsKey("guesturledit")) {
				guesturledit = new Boolean(false);
			} else {
				guesturledit = getBooleanFromParam(req.getParameter("guesturledit"));
			}
			if(!params.containsKey("guestcatedit")) {
				guestcatedit = new Boolean(false);
			} else {
				 guestcatedit= getBooleanFromParam(req.getParameter("guestcatedit"));
			}
			if(!params.containsKey("guestaddurl")) {
				guestaddurl = new Boolean(false);
			} else {
				 guestaddurl= getBooleanFromParam(req.getParameter("guestaddurl"));
			}
			if(!params.containsKey("guestaddcat")) {
				guestaddcat = new Boolean(false);
			} else {
				guestaddcat = getBooleanFromParam(req.getParameter("guestaddcat"));
			}
			return ctrl.editPermissions(projectID, tfsession, useruseradd, userurledit, usercatedit,useraddurl,useraddcat,guestread,guesturledit,guestcatedit,guestaddurl,guestaddcat ) ;

		case 9: // editcategory
			// edit category
			String newname = null;
			String newdescription = null;
			Integer category;

			// nur parameter category ist pflicht
			if(params.containsKey("name")) 
			{ 
				newname = req.getParameter("name"); 
			}

			if(params.containsKey("description")) 
			{ 
				newdescription = req.getParameter("description"); 
			}

			if(!params.containsKey("category")) 
			{ 	r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'category'");
				return r;
			} 
			else 
			{ 
				category = Integer.parseInt(req.getParameter("category"));
			}

			return ctrl.editCategory(category, newname, newdescription, tfsession);

		case 10: // adduser
			String username = null;
			String role = null;
			Integer projectid;

			if(!params.containsKey("user")) 
			{ 
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			}
			else
				username = req.getParameter("user"); 
			
			if(!params.containsKey("projectid")) 
			{
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'projectid'");
				return r;
			}
			else
				projectid = Integer.parseInt(req.getParameter("projectid")); 

			if(!params.containsKey("role")) 
			{ 	r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'role'");
				return r;
			} 
			else 
			{ 
				role = req.getParameter("role");
				if(!(role.equals("user")||role.equals("projectadmin")))
				{
					r = new ErrorResponse();
					r.serverReturnValue(2, "Unknown 'role'");
					return r;
				}
			}
			if(role.equals("user"))
				return ctrl.addUserToProject(username,projectid,tfsession);
			else
				return ctrl.grantProjectAdmin(username,projectid,tfsession);

		case 11: // deluser
			String username11 = null;
			Integer projectid11;

			if(!params.containsKey("user")) 
			{ 
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'user'");
				return r;
			}
			else
				username11 = req.getParameter("user"); 
			
			if(!params.containsKey("projectid")) 
			{
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'projectid'");
				return r;
			}
			else
				projectid11 = Integer.parseInt(req.getParameter("projectid")); 

			return ctrl.removeUserFromProject(username11,projectid11,tfsession);

		case 12: // getuser
			Integer projectid12;

			if(!params.containsKey("projectid")) 
			{
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'projectid'");
				return r;
			}
			else
				projectid12 = Integer.parseInt(req.getParameter("projectid")); 

			return ctrl.getUsersOfProject(projectid12,tfsession);

		case 13: // logout
			if( tfsession != SessionData.guest)
			{
				HttpSession session = req.getSession(false);
				session.invalidate();
				SessionData.removeSession(tfsession.sessionkey);
			}
			r = new ErrorResponse();
			r.serverReturnValue(0, "OK");
			return r;

		case 14: // removepage
			String url14;
			String cat14;
			Integer proj14 = null;
			// suche, es müssen die parameter category und url da sein
			if(!params.containsKey("url")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'url'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'url'");
				return r;
			} else {
				url14 = req.getParameter("url");
			}
			
			if(!params.containsKey("category")) {
				r = new ErrorResponse();
				r.serverReturnValue(2, "Need Parameter 'category'");
				requestLog.warn("["+req.getRemoteAddr()+"] missing param 'category'");
				return r;		
			} 
			else 
			{
				cat14 = req.getParameter("category");

				if( cat14.equals("all"))
				{
					if(!params.containsKey("projectid")) 
					{
						r = new ErrorResponse();
						r.serverReturnValue(2, "Need Parameter 'projectid'");
						requestLog.warn("["+req.getRemoteAddr()+"] missing param 'projectid'");
						return r;
					} else 
					{
						proj14 = Integer.parseInt(req.getParameter("projectid"));
					}
				}
			}			
			
			return ctrl.removePage(url14, cat14, proj14, tfsession);
		}
		
		r = new ErrorResponse();
		r.serverReturnValue(-1, "Unknown command");
		requestLog.error("["+req.getRemoteAddr()+"] unknown command '"+cmd+"'");

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

	private Boolean getBooleanFromParam(String s)
	{
		if(s.equals("yes"))
			return(new Boolean(true));
		else
			return(new Boolean(false));
	}
}
