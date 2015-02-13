package com.untd.database.poseidon;

import static org.quartz.JobKey.*;
import static org.quartz.TriggerKey.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

import java.sql.DriverManager;
import java.text.ParseException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.untd.database.poseidon.data.Script;

/**
 * 
 * This is the main class for the Poseidon monitoring server. 
 * 
 */
public class PoseidonServer {
	
	/**
	 * Server ID, obtained from the property file
	 */
	static public int serverId;	
	
	static private SchedulerFactory schedFact;
	static private Scheduler sched;	
	static private Configuration prop;
	static private Logger logger;	
	static private volatile boolean enabled;
	static private volatile long rescheduleCount;

	/**
	 * Main method for the PoseidonServer class.
	 */
	public static void main(String[] args) {		
		init();
		run();
	}	
	
	/**
	 * Initialize configuration
	 */
	private static void initConfiguration() {
		// Configuration will be already initialized 
		// in JUnit test
		if (PoseidonConfiguration.isInitialized()) {
			return;
		}
		try {
			PoseidonConfiguration.init();
			PoseidonConfiguration.addPropertyFile("poseidon.conf");
			PoseidonConfiguration.addPropertyFile("poseidon-default.conf");			
		} catch (ConfigurationException e) {
			logger.error("Error trying to load properties: " + e.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * Initialize server
	 */
	public static void init() {
		
		// Initialize logger	
		logger = LoggerFactory.getLogger(PoseidonServer.class);
		
		// Disable DNS cache
		java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "0");				 					   									  
		
		// Initialize configuration
		initConfiguration();				
		
		prop = PoseidonConfiguration.getConfiguration();
		
		// Check for mandatory properties
		assertMandatoryProperty("serverId");
		assertMandatoryProperty("decryptionSecret");
		assertMandatoryProperty("controlConnectionDescription");
		assertMandatoryProperty("username");
		assertMandatoryProperty("password");
						
		serverId = prop.getInt("serverId");		
											
		logger.info("Starting server ID:"+serverId);
						
		// Initialize Alerter
		Alerter.init();
		
		// Initialize ControlDataStore
		try {
			// Load JDBC drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			DriverManager.registerDriver(new org.postgresql.Driver());
			
			ControlDataStore.init(ConfigurationConverter.getProperties(prop));
		} catch (Exception e) {
			logger.error("Can not connect to control database:"+e.getMessage());
			System.exit(1);
		}
		
		// Create and start Quartz scheduler instance
		try {					
			schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();
			sched.getListenerManager().addTriggerListener(new PoseidonTriggerListener());

			sched.start();	
		} catch (Exception e) {
			logger.error("Error initializing scheduler:"+e.getMessage());
			return;
		}
		
		enabled = true;
		rescheduleCount = 0;
	}
	
	
	/**
	 * Run the server. 
	 */
	public static void run () {
		
		
		// Loop while enabled flag is set, which pretty much means
		// until killed, because enabled flag is only reset in testing. 
		while(enabled) {
			try {
				
				// Get a list of active scripts and add them to the schedule				
				for (final Script script : ControlDataStore.getActiveScriptList(serverId)) {				
					scheduleScript(script);
				}
				
				// Get a list of inactive scripts and remove them from the schedule				
				for (final Script script : ControlDataStore.getInactiveScriptList(serverId)) {				
					unscheduleScript(script);
				}
				
				// Send heart beat to the server table
				ControlDataStore.logHeartbeat(serverId);
				
				Thread.sleep(prop.getInt("mainThreadSleepMs",5000));
																				

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		
		try {
			sched.shutdown(true);
		} catch (SchedulerException e) {
			// Do nothing, we are shutting down
		}
	}
	
	/**
	 * Stop Poseidon server. This is used in testing only
	 */
	public static void disable() {
		enabled = false;
	}
	
	/**
	 * Schedule particular script in Quartz
	 * 
	 * @param script Script to schedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private static void scheduleScript(Script script) throws ParseException  {
		JobDetail jobDetail;
		JobDataMap jobData;
		Trigger  trigger;		
		
		// Try to find job with this script id first		
		try {
			if ((jobDetail = sched.getJobDetail(jobKey(script.getScriptIdStr()))) != null) {
				
				// Script already exists, check if it has been updated
				jobData = jobDetail.getJobDataMap();
				if (jobData.getLong("update_sysdate") != script.getUpdateSysdate().getTime()) {
					
					// Script was updated, we need to change trigger schedule
					logger.info("Rescheduling script:"+script.getName());
					rescheduleCount++;
					
					// Update timestamp in the job detail
					jobData.put("update_sysdate",script.getUpdateSysdate().getTime());
					sched.addJob(jobDetail, true);
					
					trigger = newTrigger()
						    .withIdentity(script.getScriptIdStr())
						    .withSchedule(cronSchedule(script.getFullSchedule()))
						    .build();
					sched.rescheduleJob(triggerKey(script.getScriptIdStr()),
							trigger);
					
				}
			} else {	
				// No job for this script yet, create new one
				logger.info("Scheduling script ["+script.getName()+"] using schedule ["+script.getFullSchedule()+"]");
				
				jobDetail = newJob(PoseidonJob.class)
					    .withIdentity(script.getScriptIdStr())
					    .usingJobData("script_id", script.getScriptId())
					    .usingJobData("update_sysdate", script.getUpdateSysdate().getTime())
					    .storeDurably()
					    .build();
				
				trigger = newTrigger()
					    .withIdentity(script.getScriptIdStr())
					    .withSchedule(cronSchedule(script.getFullSchedule()))
					    .build();
				
				sched.scheduleJob(jobDetail,trigger);
			}
			
		} catch (SchedulerException e) {
			logger.error("Can not schedule script "+script.getScriptId()+":"+e.getMessage());			
		} 
		
	}
	
	/**
	 * Unschedule particular script from Quartz
	 * @param script Script to unschedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private static void unscheduleScript(Script script) {
		try {
			if ((sched.getJobDetail(jobKey(script.getScriptIdStr()))) == null) {
				// There is no scheduled job - return
				return;
			}
		} catch (SchedulerException e) {
			logger.error("Error looking up script "+script.getScriptId()+":"+e.getMessage());
			return;
		}
		
		try {
			logger.info("Unscheduling script:"+script.getName());
			sched.deleteJob(jobKey(script.getScriptIdStr()));
		} catch (SchedulerException e) {
			logger.error("Error unscheduling script "+script.getScriptId()+":"+e.getMessage());
			return;
		}
	}
		
	/**
	 * Check if particular mandatory property is set	
	 * 
	 * @param propertyName
	 */
	private static void assertMandatoryProperty(String propertyName) {
		if (prop.getString(propertyName) == null) {
			logger.error("Mandatory property "+propertyName+" is not set");
			System.exit(1);
		}
	}

	/**
	 * @return the rescheduleCount
	 */
	public static long getRescheduleCount() {
		return rescheduleCount;
	}
		
	
}
