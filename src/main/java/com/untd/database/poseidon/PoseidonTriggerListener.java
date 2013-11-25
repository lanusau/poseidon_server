package com.untd.database.poseidon;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
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
	 * Log the fact that some script misfired
	 * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
	 */
	public void triggerMisfired(Trigger trigger) {
				
		TriggerKey triggerKey = trigger.getKey();
		int scriptId;
		
		// Job name in Poseidon is the same as script ID
		// Convert jobName to int
		try {
			scriptId = Integer.parseInt(triggerKey.getName());
			Script script = ControlDataStore.getScript(scriptId);
			ControlDataStore.logScriptMissfire(script);
			
		} catch (Exception e){
			LoggerFactory.getLogger(PoseidonTriggerListener.class).error("Invalid jobName/ScriptID:"+triggerKey.getName());
		}
		
	}

	@Override
	public void triggerComplete(Trigger arg0, JobExecutionContext arg1,
			CompletedExecutionInstruction arg2) {
		// TODO Auto-generated method stub
		
	}

}
