package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;


public class ScriptTest {
	public static Connection connection;
	public static int script_id;
	
	@BeforeClass
	public static void BeforeClass() throws SQLException, ConfigurationException {
		
		// Initialize configuration
		PoseidonConfiguration.init();
		PoseidonConfiguration.addPropertyFile("poseidon-test.conf");
		SqlText.init();
		
		// Connect to the test database
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		connection = DriverManager.getConnection(
				PoseidonConfiguration.getConfiguration().getString("controlConnectionDescription"),
				PoseidonConfiguration.getConfiguration().getString("username"),
				PoseidonConfiguration.getConfiguration().getString("password"));		
		connection.setAutoCommit(false);
		
		// Find script ID by name
		String sql = "select script_id from psd_script where name = 'Dataguard check'";
		PreparedStatement st = connection.prepareCall(sql);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			script_id = rs.getInt(1);
		} else {
			script_id = 0;
		}
	}

	@Test
	public void testScriptCreation() throws SQLException {		
		Script script = new Script(connection, script_id);
		assertNotNull(script);		
	}
	
	@Test
	public void testScriptTargets() throws SQLException {
		// Find target id for oracle@target1.com
		int targetId;
		String sql = "select target_id from psd_target where name = 'oracle@target1.com'";
		PreparedStatement st = connection.prepareCall(sql);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			targetId = rs.getInt(1);
		} else {
			targetId = 0;
		} 
		Script script = new Script(connection, script_id);
		assertNotNull(script);	
		assertTrue(script.containsTarget(script.getTargets(), targetId));
	}
	
	@Test
	public void testScriptNotifications() throws SQLException {
		Script script = new Script(connection, script_id);
		assertNotNull(script);		
		assertNotNull(script.getNotificationList(Script.SEVERITY_HIGH));
		assertNotNull(script.getNotificationList(Script.SEVERITY_MED));
		assertNotNull(script.getNotificationList(Script.SEVERITY_LOW));
	}
	
	@AfterClass
	public static void AfterClass() throws SQLException, ConfigurationException {
						
		connection.close();
						
	}	

}
