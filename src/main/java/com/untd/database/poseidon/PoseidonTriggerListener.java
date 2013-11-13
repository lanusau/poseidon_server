package com.untd.database.poseidon;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.LoggerFactory;


/**
 * Purpose of this listener is to capture trigger misfires.
 * See Quartz documentation for more information about trigger
 * listeners. 
 *
 */
public class PoseidonTriggerListener implements TriggerListener {


	public String getName() {
		return "PoseidonTriggerListener";
	}

	public void triggerFired(Trigger arg0, JobExecutionContext arg1) {		
	}

	public boolean vetoJobExecution(Trigger arg0, JobExecutionContext arg1) {
		return false;
	}

	/** 
	 * Log the fact that some script missfired
	 * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
	 */
	public void triggerMisfired(Trigger trigger) {
		
		String jobName = trigger.getJobName();
		int scriptId;
		
		// Job name in Poseidon is the same as script ID
		// Convert jobName to int
		try {
			scriptId = Integer.parseInt(jobName);
			Script script = ControlDataStore.getScript(scriptId);
			ControlDataStore.logScriptMissfire(script);
			
		} catch (Exception e){
			LoggerFactory.getLogger(PoseidonTriggerListener.class).error("Invalid jobName/ScriptID:"+jobName);
		}
		
	}

	public void triggerComplete(Trigger arg0, JobExecutionContext arg1, int arg2) {
	}

}
