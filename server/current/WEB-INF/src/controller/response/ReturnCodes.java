/*
 * Created 15.9.
 * Alle Returndescriptions zu den Codes fuer die ResponseKlassen
 */

package controller.response;

import java.util.Map;

public class ReturnCodes
{

	public ReturnCodes()
	{
	}
	public static String getDescription(Integer code)
	{
		switch(code.intValue())
		{
			case 0:return("OK");
			case 1:return("Could not find URL!");
			case 2:return("Command not correct!");
			case 3:return("Interface Version incompatible!");
			case 4:return("Category exists!");
			case 5:return("Could not find Category");
			case 6:return("Username exists");
			case 7:return("Username to short");
			case 8:return("Login failed");
			case 9:return("Not authorized!");
			case 10:return("Password to short");
			case 11:return("User does not exist");
			case 12:return("User does not belong to specified project!");
			case 13:return("Can not delete Category! (not empty)");
			case 14:return("Category is not a leaf!");		
			default :return("Some wierd Error");
		}

	}

}
