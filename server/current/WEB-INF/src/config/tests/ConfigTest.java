/* Martin Klink 23.3.06
 */


package config.tests;

import config.*;
import config.teamfound.*;
import config.teamfound.TeamFoundConfig;

public class ConfigTest
{
	public static void main(String args[])
	{
		try
		{
			java.io.File f = new java.io.File("/home/moddin/build/teamfound.properties");

			java.io.FileInputStream pin = new java.io.FileInputStream(f);

			java.util.Properties props = new java.util.Properties();

			props.load(pin);
	
			TeamFoundConfig.initstatic(props);
			System.out.println(TeamFoundConfig.getConfValue("tfpath"));
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}

