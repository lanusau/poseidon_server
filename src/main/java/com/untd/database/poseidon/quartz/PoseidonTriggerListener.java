package com.untd.database.poseidon.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.untd.database.poseidon.dao.ScriptDao;
import com.untd.database.poseidon.dao.ScriptLogDao;
import com.untd.database.poseidon.model.database.Script;


/**
 * Purpose of this listener is to capture trigger misfires.
 * See Quartz documentation for more information about trigger
 * listeners. 
 *
 */
public class PoseidonTriggerListener implements TriggerListener {
	
	@Autowired
	private ScriptLogDao scriptLogDao;
	
	@Autowired
	private ScriptDao scriptDao;

	@Override
	public String getName() {
		return "PoseidonTriggerListener";
	}

	/** 
	 * Log the fact that some script misfired
	 * @see org.quartz.TriggerListener#triggerMisfired(org.quartz.Trigger)
	 */
	@Override
	public void triggerMisfired(Trigger trigger) {
				
		TriggerKey triggerKey = trigger.getKey();
		int scriptId;
		
		// Job name in Poseidon is the same as script ID
		// Convert jobName to int
		try {
			scriptId = Integer.parseInt(triggerKey.getName());
			Script script = scriptDao.findOne(scriptId);
			scriptLogDao.logScriptMissfire(script);
			
		} catch (Exception e){
			LoggerFactory.getLogger(PoseidonTriggerListener.class).error("Failed to log script missfire for scriptId:"+triggerKey.getName()+" - "+e.getMessage());
		}
		
	}

	@Override
	public void triggerComplete(Trigger arg0, JobExecutionContext arg1,
			CompletedExecutionInstruction arg2) {
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false;
	}

}
