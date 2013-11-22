package com.untd.database.poseidon;


import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class ControlDataStoreTest {

	@Test
	public void testControlStore() throws ConfigurationException, SQLException {
		TestSetup.setup();	
		
		ControlDataStore.init(
				PoseidonConfiguration.getConfiguration().getString("controlConnectionDescription"), 
				PoseidonConfiguration.getConfiguration().getString("username"), 
				PoseidonConfiguration.getConfiguration().getString("password"));
		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);

		// There should be 3 active and 1 inactive scripts
		ArrayList<Script> scriptList = ControlDataStore.getActiveScriptList(TestSetup.server_id);
		assertTrue(scriptList.size() == 3);
		scriptList = ControlDataStore.getInactiveScriptList(TestSetup.server_id);
		assertTrue(scriptList.size() == 1);
		
		// Check script results logging
		int scriptLogId = ControlDataStore.logScriptStart(script);
		assertTrue(scriptLogId > 0);
		
		Target target = new Target(TestSetup.connection,TestSetup.target_id);
		int scriptTargetLogId = ControlDataStore.logScriptTargetStart(scriptLogId, target);
				
		ControlDataStore.logScriptEnd(script, scriptLogId);
		
		ExecutionThread thread = new ExecutionThread(script,target);
		thread.setScriptTargetLogId(scriptTargetLogId);
		ControlDataStore.logScriptTargetEnd(thread);
		
		// Check that values logged into the database are correct
		String sql = "select count(*) from psd_script_log "
				+ "where script_log_id = ? "
				+ "and status_number = ? ";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptLogId);
		st.setInt(2, ExecutionResult.RESULT_FINISHED);
		ResultSet rs = st.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		assertTrue(count > 0);
		
		sql = "select count(*) from psd_script_target_log "
				+ "where script_target_log_id = ? "
				+ "and status_number = ? ";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptTargetLogId);
		st.setInt(2, ExecutionResult.RESULT_NOT_FINISHED);
		rs = st.executeQuery();
		rs.next();
		count = rs.getInt(1);
		assertTrue(count > 0);		

		TestSetup.done();
	}

}
