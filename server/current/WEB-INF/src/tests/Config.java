package tests;

import org.apache.log4j.PropertyConfigurator;

/**
 * This class sets up an testing environment
 * 
 * at the moment only logging is configured 
 * 
 * @author jonas heese
 *
 */
public class Config {
	public static void testConfig() {
		PropertyConfigurator.configure("logging.properties");
	}
}
