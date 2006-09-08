/* Martin Klink 23.3.06
 */


package config.tests;

import config.*;
import config.teamfound.*;

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
		
			Config tfc = new TeamFoundConfig(props);
			System.out.println(tfc.getConfValue("tfpath"));
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}

