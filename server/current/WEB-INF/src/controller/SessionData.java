/*
 * Einfache Klasse die alle Sessiondaten enthalten soll
 * Damit nicht bei jeder Anfrage auf die Datenbank zugegriffen werden muss
 */

package controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

import db.dbbeans.*;

public class SessionData
{
	private static ConcurrentHashMap<String,SessionData> sessions;
	public static ConcurrentHashMap<Integer,projectdataBean> projectdata;

	public static void addSession(String sessionid, userRightBean rights, tfuserBean tfu)
	{
		if( getSessionData(sessionid) == null)
		{
			sessions.put(sessionid,new SessionData(rights, sessionid, tfu));
		}
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
}
