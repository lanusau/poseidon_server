package com.untd.database.poseidon;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;

/**
 * Static class to access configured SQL statements
 *
 */
public class SqlText {

	static private PropertiesConfiguration sqlText;
	
	/**
	 * Load SQL text from properties file
	 * 
	 * @throws ConfigurationException
	 */
	public static void init() throws ConfigurationException {
		AbstractConfiguration.setDefaultListDelimiter('\0');
		sqlText = new PropertiesConfiguration("sqltext.properties");
	}
	
	/**
	 * Get text of particular SQL statement
	 * 
	 * @param key - key of the SQL statement
	 * @return - SQL statement text
	 * @throws ConfigurationException
	 */
	public static String getString(String key) {
		if (sqlText == null) {
			LoggerFactory.getLogger(SqlText.class).error("SqlText.init was not called");
			return "";
		}
		return sqlText.getString(key);
	}
}
