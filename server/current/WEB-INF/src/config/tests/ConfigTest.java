/* Martin Klink 23.3.06
 */


package config.tests;

import config.*;

public class ConfigTest
{
	public static void main(String args[])
	{
		TeamFoundConfig tfc = new TeamFoundConfig();
		System.out.println(tfc.getConfValue("tfpath"));
	}
}
