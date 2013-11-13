package com.untd.database.poseidon;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Static class to keep configuration information
 *
 */
public class PoseidonConfiguration {
	
	private static CompositeConfiguration prop; 
	
	/**
	 * Load property files
	 * 
	 * @throws ConfigurationException
	 */
	public static void init()  {		
		prop = new CompositeConfiguration();
	}
	
	/**
	 * Add additional property file
	 * 
	 * @param fileName - name of the property file to add
	 * @throws ConfigurationException
	 */
	public static void addPropertyFile(String fileName) throws ConfigurationException {
		Configuration properties = new PropertiesConfiguration(fileName);
		prop.addConfiguration(properties);
	}
	
	/**
	 * Get configuration 
	 * 
	 * @return Configuration object
	 */
	public static Configuration getConfiguration() {
		return prop;
	}
}
