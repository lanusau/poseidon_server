package com.untd.database.poseidon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;


public class TestSetup {
	
	public static Connection connection;
	public static int script_id;
	public static int target_id;
	public static int server_id;
	
	public static void setup() throws SQLException, ConfigurationException, IOException {
		
		// Initialize configuration
		PoseidonConfiguration.init();
		PoseidonConfiguration.addPropertyFile("poseidon-test.conf");		
		
		// Connect to the test database
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		connection = DriverManager.getConnection(
				PoseidonConfiguration.getConfiguration().getString("controlConnectionDescription"),
				PoseidonConfiguration.getConfiguration().getString("username"),
				PoseidonConfiguration.getConfiguration().getString("password"));		
		connection.setAutoCommit(true);
		
		// Find test script ID by name
		String sql = "select script_id from psd_script where name = 'ScriptForJUnit'";
		PreparedStatement st = connection.prepareCall(sql);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			script_id = rs.getInt(1);
		} else {
			script_id = 0;
		}
		
		// Get test target ID by name. Also get server ID
		sql = "select target_id, server_id from psd_target where name = 'TargetForJUnit'";
		st = connection.prepareCall(sql);
		rs = st.executeQuery();
		if (rs.next()) {
			target_id = rs.getInt(1);
			server_id = rs.getInt(2);
		} else {
			target_id = 0;
		} 				
	}
	
	public static void done() throws SQLException {
		connection.close();
	}
}
