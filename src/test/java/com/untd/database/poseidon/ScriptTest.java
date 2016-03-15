package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.executor.Executor;
import com.untd.database.poseidon.executor.impl.PLSQLExecutor;
import com.untd.database.poseidon.model.ExecutionResult;



public class ScriptTest {
	
	@BeforeClass
	public static void BeforeClass() throws SQLException, ConfigurationException, IOException {
		
		TestSetup.setup();		
	}

	@Test
	public void testScriptCreation() throws SQLException {		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);
			
	}
	
	@Test
	public void testScriptTargets() throws SQLException {		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);
		assertEquals(1,ControlDataStore.getScriptTargets(script,PoseidonConfiguration.getConfiguration().getInt("serverId")).size());
	}
	
	@Test
	public void testScriptNotifications() throws SQLException {
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);	
		assertTrue(ControlDataStore.getScriptNotifications(script, Script.SEVERITY_HIGH).size() > 0);
		assertTrue(ControlDataStore.getScriptNotifications(script, Script.SEVERITY_MED).size() > 0);
		assertTrue(ControlDataStore.getScriptNotifications(script, Script.SEVERITY_LOW).size() > 0);		
	}
	
	@Test
	public void testPLSQLScript() throws SQLException {
				
		// Update PL/SQL text in the script to have CR characters
		String sql = "update psd_script set query_text = ? where script_id = ?";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);		
		st.setString(1,"begin"+(char)13+"\n open :cursor for 'select count(*) from cat'; end;");
		st.setInt(2, TestSetup.plsq_script_id);
		st.execute();
		
		Script script = ControlDataStore.getScript(TestSetup.plsq_script_id);
		assertNotNull(script);	
		assertEquals(new Integer(Script.QUERY_TYPE_PLSQL),script.getQueryType());

		// Connect to the test Oracle database
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection connection = DriverManager.getConnection(
				PoseidonConfiguration.getConfiguration().getString("oracleConnectionDescription"),
				PoseidonConfiguration.getConfiguration().getString("oracleUsername"),
				PoseidonConfiguration.getConfiguration().getString("oraclePassword"));		
		connection.setAutoCommit(true);
		
		// Create PL/SQL executor
		Executor executor = new PLSQLExecutor();
		ExecutionResult executionResult = new ExecutionResult();
		executor.execute(connection, script, executionResult);
		assertEquals(1,executionResult.getRows().size());
	}
	
	@AfterClass
	public static void AfterClass() throws SQLException {
						
		TestSetup.done();
						
	}	

}
