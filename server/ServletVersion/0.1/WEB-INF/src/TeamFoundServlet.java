import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Created on Nov 29, 2005
 */

public class TeamFoundServlet  extends HttpServlet {

	protected TeamFoundServer tfs;
	
	public void init() throws ServletException {
		tfs = new TeamFoundServer();
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
		PrintWriter p = response.getWriter();
		
		String action = request.getParameter("action");
		
		if(action == null) {
			p.println("Fehler: Keine Aktion ausgewaehlt");
			return;
		}
		
		if(action.equals("addUrl")) {
			tfs.addUrl(p, request.getParameter("url"));
			
		} else if(action.equals("search")) {
			tfs.search(p, request.getParameter("query"));
		}
		
		
	}
	
}
