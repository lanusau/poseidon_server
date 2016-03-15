package com.untd.database.poseidon;


import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.ScriptLog;
import com.untd.database.poseidon.data.ScriptTargetLog;
import com.untd.database.poseidon.data.Target;
import com.untd.database.poseidon.executor.ExecutionThread;
import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.ExecutionResultRow;

public class ControlDataStoreTest {

	@Test
	public void testControlStore() throws ConfigurationException, SQLException, IOException {
		TestSetup.setup();	
		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		assertNotNull(script);

		// There should be 3 active and 2 inactive scripts
		List<Script> scriptList = ControlDataStore.getActiveScriptList(TestSetup.server_id);
		assertEquals(3,scriptList.size());
		scriptList = ControlDataStore.getInactiveScriptList(TestSetup.server_id);
		assertEquals(2,scriptList.size());
		
		// There should be 1 target assigned to the script
		List<Target> targets = ControlDataStore.getScriptTargets(script,PoseidonConfiguration.getConfiguration().getInt("serverId"));
		assertEquals(1,targets.size());
		
		
		// Check script results logging
		ScriptLog scriptLog = ControlDataStore.logScriptStart(script);
		assertNotNull(scriptLog.getScriptLogId());
		
		// Check actual values in log tables
		String sql = "select count(*) from psd_script_log "
						+ "where script_log_id = ? "
						+ "and status_number = ? ";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptLog.getScriptLogId());
		st.setInt(2, ExecutionResult.RESULT_NOT_FINISHED);
		ResultSet rs = st.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		assertTrue(count > 0);
		
		Target target = targets.get(0);
		ScriptTargetLog scriptTargetLog = ControlDataStore.logScriptTargetStart(scriptLog, target);
				
		ControlDataStore.logScriptEnd(script, scriptLog);
		
		// Check actual values in log tables
		sql = "select count(*) from psd_script_log "
				+ "where script_log_id = ? "
				+ "and status_number = ? ";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptLog.getScriptLogId());
		st.setInt(2, ExecutionResult.RESULT_FINISHED);
		rs = st.executeQuery();
		rs.next();
		count = rs.getInt(1);
		assertTrue(count > 0);
		
		ExecutionThread thread = new ExecutionThread(script,target);
		ExecutionResult result = new ExecutionResult();
		
		// Build fake result set
		sql = "select * from psd_script";
		st = TestSetup.connection.prepareStatement(sql);
		rs = st.executeQuery();
		rs.next();
		ExecutionResultRow resultRow = new ExecutionResultRow(rs);
		result.add(resultRow);
		thread.setExecutionResult(result);
		
		ControlDataStore.logScriptTargetEnd(scriptTargetLog,thread.getExecutionResult());
		
		// Check actual values in log tables
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
		
		sql = "select count(*) from psd_script_target_row_log "
				+ "where script_target_log_id = ?";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, scriptTargetLog.getScriptTargetLogId());
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
