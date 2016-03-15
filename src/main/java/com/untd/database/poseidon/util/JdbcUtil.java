package com.untd.database.poseidon.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility class for JDBC
 *
 */
public class JdbcUtil {
	
	/**
	 * Close ResultSet handle making sure its not null and open
	 * and ignoring any errors. 
	 * 
	 * @param st
	 */
	public static void closeResultSetHandle(ResultSet rs) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (Exception e) {
			// Ignore any exceptions
		}		
	}	
	
	/**
	 * Close PreparedStatement handle making sure its not null and open
	 * and ignoring any errors. 
	 * 
	 * @param st
	 */
	public static void closeStatementHandle(PreparedStatement st) {
		try {
			if (st != null && !st.isClosed()) {
				st.close();
			}
		} catch (Exception e) {
			// Ignore any exceptions
		}		
	}
	
	/**
	 * Close Connection handle making sure its not null
	 * and ignoring any errors. 
	 * 
	 * @param st
	 */
	public static void closeConnectionHandle(Connection db) {
		try {
			if (db != null) {
				db.close();
			}
		} catch (Exception e) {
			// Ignore any exceptions
		}		
	}	

}