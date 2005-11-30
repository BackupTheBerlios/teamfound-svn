import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import Index.Download;
import Index.TeamFoundIndexer;
import Search.SearchFiles;

/*
 * erster Versuch von Martin
 */

public class IndexServlet  extends HttpServlet {

	protected Download d;
	protected TeamFoundIndexer index;
	protected SearchFiles searcher;
		  
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
		PrintWriter out = response.getWriter();
		//out.println("Test<br>");
	
		Enumeration paramNames = request.getParameterNames();

		while(paramNames.hasMoreElements()) 
		{
				  String paramName = (String)paramNames.nextElement();
				  String[] paramValues = request.getParameterValues(paramName);
				  if(paramName.equals("url"))
				  {
						
						String filename = d.downloadFile(paramValues[0]);
						out.println(filename);
						if(!(paramValues[0].equals("")))
							index.index(filename,paramValues[0]);
						else
							out.println("kein Parameter");
							 
				  }
				  else if (paramName.equals("keyword"))
				  {
						out.println("searching" + paramValues[0]);
						
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
		
		
		
			
		
		
	}
	
}
