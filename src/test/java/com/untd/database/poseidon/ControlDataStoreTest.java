package com.untd.database.poseidon;


import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.ScriptLog;
import com.untd.database.poseidon.data.ScriptTargetLog;
import com.untd.database.poseidon.data.Target;

public class ControlDataStoreTest {

	@Test
	public void testControlStore() throws ConfigurationException, SQLException, IOException {
		TestSetup.setup();	
		
		ControlDataStore.init(ConfigurationConverter.getProperties(PoseidonConfiguration.getConfiguration()));
		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);

		// There should be 3 active and 1 inactive scripts
		List<Script> scriptList = ControlDataStore.getActiveScriptList(TestSetup.server_id);
		assertEquals(3,scriptList.size());
		scriptList = ControlDataStore.getInactiveScriptList(TestSetup.server_id);
		assertEquals(1,scriptList.size());
		
		// There should be 1 target assigned to the script
		List<Target> targets = ControlDataStore.getScriptTargets(script,PoseidonConfiguration.getConfiguration().getInt("serverId"));
		assertEquals(1,targets.size());
		
		
		// Check script results logging
		ScriptLog scriptLog = ControlDataStore.logScriptStart(script);
		assertNotNull(scriptLog.getScriptLogId());
		
		Target target = targets.get(0);
		ScriptTargetLog scriptTargetLog = ControlDataStore.logScriptTargetStart(scriptLog, target);
				
		ControlDataStore.logScriptEnd(script, scriptLog);
		
		ExecutionThread thread = new ExecutionThread(script,target);	
		ControlDataStore.logScriptTargetEnd(scriptTargetLog,thread.getExecutionResult());
		
		// Check that values logged into the database are correct
		String sql = "select count(*) from psd_script_log "
				+ "where script_log_id = ? "
				+ "and status_number = ? ";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptLog.getScriptLogId());
		st.setInt(2, ExecutionResult.RESULT_FINISHED);
		ResultSet rs = st.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		assertTrue(count > 0);
		
		sql = "select count(*) from psd_script_target_log "
				+ "where script_target_log_id = ? "
				+ "and status_number = ? ";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptTargetLog.getScriptTargetLogId());
		st.setInt(2, ExecutionResult.RESULT_NOT_FINISHED);
		rs = st.executeQuery();
		rs.next();
		count = rs.getInt(1);
		assertTrue(count > 0);		
		
		// Test script miss fire log
		ControlDataStore.logScriptMissfire(script);
		
		
		// Test script timeout log
		ControlDataStore.logScriptTimeout(script, scriptLog);
		sql = "select count(*) from psd_script_log "
				+ "where script_log_id = ? "
				+ "and status_number = ? ";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptLog.getScriptLogId());
		st.setInt(2, ExecutionResult.RESULT_TIMEDOUT);
		rs = st.executeQuery();
		rs.next();
		count = rs.getInt(1);
		assertTrue(count > 0);
		
		// Test heartbeat 
		ControlDataStore.logHeartbeat(PoseidonConfiguration.getConfiguration().getInt("serverId"));
		

		TestSetup.done();
	}

}
