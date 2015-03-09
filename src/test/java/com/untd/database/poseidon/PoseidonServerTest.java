package com.untd.database.poseidon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icegreen.greenmail.util.GreenMail;
import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.Target;

public class PoseidonServerTest {
	
	static Logger logger;
	
	GreenMail fakeMailServer;
	
	/**
	 * Setup configuration
	 * @throws ConfigurationException
	 * @throws SQLException
	 * @throws IOException 
	 */
	@BeforeClass
	public static void BeforeClass() throws ConfigurationException, SQLException, IOException {	
		String sql;
		PreparedStatement st;
		
		logger = LoggerFactory.getLogger(PoseidonServerTest.class);
		
		TestSetup.setup();	
		
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
		
		// Reduce sleep time between Poseidon check cycles
		PoseidonConfiguration.getConfiguration().setProperty("mainThreadSleepMs", "500");		
		
		logger.warn("This test may run up to 2 minutes");
	}
	
	/**
	 * Start fake mail server that will receive email messages
	 */
	@Before
	public void startEmailServer() {
		fakeMailServer = new GreenMail();
		fakeMailServer.start();
		PoseidonConfiguration.getConfiguration().setProperty("mail.smtp.port", String.valueOf(fakeMailServer.getSmtp().getPort()));		
	}
	
	/**
	 * Stop fake email server
	 */
	@After
	public void stopEmailServer() {
		fakeMailServer.stop();
	}

	/**
	 * Test that scripts are scheduled and emails are sent
	 * @throws SQLException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws CnameCheckException 
	 */
	@Test
	public void testScriptScheduling() throws SQLException, InterruptedException, MessagingException, CnameCheckException {			
		
		// Set the schedule for all scripts (except the one we will test) to be
		// way in the future			
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 12);
				
		String sql = "update psd_script set " 
				+ "schedule_min = ? ,"
				+ "schedule_hour = ? ," 
				+ "schedule_day = ? "
				+ "where script_id != ?";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, calendar.get(Calendar.MINUTE));
		st.setInt(2, calendar.get(Calendar.HOUR_OF_DAY));
		st.setInt(3, calendar.get(Calendar.DAY_OF_MONTH));
		st.setInt(4, TestSetup.script_id);
		st.execute();			
				
		// For the our script, set the schedule to start in 1 minutes				
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		// Add 10 second just in case we are close to next minute
		calendar.add(Calendar.SECOND, 10);

		sql = "update psd_script set " 
				+ "schedule_min = ? ,"
				+ "schedule_hour = ? ," 
				+ "schedule_day = ? "
				+ "where script_id = ?";
		st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, calendar.get(Calendar.MINUTE));
		st.setInt(2, calendar.get(Calendar.HOUR_OF_DAY));
		st.setInt(3, calendar.get(Calendar.DAY_OF_MONTH));
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
		
		// Email should be sent in maximum 2 minutes
		assertTrue(fakeMailServer.waitForIncomingEmail(2*60*1000, 1));
				
		PoseidonServer.disable();
		
		// Give a server some time to shut down
		logger.debug("Waiting for server to shutdown for "+PoseidonConfiguration.getConfiguration().getInt("mainThreadSleepMs")*6+" ms");
		Thread.sleep(PoseidonConfiguration.getConfiguration().getInt("mainThreadSleepMs")*6);
		
		if (poseidonServerThread.isAlive()) {
			logger.debug("Server is still alive");
			Map<Thread,StackTraceElement[]> map = Thread.getAllStackTraces();
			for (Thread t : map.keySet()) {
				System.err.println("Thread:"+t.getName());
				for (StackTraceElement e : map.get(t)) {
					System.err.println(e.getClassName()+"."+e.getMethodName()+":"+e.getLineNumber());
				}
				System.err.println("\n");
			}
		}
		// Thread now should be dead
		assertFalse(poseidonServerThread.isAlive());
		
		// Subject is set to [%t] Test
		MimeMessage message = fakeMailServer.getReceivedMessages()[0];
		
		Script script = ControlDataStore.getScript(TestSetup.script_id);
		Target target = ControlDataStore.getScriptTargets(script,PoseidonServer.serverId).get(0);
		
		assertEquals(message.getSubject(),"["+target.getName()+"] Test");		

	}
	
	/**
	 * Test that script rescheduling works as expected
	 * @throws InterruptedException
	 * @throws SQLException
	 * @throws CnameCheckException 
	 */
	@Test
	public void testScriptReScheduling() throws InterruptedException, SQLException, CnameCheckException {
		
		// Start Poseidon server in separate thread 
		PoseidonServer.init();
		Thread poseidonServerThread = new Thread( new Runnable() {
			public void run()  {
				PoseidonServer.run();
			}
		});		
		poseidonServerThread.setName("PoseidonServer");
		poseidonServerThread.start();
		
		// Reschedule count should be 0 at the beginning 
		assertEquals(0,PoseidonServer.getRescheduleCount());
		
		// Sleep few seconds to allow server to schedule scripts
		Thread.sleep(3*1000);
		
		// Change update date on the script
		String sql = "update psd_script set update_sysdate = now() "
				+ "where script_id = ?";
		PreparedStatement st = TestSetup.connection.prepareStatement(sql);
		st.setInt(1, TestSetup.script_id);
		st.execute();
		
		// Wait few cycles
		Thread.sleep(PoseidonConfiguration.getConfiguration().getInt("mainThreadSleepMs")*3);
		
		// There should be only 1 reschedule event
		assertEquals(1,PoseidonServer.getRescheduleCount());
		
		PoseidonServer.disable();
		poseidonServerThread.join();
	}
	
	@Test
	public void testCnameCheckOnStartup() throws InterruptedException {
		// Set CNAME parameter
		PoseidonConfiguration.getConfiguration().setProperty("cname", "www.google.com");
		
		// Init should raise an exception
		try {
			PoseidonServer.init();
			fail("Should have raised an Exception");
		} catch (CnameCheckException e) {
		} finally {
			PoseidonConfiguration.getConfiguration().clearProperty("cname");
			PoseidonServer.setCnameChecker(null);
		}
	}
	
	@Test
	public void testCnameShutdown() throws CnameCheckException, InterruptedException {
		// Create fake CNAME checker and
		FakeCnameChecker fakeCnameChecker = new FakeCnameChecker();
		PoseidonServer.init();
		PoseidonServer.setCnameChecker(fakeCnameChecker);
		
		// Run server
		Thread poseidonServerThread = new Thread( new Runnable() {
			public void run()  {
				PoseidonServer.run();
			}
		});		
		poseidonServerThread.setName("PoseidonServer");
		poseidonServerThread.start();
		
		// Wait few cycles
		Thread.sleep(PoseidonConfiguration.getConfiguration().getInt("mainThreadSleepMs")*3);
				
		// Thread should be running
		assertTrue(poseidonServerThread.isAlive());
		
		// Simulate CNAME change
		fakeCnameChecker.setMatch(false);
		
		// Wait few cycles
		Thread.sleep(PoseidonConfiguration.getConfiguration().getInt("mainThreadSleepMs")*3);
		
		// Thread should have exited
		assertFalse(poseidonServerThread.isAlive());
		
		PoseidonServer.setCnameChecker(null);
	}
	
	/**
	 * Discard test setup
	 * @throws SQLException
	 */
	@AfterClass
	public static void AfterClass() throws SQLException {											
		TestSetup.done();						
	}		

}
