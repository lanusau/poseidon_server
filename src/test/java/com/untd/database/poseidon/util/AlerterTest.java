package com.untd.database.poseidon.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icegreen.greenmail.util.GreenMail;
import com.untd.database.poseidon.config.TestConfig;
import com.untd.database.poseidon.dao.ScriptDao;
import com.untd.database.poseidon.dao.TargetDao;
import com.untd.database.poseidon.model.AlertMessage;
import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.Severity;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.Target;
import com.untd.database.poseidon.util.Alerter;

@RunWith(SpringJUnit4ClassRunner.class)
public class AlerterTest extends TestConfig {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private ScriptDao scriptDao;
	
	@Autowired
	private TargetDao targetDao;
	
	@Autowired
	private Alerter alerter;
	
	@Test
	@Transactional
	public void testAlert() throws SQLException, IOException {
		
		// Start fake mail server that will receive email messages
		final GreenMail fakeMailServer = new GreenMail();
		fakeMailServer.start();
		JavaMailSenderImpl javaMailSenderImp = ((JavaMailSenderImpl)javaMailSender);
		javaMailSenderImp.setHost("localhost");
		javaMailSenderImp.setPort(fakeMailServer.getSmtp().getPort());

		// Create AlertMessage object
		Script script = scriptDao.findOne(1);		
		Target target = targetDao.findOne(1);
		ExecutionResult fakeExecutionResult = new ExecutionResult();
		fakeExecutionResult.setResultCode(ExecutionResult.RESULT_FINISHED_NOT_TRIGGERED);		
		fakeExecutionResult.setSeverity(Severity.HIGH);
		AlertMessage alertMessage = new AlertMessage(script,target,fakeExecutionResult);
		
		// Set alert that GreenMail will receive locally

		ArrayList<String> emailAddresses = new ArrayList<String>();
		emailAddresses.add("whatever@some.host.com");
		alerter.alert(emailAddresses, alertMessage);
		assertEquals(1, fakeMailServer.getReceivedMessages().length);
		
		fakeMailServer.stop();
	}

}
