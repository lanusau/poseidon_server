package com.untd.database.poseidon;

import java.util.Vector;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.ScriptLog;
import com.untd.database.poseidon.data.Target;


/**
 * This is a main universal Quartz scheduler job, that executes all the scripts. 
 * Only one instance of the particular script can be running at any given time. 
 */

@DisallowConcurrentExecution
public class PoseidonJob implements Job {
	
	org.nfunk.jep.JEP expressionParser;
	Script script;
	
	/**
	 * This method gets called by Quartz when its time to execute our script
	 * @param ctx - Quartz job execution context
	 */
	public void execute(JobExecutionContext ctx) throws JobExecutionException {

		JobExecutionException exception_Blanket;
		
		Vector<ExecutionThread> executionThreads;		
		boolean threadsRunning;
		ScriptLog scriptLog;
		int scriptId=0;
		long jobStartTime,jobEndTime;
		
		int maxThreadRunTimeSec = PoseidonConfiguration.getConfiguration().getInt("maxThreadRunTimeSec",600);
		
		try {
			// Get data passed to the job and extract script ID
			JobDataMap jobData = ctx.getJobDetail().getJobDataMap();
			scriptId = jobData.getInt("script_id");		
			script = ControlDataStore.getScript(scriptId);
			
			jobStartTime = System.currentTimeMillis();
			
			// Log start of the script
			scriptLog = ControlDataStore.logScriptStart(script);
			
			// For each target in the script, start execution thread
			executionThreads = new Vector<ExecutionThread>();			
			for (final Target target : ControlDataStore.getScriptTargets(script,PoseidonServer.serverId)) {			
				final ExecutionThread thread = new ExecutionThread(script,target);
				thread.setScriptTargetLog(ControlDataStore.logScriptTargetStart(scriptLog,target));
				executionThreads.add(thread);
				thread.start();	
			}
			
			// Sleep a bit to allow threads to start up
			Thread.sleep(500);
		
			// Wait until all jobs finish or until timer expires
			threadsRunning = true;
			jobEndTime = System.currentTimeMillis();
			while (threadsRunning && ((jobEndTime-jobStartTime) < maxThreadRunTimeSec * 1000)) {
								
				threadsRunning = false;
								
				for (final ExecutionThread thread : executionThreads) {				
					threadsRunning = threadsRunning || thread.isRunning();										
				}
				
				jobEndTime = System.currentTimeMillis();
			
				// Sleep before trying again
				Thread.sleep(100);								
			}
			
			// Create a parser instance
			expressionParser = new org.nfunk.jep.JEP();
			expressionParser.addStandardFunctions();
			expressionParser.addStandardConstants();			
			
			// If timer expired, log the timeout and exit
			// There is no easy way to cleanup zombie threads, because they are most
			// probably hanging on the system network call. So we just leave them running
			if ((jobEndTime-jobStartTime) > maxThreadRunTimeSec * 1000) {
				
				ControlDataStore.logScriptTimeout(script,scriptLog);
				
				// Update status for all threads that finished, so that we can
				// see in UI which targets are hanging				
				for (final ExecutionThread thread : executionThreads) {									
					if (!thread.isRunning()) {
						thread.process(expressionParser);
						ControlDataStore.logScriptTargetEnd(thread.getScriptTargetLog(), thread.getExecutionResult());
					}
				}
				return;
			}			
			
			// First, set global severity for a script to low
			// If any targets raise higher severity - they will update script
			script.setExecutionSeverity(Script.SEVERITY_LOW);
			
			// Process each result 
			for (final ExecutionThread thread : executionThreads) {						
				thread.process(expressionParser);
				ControlDataStore.logScriptTargetEnd(thread.getScriptTargetLog(), thread.getExecutionResult());
			}
			
			ControlDataStore.logScriptEnd(script,scriptLog);
			
		} catch (Exception e) {
			// We are only allowed to throw JobExecutionException, 
			// so we have to wrap everything in try block and catch everything	
			String msg = "Script "+scriptId+" execution failed:"+e.getMessage();
			LoggerFactory.getLogger(PoseidonJob.class).warn(msg);
			exception_Blanket = new JobExecutionException(msg);
			throw exception_Blanket;
		}
		
	}
	
}
