package com.untd.database.poseidon.quartz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.untd.database.poseidon.dao.ScriptDao;
import com.untd.database.poseidon.dao.ScriptLogDao;
import com.untd.database.poseidon.dao.ScriptTargetLogDao;
import com.untd.database.poseidon.dao.TargetDao;
import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.ScriptLog;
import com.untd.database.poseidon.model.database.Target;
import com.untd.database.poseidon.task.ScriptExecutionTask;
import com.untd.database.poseidon.task.ScriptExecutionTaskBuilder;
import com.untd.database.poseidon.util.Alerter;
import com.untd.database.poseidon.util.PasswordDecryptor;


/**
 * This is a main universal Quartz scheduler job, that executes all the scripts. 
 * Only one instance of the particular script can be running at any given time. 
 */

@DisallowConcurrentExecution
public class PoseidonJob implements Job {
	
	@Autowired
	private ScriptDao scriptDao;
	
	@Autowired
	private ScriptLogDao scriptLogDao;
	
	@Autowired
	private TargetDao targetDao;
	
	@Autowired
	private ScriptTargetLogDao scriptTargetLogDao;
	
	@Autowired
	private PasswordDecryptor passwordDecryptor;
	
	@Autowired
	private Alerter alerter;
	
	@Autowired
	ExecutorService executorService;
	
	
	/**
	 * This method gets called by Quartz when its time to execute our script
	 * @param ctx - Quartz job execution context
	 */
	public void execute(JobExecutionContext ctx) throws JobExecutionException {		
		
		final List<Callable<ExecutionResult>> taskList = new ArrayList<Callable<ExecutionResult>>();
		
		int maxThreadRunTimeSec = 600;
		
		// Get data passed to the job and extract script ID
		JobDataMap jobData = ctx.getJobDetail().getJobDataMap();
		int scriptId = jobData.getInt("scriptId");		
		int serverId = jobData.getInt("serverId");
		
		try {
						
			// Load script with all dependent 
			final Script script = scriptDao.findOneWithDependencies(scriptId);
			
			// Log start of the script
			final ScriptLog scriptLog = scriptLogDao.logScriptStart(script);
			
			// For each target in the script, add script execution task
			for (final Target target : targetDao.getScriptTargets(script,serverId)) {	
				
				final ScriptExecutionTask scriptExecutionTask = new ScriptExecutionTaskBuilder()
						.withScript(script)
						.withTarget(target)
						.withAlerter(alerter)
						.withPasswordDecryptor(passwordDecryptor)
						.withScriptLog(scriptLog)
						.withScriptTargetLogDao(scriptTargetLogDao)
						.build();
						
				taskList.add(new Callable<ExecutionResult>() {

					@Override
					public ExecutionResult call() throws Exception {
						return scriptExecutionTask.execute();
					}
					
				});
			}	
			
			// Return if script has no targets to run on
			if (taskList.size() == 0) {
				scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_FINISHED,false,false);
				return;
			}
			
			List<Future<ExecutionResult>> futureList;
			
			try {
				// Use thread pool to execute tasks in parallel
				futureList = executorService.invokeAll(taskList, maxThreadRunTimeSec, TimeUnit.SECONDS);											
			} catch (Exception e) {
				scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_FINISHED,true,false);
				return;
			}
			
			for (Future<ExecutionResult> future: futureList) {
				// If any tasks timed out, set timeout flag on script log and exit
				if (future.isCancelled()) {
					scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_TIMEDOUT,true,false);
					return;
				}
				ExecutionResult executionResult = future.get();
				
				// If any tasks got error, set error flag and exit
				if (executionResult.getResultCode() == ExecutionResult.RESULT_FINISHED_ERROR) {
					scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_FINISHED,true,false);
					return;
				}
				
				// If any tasks triggered alert, set trigger flag and exit
				if (executionResult.getResultCode() == ExecutionResult.RESULT_FINISHED_TRIGGERED) {
					scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_FINISHED,false,true);
					return;
				}				
			}		
			
			scriptLogDao.logScriptEnd(scriptLog,ExecutionResult.RESULT_FINISHED,false,false);
			
		} catch (Exception e) {
			// We are only allowed to throw JobExecutionException, 
			// so we have to wrap everything in try block and catch everything	
			String msg = "Script "+scriptId+" execution failed:"+e.getMessage();
			LoggerFactory.getLogger(PoseidonJob.class).warn(msg);
			throw new JobExecutionException(msg);
		}
		
	}
	
}
