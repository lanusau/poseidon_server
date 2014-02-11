package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import com.untd.database.poseidon.data.Script;



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
	
	@AfterClass
	public static void AfterClass() throws SQLException {
						
		TestSetup.done();
						
	}	

}
