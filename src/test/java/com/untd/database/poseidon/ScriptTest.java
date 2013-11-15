package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.sql.SQLException;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;


public class ScriptTest {
	
	@BeforeClass
	public static void BeforeClass() throws SQLException, ConfigurationException {
		
		TestSetup.setup();		
	}

	@Test
	public void testScriptCreation() throws SQLException {		
		Script script = new Script(TestSetup.connection, TestSetup.script_id);
		assertNotNull(script);		
	}
	
	@Test
	public void testScriptTargets() throws SQLException {		
		Script script = new Script(TestSetup.connection, TestSetup.script_id);
		assertNotNull(script);	
		assertTrue(script.containsTarget(script.getTargets(), TestSetup.target_id));
	}
	
	@Test
	public void testScriptNotifications() throws SQLException {
		Script script = new Script(TestSetup.connection, TestSetup.script_id);
		assertNotNull(script);		
		assertNotNull(script.getNotificationList(Script.SEVERITY_HIGH));
		assertNotNull(script.getNotificationList(Script.SEVERITY_MED));
		assertNotNull(script.getNotificationList(Script.SEVERITY_LOW));
	}
	
	@AfterClass
	public static void AfterClass() throws SQLException {
						
		TestSetup.done();
						
	}	

}
