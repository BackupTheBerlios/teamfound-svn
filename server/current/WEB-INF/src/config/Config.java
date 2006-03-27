/**
 * @Author Martin Klink
 * Created 23.03.06
 * Methoden zum zugriff auf die Properties.
 * Erstmal nur der Dateipfad in dem die Daten landen sollten.
 *
 * 
 * TODO Wenn wir ein Admin Interface haben, dann soll das Properties file automatisch
 * 		mit erzeugt werden etc.
 */
package config;

public interface Config
{
	

	/**
	 * Konfigurationsfile auslesen und Value zu Key liefern
	 *
	 * @param name Name des Schluessels bsp. "pfad" fuer den dateipfad 
	 *
	 */ 
	public String getConfValue(String name);
}
