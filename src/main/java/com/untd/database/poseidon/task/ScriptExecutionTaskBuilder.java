package com.untd.database.poseidon.task;

import com.untd.database.poseidon.dao.ScriptTargetLogDao;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.ScriptLog;
import com.untd.database.poseidon.model.database.Target;
import com.untd.database.poseidon.util.Alerter;
import com.untd.database.poseidon.util.PasswordDecryptor;

/**
 * Builder class to build {@link ScriptExecutionTask} instance
 *
 */
public class ScriptExecutionTaskBuilder {
	private ScriptExecutionTask scriptExecutionTask;
	
	public ScriptExecutionTaskBuilder() {
		scriptExecutionTask = new ScriptExecutionTask();
	}
	
	public ScriptExecutionTaskBuilder withScript(Script script) {
		scriptExecutionTask.setScript(script);
		return this;
	}
	
	public ScriptExecutionTaskBuilder withTarget(Target target) {
		scriptExecutionTask.setTarget(target);
		return this;
	}
	
	public ScriptExecutionTaskBuilder withPasswordDecryptor(PasswordDecryptor passwordDecryptor) {
		scriptExecutionTask.setPasswordDecryptor(passwordDecryptor);
		return this;
	}
	
	public ScriptExecutionTaskBuilder withAlerter(Alerter alerter) {
		scriptExecutionTask.setAlerter(alerter);
		return this;
	}
	
	public ScriptExecutionTaskBuilder withScriptTargetLogDao(ScriptTargetLogDao scriptTargetLogDao) {
		scriptExecutionTask.setScriptTargetLogDao(scriptTargetLogDao);
		return this;
	}
	
	public ScriptExecutionTaskBuilder withScriptLog(ScriptLog scriptLog) {
		scriptExecutionTask.setScriptLog(scriptLog);
		return this;
	}	
	
	public ScriptExecutionTask build() {
		return this.scriptExecutionTask;
	}
}
