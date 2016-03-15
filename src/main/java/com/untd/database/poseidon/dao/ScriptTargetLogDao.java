package com.untd.database.poseidon.dao;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.ExecutionResultRow;
import com.untd.database.poseidon.model.database.ScriptLog;
import com.untd.database.poseidon.model.database.ScriptTargetColLog;
import com.untd.database.poseidon.model.database.ScriptTargetLog;
import com.untd.database.poseidon.model.database.ScriptTargetRowLog;
import com.untd.database.poseidon.model.database.Target;

@Transactional
public class ScriptTargetLogDao extends HibernateDao<ScriptTargetLog,Integer>{

	public ScriptTargetLogDao() {
		super(ScriptTargetLog.class);
	}

	/**
	 * Log start of the script on particular target
	 * @param scriptLog  Script Log record that was received from {@link #logScriptStart(Script)}
	 * @param target
	 * @return ScriptTargetLog record
	 */
	public ScriptTargetLog logScriptTargetStart(ScriptLog scriptLog,Target target) {
		ScriptTargetLog scriptTargetLog = new ScriptTargetLog();
		scriptTargetLog.setScriptLogId(scriptLog.getScriptLogId());
		scriptTargetLog.setTargetId(target.getTargetId());
		scriptTargetLog.setStatusNumber(new Integer(ExecutionResult.RESULT_NOT_FINISHED));
		scriptTargetLog.setStartDate(new Date());
		scriptTargetLog.setCreateSysdate(new Date());
		scriptTargetLog.setUpdateSysdate(new Date());
		
		return save(scriptTargetLog);
	}
	
	
	/**
	 * @param scriptTargetLog - ScriptTargetLog that was received from {@link #logScriptTargetStart(ScriptLog ,Target)}
	 * @param result - Execution result
	 */
	public void logScriptTargetEnd(ScriptTargetLog scriptTargetLog, ExecutionResult result) {

		scriptTargetLog.setStatusNumber(new Integer(result.getResultCode()));
		scriptTargetLog.setErrorMessage(result.getResultErrorMsg());
		scriptTargetLog.setSeverity(result.getSeverity().getId());
		scriptTargetLog.setUpdateSysdate(new Date());

		// Dump rows
		int rowNum = 0;
		for (final ExecutionResultRow row : result.getRows()) {

			ScriptTargetRowLog rowLog = new ScriptTargetRowLog();
			rowLog.setScriptTargetLog(scriptTargetLog);
			rowLog.setRowNumber(rowNum);
			rowLog.setExpressionResult(row.getExpressionResult());
			rowLog.setExpressionErrorMessage(row.getExpressionErrorMsg());
			rowLog.setSeverity(row.getExpressionSeverity());
			rowLog.setCreateSysdate(new Date());
			rowLog.setUpdateSysdate(new Date());

			// Dump columns
			int colNum = 0;
			for (String columnValue : row.getColumns()) {
				ScriptTargetColLog colLog = new ScriptTargetColLog();

				colLog.setScriptTargetRowLog(rowLog);
				colLog.setColumnNumber(colNum);
				colLog.setColumnValue(columnValue);
				colLog.setCreateSysdate(new Date());
				colLog.setUpdateSysdate(new Date());

				rowLog.getScriptTargetColLogs().add(colLog);

				colNum++;
			}

			scriptTargetLog.getScriptTargetRowLogs().add(rowLog);
			rowNum++;
		}
		
		update(scriptTargetLog);

	}
}
