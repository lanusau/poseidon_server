package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.Target;
import com.untd.database.poseidon.model.AlertMessage;
import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.util.Alerter;

public class AlerterTest {

	@Test
	public void testAlert() throws ConfigurationException, SQLException, IOException {
		
		TestSetup.setup();	
		
		// Start fake mail server that will receive email messages
		final GreenMail fakeMailServer = new GreenMail();
		fakeMailServer.start();
		PoseidonConfiguration.getConfiguration().setProperty("mail.smtp.port", String.valueOf(fakeMailServer.getSmtp().getPort()));
		
		// Create AlertMessage object
		Script script = ControlDataStore.getScript(TestSetup.script_id);		
		Target target = ControlDataStore.getScriptTargets(script,PoseidonConfiguration.getConfiguration().getInt("serverId")).get(0);
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
		
		fakeMailServer.stop();
		
		TestSetup.done();
	}

}
