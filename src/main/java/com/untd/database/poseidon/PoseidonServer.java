package com.untd.database.poseidon;

import java.sql.DriverManager;
import java.text.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This is the main class for the Poseidon monitoring server. 
 * 
 */
public class PoseidonServer {
	
	static private String jdbcURL,username,password;
	static private SchedulerFactory schedFact;
	static private Scheduler sched;	
	static private Configuration prop;
	static private Logger logger;
	
	/**
	 * Server ID, obtained from the property file
	 */
	static public int serverId;	

	/**
	 * 
	 * Main method for the PoseidonServer class.
	 * 
	 */
	public static void main(String[] args) {		
		
		// Initialize logger	
		logger = LoggerFactory.getLogger(PoseidonServer.class);
		
		// Disable DNS cache
		java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "0");				 					   									  
		
		// Setup configuration
		try {			
			PoseidonConfiguration.init();
			PoseidonConfiguration.addPropertyFile("poseidon-default.conf");
			PoseidonConfiguration.addPropertyFile("poseidon.conf");
			SqlText.init();
		} catch (ConfigurationException e) {
			logger.error("Error trying to load properties: "+ e.getMessage());
			System.exit(1);
		}				
		
		prop = PoseidonConfiguration.getConfiguration();
		
		// Check for mandatory properties
		assertMandatoryProperty("serverId");
		assertMandatoryProperty("decryptionSecret");
		assertMandatoryProperty("controlConnectionDescription");
		assertMandatoryProperty("username");
		assertMandatoryProperty("password");
						
		serverId = prop.getInt("serverId");	
		jdbcURL = prop.getString("controlConnectionDescription");
		username = prop.getString("username");
		password = prop.getString("password");		
											
		logger.info("Starting server ID:"+serverId);
						
		// Initialize Alerter
		Alerter.init();
		
		// Initialize ControlDataStore
		try {
			// Load JDBC drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			DriverManager.registerDriver(new org.postgresql.Driver());
			
			ControlDataStore.init(jdbcURL,username,password);
		} catch (Exception e) {
			logger.error("Can not connect to control database:"+e.getMessage());
			return;
		}
		
		// Create new schedule factory
		try {					
			schedFact = new org.quartz.impl.StdSchedulerFactory();
			sched = schedFact.getScheduler();
			sched.addGlobalTriggerListener(new PoseidonTriggerListener());

			sched.start();	
		} catch (Exception e) {
			logger.error("Error initializing scheduler:"+e.getMessage());
			return;
		}
		
		
		// Loop forever until process is killed
		while(true) {
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
				
				Thread.sleep(prop.getInt("mainThreadSleepSec*1000"));
																				

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}

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
		CronTrigger trigger;		
		
		// Try to find job with this script id first		
		try {
			if ((jobDetail = sched.getJobDetail(script.getScript_id()+"",Scheduler.DEFAULT_GROUP)) != null) {
				
				// Script already exists, check if it has been updated
				jobData = jobDetail.getJobDataMap();
				if (jobData.getLong("update_sysdate") != script.getUpdate_sysdate().getTime()) {
					// Script was updated, we need to change trigger schedule
					logger.info("Rescheduling script:"+script.getName());
					
					trigger = new CronTrigger(script.getScript_id()+"",
							Scheduler.DEFAULT_GROUP,
							script.getFullSchedule());
					trigger.setJobName(script.getScript_id()+"");
					trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
					sched.rescheduleJob(script.getScript_id()+"",
							Scheduler.DEFAULT_GROUP,
							trigger);
					// Update timestamp in the job detail
					jobData.put("update_sysdate",script.getUpdate_sysdate().getTime());
					jobDetail.setJobDataMap(jobData);
					
				}
			} else {	
				// No job for this script yet, create new one
				logger.info("Scheduling script:"+script.getName());
				
				jobDetail = new JobDetail(script.getScript_id()+"",
						Scheduler.DEFAULT_GROUP,
		                PoseidonJob.class);
				
				jobData = new JobDataMap();
				jobData.put("script_id",script.getScript_id());
				jobData.put("update_sysdate",script.getUpdate_sysdate().getTime());
				
				jobDetail.setJobDataMap(jobData);
				
				trigger = new CronTrigger(script.getScript_id()+"",
						Scheduler.DEFAULT_GROUP,
						script.getFullSchedule());
				
				sched.scheduleJob(jobDetail,trigger);
			}
			
		} catch (SchedulerException e) {
			logger.error("Can not schedule script "+script.getScript_id()+":"+e.getMessage());			
		} catch (ParseException e) {			
			logger.error("Can not schedule script "+script.getScript_id()+":"+e.getMessage());
			logger.error("Schedule:"+script.getFullSchedule());
		}
		
	}
	
	/**
	 * Unschedule particular script from Quartz
	 * @param script Script to unschedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private static void unscheduleScript(Script script) {
		JobDetail jobDetail;			
		
		try {
			if ((jobDetail = sched.getJobDetail(script.getScript_id()+"",Scheduler.DEFAULT_GROUP)) == null) {
				// There is no scheduled job - return
				return;
			}
		} catch (SchedulerException e) {
			logger.error("Error looking up script "+script.getScript_id()+":"+e.getMessage());
			return;
		}
		
		try {
			logger.info("Unscheduling script:"+script.getName());
			sched.unscheduleJob(jobDetail.getName(),
						Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			logger.error("Error unscheduling script "+script.getScript_id()+":"+e.getMessage());
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
	
}
