/**
 * @Author Martin Klink
 * Created 23.03.06
 * Methoden zum zugriff auf die Teamfound Properties.
 * Zur Zeit eigentlich nur der Systempfad in dem die Daten landen sollten.
 *
 * Zur Zeit nur zum auslesen!
 * TODO Wenn wir ein Admin Interface haben, dann soll das Properties file automatisch
 * 		mit erzeugt werden etc.
 */
package config.teamfound;

import config.Config;

import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;

public class TeamFoundConfig 
{
	protected static Properties tfprops;

	/**
	 * liest die Konfiguration aus dem file
	 */
	public static void  initstatic(Properties p)
	{
		tfprops = p;
	}

	/**
	 * Konfigurationsfile auslesen und Value zu Key liefern
	 *
	 * @param name Name des Schluessels bsp. "pfad" fuer den dateipfad 
	 *
	 */ 
	public static String getConfValue(String name)
	{
		return(tfprops.getProperty(name));
	}
}
