package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icegreen.greenmail.util.GreenMail;

public class PoseidonServerTest {

	@Test
	public void testPoseidonServer() throws ConfigurationException, SQLException, InterruptedException, MessagingException {
		TestSetup.setup();
		
		Logger logger = LoggerFactory.getLogger(PoseidonServerTest.class);
		
		// Mark all scripts inactive expect the script we want to fire
		String sql = "update psd_script set status_code ='I' "
				+ "where script_id != ?";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, TestSetup.script_id);
		st.execute();					
		
		// Update information for the target where the script will be
		// executed to be the same as the one we are connected to
		sql = "update psd_target "
				+ "set hostname = ?,"
				+ "database_name = ? "
				+ "where target_id = ?";
		
		String controlConnectionDescription = PoseidonConfiguration.getConfiguration().getString("controlConnectionDescription");
		String hostName = controlConnectionDescription.split("/+")[1];
		String dbName = controlConnectionDescription.split("/+")[2];
		
		st = TestSetup.connection.prepareStatement(sql);
		st.setString(1, hostName);
		st.setString(2, dbName);
		st.setInt(3, TestSetup.target_id);
		st.execute();
		
		// Set property for serverId
		PoseidonConfiguration.getConfiguration().setProperty("serverId", String.valueOf(TestSetup.server_id));
			
		// Start fake mail server that will receive email messages
		final GreenMail fakeMailServer = new GreenMail();
		fakeMailServer.start();
		PoseidonConfiguration.getConfiguration().setProperty("mail.smtp.port", String.valueOf(fakeMailServer.getSmtp().getPort()));
				
		// Update script schedule to start in 1 minutes				
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 2);
		// Add 10 second just in case we are close to next minute
		calendar.add(Calendar.SECOND, 10);

		int minute = calendar.get(Calendar.MINUTE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		sql = "update psd_script set " 
				+ "schedule_min = ? ,"
				+ "schedule_hour = ? ," 
				+ "schedule_day = ? "
				+ "where script_id = ?";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, minute);
		st.setInt(2, hour);
		st.setInt(3, day);
		st.setInt(4, TestSetup.script_id);
		st.execute();	
				
		// Start Poseidon server in separate thread 
		// and wait 2 minutes for the alert to be sent
		PoseidonServer.init();
		Thread poseidonServerThread = new Thread( new Runnable() {
	        public void run()  {
	        	PoseidonServer.run();
	        }
	    });		
		poseidonServerThread.setName("PoseidonServer");
		poseidonServerThread.start();

		logger.warn("This test will run for 2 minutes");		
		Thread.sleep(1*60*1000);
		logger.warn("1 more minute");
		Thread.sleep(1*60*1000);
		
		PoseidonServer.disable();
		
		// Give a server few seconds to shutdown
		Thread.sleep(5*1000);
		
		// Thread now should be dead
		assertFalse(poseidonServerThread.isAlive());
		
		// Should have been 1 email sent
		assertEquals(1, fakeMailServer.getReceivedMessages().length);
		
		// Subject is set to [%t] Test
		MimeMessage message = fakeMailServer.getReceivedMessages()[0];
		Target target = new Target(TestSetup.connection,TestSetup.target_id);
		assertEquals(message.getSubject(),"["+target.getName()+"] Test");
		
		fakeMailServer.stop();
		
		TestSetup.done();
	}

}
