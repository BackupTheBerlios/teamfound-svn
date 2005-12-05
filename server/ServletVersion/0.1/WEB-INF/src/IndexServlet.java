import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import java.util.Enumeration;
import Index.Download;
import Index.DownloadFailedException;
import Index.IndexAccessException;
import Index.TeamFoundIndexer;
import Search.SearchFiles;



/*
 * erster Versuch von Martin
 */

public class IndexServlet  extends HttpServlet {

	protected Download d;
	protected TeamFoundIndexer index;
	protected SearchFiles searcher;
	protected ServletConfig conf;
		  
	public void init() throws ServletException {
			  d = new Download();
			  index = new TeamFoundIndexer();
			  searcher = new SearchFiles();
	}
	
	/**
	 * Get-request wird einfach auf doPost-umgeleitet
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "TeamFound";
		
		out.println(
							"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"+
							"\n"+
							"<HTML>\n" +
							"<HEAD><TITLE>" + title + "</TITLE></HEAD>\n"+
							"<BODY>\n");

		//conf = this.getServletConfig();
		//out.println("Servlet Name: "+conf.getServletName()+ "<br>");
		//java.util.Enumeration en = conf.getInitParameterNames();
		//while(en.hasMoreElements())
		//{
		//	out.println("Servlet InitParameter: "+ "<br>");
		//}
		//out.println("Servlet Info: "+this.getServletInfo()+ "<br>");
	   //out.println("Object HachCode: "+this.hashCode()+"<br>");		

		
		Enumeration paramNames = request.getParameterNames();

		while(paramNames.hasMoreElements()) 
		{
				  String paramName = (String)paramNames.nextElement();
				  String[] paramValues = request.getParameterValues(paramName);
				  if(paramName.equals("url"))
				  {
						if(!(paramValues[0].equals("")))
						{
							try {
								index.index(paramValues[0]);
							} catch(IndexAccessException iae) {
								iae.printStackTrace();
							} catch (DownloadFailedException e) {
								e.printStackTrace();
							}
						} else {
							out.println("Parameter URL hat keinen Wert.");
						}
							 
				  }
				  else if (paramName.equals("keyword"))
				  {							 
						out.println("searching " + paramValues[0] + "<br>");
						
						if(!(paramValues[0].equals("")))
						{
							//String tmp = searcher.search("wort");
							String tmp = searcher.search(paramValues[0]);
							out.print(tmp);
						}
						else
						out.println("kein Parameter");

				  }
				  else
				  {
							 out.println("<H3>Falscher Parameter</H3>");
				  }
		
		}
		out.println("</BODY></HTML>");
		
	}
	
}
