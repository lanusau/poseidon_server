package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;

public class AlerterTest {

	@Test
	public void testAlert() throws ConfigurationException, SQLException {
		
		TestSetup.setup();	
		
		// Start fake mail server that will receive email messages
		final GreenMail fakeMailServer = new GreenMail();
		fakeMailServer.start();
		PoseidonConfiguration.getConfiguration().setProperty("mail.smtp.port", String.valueOf(fakeMailServer.getSmtp().getPort()));
		
		// Create AlertMessage object
		Script script = new Script(TestSetup.connection,TestSetup.script_id);		
		Target target = new Target(TestSetup.connection,TestSetup.target_id);
		ExecutionResult fakeExecutionResult = new ExecutionResult();
		fakeExecutionResult.setResultCode(ExecutionResult.RESULT_FINISHED_NOT_TRIGGERED);		
		fakeExecutionResult.setSeverity(Script.SEVERITY_LOW);
		AlertMessage alertMessage = new AlertMessage(script,target,fakeExecutionResult);
		
		// Set alert that GreenMail will receive locally
		Alerter.init();
		ArrayList<String> emailAddresses = new ArrayList<String>();
		emailAddresses.add("whatever@some.host.com");
		Alerter.alert(emailAddresses, alertMessage);
		assertEquals(1, fakeMailServer.getReceivedMessages().length);
		
		TestSetup.done();
	}

}
