/*
 * Einfache Klasse die alle Sessiondaten enthalten soll
 * Damit nicht bei jeder Anfrage auf die Datenbank zugegriffen werden muss
 */

package controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import org.jdom.*;

import db.dbbeans.*;

public class SessionData
{
	private static ConcurrentHashMap<String,SessionData> sessions = new ConcurrentHashMap<String,SessionData>();
	public static ConcurrentHashMap<Integer,projectdataBean> projectdata = new ConcurrentHashMap<Integer,projectdataBean>();

	public static final SessionData guest = new SessionData(null, "guest", new tfuserBean(-1, "guest", "", "guest", null, false));

	public static void addSession(String sessionid, userRightBean rights, tfuserBean tfu)
	{
		if( getSessionData(sessionid) != null)
		{
			removeSession(sessionid);
		}
		sessions.put(sessionid,new SessionData(rights, sessionid, tfu));
	}

	public static void removeOldSessions()
	{
		Date maxlastaccess = new Date();
		maxlastaccess.setTime(maxlastaccess.getTime()-30*60*1000); // 30min * 60sec * 1000 millisec

		Iterator it = sessions.values().iterator();
		while( it.hasNext())
		{
			SessionData sd = (SessionData)it.next();
			if( sd.lastaccess.before(maxlastaccess))
			{	removeSession(sd.sessionkey);
			}
		}
	}

	public static void removeSession(String sessionid)
	{
		if( getSessionData(sessionid) != null)
		{
			sessions.remove(sessionid);
		}
	}

	public static SessionData getSessionData(String sessionkey)
	{
		return(sessions.get(sessionkey));
	}


	public userRightBean urb;
	public Date lastaccess;
	public String sessionkey;
	public tfuserBean tfu;

	public SessionData( userRightBean _urb, String _sessionkey, tfuserBean t)
	{
		urb = _urb;
		sessionkey = _sessionkey;
		lastaccess = new Date();
		tfu = t;
	}

	public Element getXMLBlock()
	{
		Element session = new Element("session");

		Element sessvalue = new Element("return-value");
//		sessvalue.addContent(SessionStatus.toString());
		session.addContent(sessvalue);
		
		Element sessDescr = new Element("return-description");
//		sessDescr.addContent(SessionDescription);
		session.addContent(sessDescr);

		Element uname = new Element("name");
		uname.addContent(tfu.getUsername());
		session.addContent(uname);
	
		Element sesskey = new Element("sessionkey");
		sesskey.addContent(sessionkey);
		session.addContent(sesskey);

		return session;
	}
}
