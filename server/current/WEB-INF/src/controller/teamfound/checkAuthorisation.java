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
			//Ueberpruefen ob Gaeste pages adden duerfen
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


}
