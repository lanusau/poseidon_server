package com.untd.database.poseidon.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

import java.sql.DriverManager;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.untd.database.poseidon.dao.ScriptDao;
import com.untd.database.poseidon.model.Settings;
import com.untd.database.poseidon.model.database.Script;

/**
 * Scheduled task that adds/removes scripts to Quartz scheduler as needed
 *
 */
public class ScriptSchedulingTask implements InitializingBean {
	
	@Autowired
	private ScriptDao scriptDao;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
	@Autowired
	private Settings settings;
	
	private Scheduler scheduler;
	private int rescheduleCount = 0;
	private Logger logger;

	/**
	 * Main worker method
	 */
	@Scheduled(fixedDelay=30000)
	public void work() {		
		try {
			// Get a list of active scripts and add them to the schedule	
			List<Script> activeScriptList = scriptDao.getActiveScriptList(settings.getServerId()); 
			Set<String> activeScriptIds = new HashSet<String>();
			for (final Script script : activeScriptList) {
				activeScriptIds.add(script.getScriptIdStr());
				if (scriptScheduled(script) ) {
					if (needsUpdate(script)) {
						rescheduleScript(script);
					}
				} else {
					scheduleScript(script);
				}
			}
			
			//Find any scripts in schedule that are no longer in the active script list
			for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
				if (!activeScriptIds.contains(jobKey.getName())) {
					unscheduleScript(jobKey);
				}
			}
			
		} catch (SchedulerException e) {
			logger.error("Scheduler error:"+e.getMessage());
		}
	}
	
	/**
	 * Check if script is already scheduled
	 * @param script - {@link Script}
	 * @return - true if script is already scheduled in Quartz, false otherwise
	 * @throws SchedulerException
	 */
	private boolean scriptScheduled(Script script) throws SchedulerException {
		return scheduler.checkExists(jobKey(script.getScriptIdStr()));		
	}
	
	/**
	 * Check if scheduler needs to be updated with new script information
	 * @param script - {@link Script}
	 * @return true if script in Quartz has older update date, false otherwire
	 * @throws SchedulerException
	 */
	private boolean needsUpdate(Script script) throws SchedulerException {
		JobDataMap jobData = scheduler.getJobDetail(jobKey(script.getScriptIdStr())).getJobDataMap();
		return jobData.getLong("update_sysdate") != script.getUpdateSysdate().getTime();
	}
	
	/**
	 * Reschedule particular script in Quartz
	 * 
	 * @param script  - {@link Script} to schedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private void rescheduleScript(Script script) throws SchedulerException {
		JobDetail jobDetail = scheduler.getJobDetail(jobKey(script.getScriptIdStr()));
		JobDataMap jobData = jobDetail.getJobDataMap();

		// Script was updated, we need to change trigger schedule
		logger.info("Rescheduling script:" + script.getName());
		rescheduleCount++;

		// Update timestamp in the job detail
		jobData.put("update_sysdate", script.getUpdateSysdate().getTime());
		scheduler.addJob(jobDetail, true);

		Trigger trigger = newTrigger()
				.withIdentity(script.getScriptIdStr())
				.withSchedule(cronSchedule(script.getFullSchedule()))
				.build();
		scheduler.rescheduleJob(triggerKey(script.getScriptIdStr()), trigger);

	}
	
	/**
	 * Schedule new script in Quartz
	 * 
	 * @param script - {@link Script} to schedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private void scheduleScript(Script script) throws SchedulerException {
		// No job for this script yet, create new one
		logger.info("Scheduling script [" + script.getName() + "] using schedule [" + script.getFullSchedule() + "]");

		JobDetail jobDetail = newJob(PoseidonJob.class)
				.withIdentity(script.getScriptIdStr())
				.usingJobData("scriptId", script.getScriptId())
				.usingJobData("update_sysdate", script.getUpdateSysdate().getTime())
				.storeDurably()
				.build();

		Trigger trigger = newTrigger()
				.withIdentity(script.getScriptIdStr())
				.withSchedule(cronSchedule(script.getFullSchedule()))
				.build();

		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	/**
	 * Unschedule particular script from Quartz
	 * @param script - {@link Script} to unschedule
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	private void unscheduleScript(JobKey jobKey) throws SchedulerException {
			logger.info("Unscheduling script:"+jobKey.getName());
			scheduler.deleteJob(jobKey);	
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger = LoggerFactory.getLogger(ScriptSchedulingTask.class);
		
		// Obtain Quartz scheduler instance
		scheduler = schedulerFactoryBean.getScheduler();
		
		// Register JDBC drivers
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());			
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		DriverManager.registerDriver(new org.postgresql.Driver());
	}

	/**
	 * @return the rescheduleCount
	 */
	public int getRescheduleCount() {
		return rescheduleCount;
	}

	/**
	 * @param rescheduleCount the rescheduleCount to set
	 */
	public void setRescheduleCount(int rescheduleCount) {
		this.rescheduleCount = rescheduleCount;
	}
}
