import java.io.PrintWriter;

/*
 * Created on Nov 29, 2005
 */

public class TeamFoundServer {

	public void addUrl(PrintWriter p, String parameter) {
		p.println("adding url:"+ parameter);
		
	}

	public void search(PrintWriter p, String parameter) {
		p.println("searching for:"+ parameter);
		
	}

}
