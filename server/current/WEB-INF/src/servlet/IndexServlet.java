package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import java.util.Enumeration;
import controller.DownloadFailedException;
import controller.IndexAccessException;
import controller.teamfound.TeamFoundController;

/**
 * Servlet zum Aufruf des Controllers, muss noch an die neue Version angepasst
 * werden
 * 
 * @author Jonas Heese <dev-teamfound@jonasheese.de>
 * 
 */

public class IndexServlet extends HttpServlet {

	private static final long serialVersionUID = 8195225642127719226L;

	protected TeamFoundController index;

	protected ServletConfig conf;

	public void init() throws ServletException {
		index = new TeamFoundController();
	}

	/**
	 * Get-request wird einfach auf doPost-umgeleitet
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "TeamFound";

		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
						+ "\n"
						+ "<HTML>\n"
						+ "<HEAD><TITLE>"
						+ title
						+ "</TITLE></HEAD>\n" + "<BODY>\n");

		Enumeration paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramName.equals("url")) {
				if (!(paramValues[0].equals(""))) {
					try {
						index.addToIndex(paramValues[0]);
					} catch (IndexAccessException iae) {
//						 TODO Auto-generated catch block
						iae.printStackTrace();
					} catch (DownloadFailedException e) {
//						 TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					out.println("Parameter URL hat keinen Wert.");
				}

			} else if (paramName.equals("keyword")) {

				out.println("searching " + paramValues[0] + "<br>");

				if (!(paramValues[0].equals(""))) {
					try {
						index.search(paramValues[0]);
					} catch (IndexAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					out.println("kein Parameter");
				}

			} else {
				out.println("<H3>Falscher Parameter</H3>");
			}

		}
		out.println("</BODY></HTML>");

	}

}
