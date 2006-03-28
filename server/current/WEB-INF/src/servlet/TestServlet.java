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
import controller.teamfound.TestController;

public class TestServlet extends BaseServlet {
	private static final long serialVersionUID = -2766879274061221595L;

	public void init() throws ServletException {
		ctrl = new TestController();
		xmlout = new XMLOutputter(Format.getPrettyFormat());
		
		commands = new HashMap<String, Integer>();
		
		commands.put("search", new Integer(1));
		commands.put("addpage", new Integer(2));
		commands.put("getcategories", new Integer(3));
		commands.put("addcategories", new Integer(4));
	}
}
