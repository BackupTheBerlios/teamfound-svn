import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 * Created on Nov 28, 2005
 */

public class TestServlet extends HttpServlet {
	
	public void init() throws ServletException {
	}
	
	
	public void destroy() {
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter p = response.getWriter();
		p.println("Hello world");
		
		
		
	}
	
	public String getServletInfo() {
        return "TeamFound hello world Servlet";
    }
}
