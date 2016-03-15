package com.untd.database.poseidon.dao;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.ScriptLog;

@Transactional
public class ScriptLogDao extends HibernateDao<ScriptLog,Integer>{
	
	private int serverId;

	public ScriptLogDao() {
		super(ScriptLog.class);
	}
	
	/**
	 * Log script start
	 * @param script - {@link Script} to log start for
	 * @return {@link ScriptLog}
	 */
	public  ScriptLog logScriptStart(Script script){
		
		ScriptLog scriptLog = new ScriptLog();
		scriptLog.setScriptId(script.getScriptId());
		scriptLog.setServerId(serverId);
		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_NOT_FINISHED));
		scriptLog.setStartDate(new Date());
		scriptLog.setErrorStatusCode(0);
		scriptLog.setTriggerStatusCode(0);
		scriptLog.setCreateSysdate(new Date());
		scriptLog.setUpdateSysdate(new Date());
				
		return save(scriptLog);

	}
	
	/**
	 * Log end of a script execution
	 * @param script - {@link Script} to log end for
	 * @param executionResult - {@link ExecutionResult}
	 * @param errorOccured - whether error has occurred
	 * @param triggered - whether script was triggered
	 */
	public void logScriptEnd(ScriptLog scriptLog, short executionResult, boolean errorOccured, boolean triggered) {
		

		scriptLog.setStatusNumber(new Integer(executionResult));
		
		if (errorOccured) {
			scriptLog.setErrorStatusCode(1);			
		} else {
			scriptLog.setErrorStatusCode(0);
		}
		if (triggered) {
			scriptLog.setTriggerStatusCode(1);			
		} else {
			scriptLog.setTriggerStatusCode(0);
		}
		scriptLog.setUpdateSysdate(new Date());
		scriptLog.setFinishDate(new Date());
		
		update(scriptLog);
		
	}	
	
	/**
	 * Log script miss-fire.
	 * Miss-fire happens when script was supposed to run but didn't, most likely due to 
	 * lack of available Quartz threads
	 * @param script - {@link Script}
	 */
	public void logScriptMissfire(Script script)  {	
		ScriptLog scriptLog = new ScriptLog();
		scriptLog.setScriptId(script.getScriptId());
		scriptLog.setServerId(serverId);
		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_MISSFIRED));
		scriptLog.setStartDate(new Date());
		scriptLog.setErrorStatusCode(1);
		scriptLog.setTriggerStatusCode(0);
		scriptLog.setCreateSysdate(new Date());
		scriptLog.setUpdateSysdate(new Date());
		
		save(scriptLog);
	}	

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

}
