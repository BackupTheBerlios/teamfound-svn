/*
 * created 21.9. Martin Klink
 * Klasse die Funktionen haelt um Berechtigung zu checken 
 */

package controller.teamfound;

import controller.SessionData;

public class checkAuthorisation
{
	/* 
	 * ueberprueft Authorisation zum adden von URLs
	 */
	public static boolean checkAddPage(SessionData tfsession, Integer projectID)
	{
		if(tfsession == SessionData.guest)
		{
			if(SessionData.projectdata.get(projectID).getGuestAddurl().booleanValue())
				return true;
			else
				return false;
		}


		if(isAdmin(tfsession,projectID))
			return true;

		if(tfsession.urb.addUrl(projectID))
			return true;
		else 
			return false;
	}
	/*
	 *
	 *Ueberbrueft ob der angemeldete  Nutzer admin des Projekts ist
	 */
	public static boolean isAdmin(SessionData tfsession, Integer projectid)
	{
		if(tfsession == SessionData.guest)
			return false;
		if( tfsession.urb.isAdmin(projectid))
			return true;
		else
			return false;
	}
	/*
	 *
	 *	Ueberbrueft ob der angemeldete Nutzer/Gast Kategorien im Projekt adden darf 
	 */
	public static boolean checkAddCat(SessionData tfsession, Integer projectid)
	{
		// checke ob guestaddcat == true oder 
		// user eingeloggt, zu diesem projekt gehoert und projekt useraddcat == true gestzt hat
		// oder user admin des projekts ist
		
		if( tfsession == SessionData.guest)
		{
			if( SessionData.projectdata.get(projectid).getGuestAddcat().booleanValue())
				return true;
			else
				return false;
		}

		if(isAdmin(tfsession,projectid))
			return true;

		if(tfsession.urb.addCat(projectid))
			return true;
		else 
			return false;
	}
	/*
	 *
	 *	Ueberbrueft ob der angemeldete Nutzer/Gast im Projekt suchen darf
	 */
	public static boolean checkSearch(SessionData tfsession, Integer projectid)
	{
		if( tfsession == SessionData.guest)
		{
			if( SessionData.projectdata.get(projectid).getGuestRead().booleanValue())
				return true;
			else
				return false;
		}

		if(isAdmin(tfsession,projectid))
			return true;

		if(tfsession.urb.isUser(projectid))
			return true;
		

		return false;
	}



}
