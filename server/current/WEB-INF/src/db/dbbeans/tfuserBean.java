/**
 * einfache Bean zur tfuser-Tabelle
 * @author Martin Klink, Jan
 */

package db.dbbeans;

import java.io.Serializable;
import java.sql.Timestamp;

public class tfuserBean implements Serializable
{
	public static final long serialVersionUID = 128L;

	private Integer id;
	private String username;
	private String pass;
	private String sessionkey;
	private Timestamp lastaction;
	private Boolean serveradmin;

	public tfuserBean(Integer i, String _username, String _pass, String _sessionkey, Timestamp _lastaction, Boolean _serveradmin)
	{
		id=i;
		username=_username;
		pass=_pass;
		sessionkey=_sessionkey;
		lastaction=_lastaction;
		serveradmin=_serveradmin;
	}

	public tfuserBean()
	{};
	
	public Boolean getServeradmin()
	{
		return(serveradmin);
	}
	public void setServeradmin(Boolean _serveradmin)
	{
		serveradmin=_serveradmin;
	}
	public String getUsername()
	{
		return(username);
	}
	public void setUsername(String _username)
	{
		username=_username;
	}
	public String getPass()
	{
		return(pass);
	}
	public void setPass(String s)
	{
		pass=s;
	}
	public String getSessionkey()
	{
		return(sessionkey);
	}
	public void setSessionkey(String s)
	{
		sessionkey=s;
	}
	public Timestamp getLastaction()
	{
		return(lastaction);
	}
	public void setLastaction(Timestamp t)
	{
		lastaction=t;
	}
	public Integer getID()
	{
		return(id);
	}
	public void setID(Integer i)
	{
		id=i;
	}

	public void printAll()
	{
		System.out.println("id "+id);
		System.out.println("username "+username);
		System.out.println("pass "+pass);
		System.out.println("sessionkey "+sessionkey);
		System.out.println("lastaction "+lastaction);
		System.out.println("serveradmin "+serveradmin);
	}
}
