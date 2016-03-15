package com.untd.database.poseidon.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.nfunk.jep.JEP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.untd.database.poseidon.dao.ScriptTargetLogDao;
import com.untd.database.poseidon.exception.ExpressionException;
import com.untd.database.poseidon.model.AlertMessage;
import com.untd.database.poseidon.model.ExecutionResult;
import com.untd.database.poseidon.model.ExecutionResultRow;
import com.untd.database.poseidon.model.Notification;
import com.untd.database.poseidon.model.Severity;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.ScriptLog;
import com.untd.database.poseidon.model.database.ScriptTargetLog;
import com.untd.database.poseidon.model.database.Target;
import com.untd.database.poseidon.util.Alerter;
import com.untd.database.poseidon.util.JdbcUtil;
import com.untd.database.poseidon.util.PasswordDecryptor;

/**
 * Task that executes {@link Script} on particular {@link Target}
 *
 */
public class ScriptExecutionTask {
	
	private Script script;
	private Target target;
	private PasswordDecryptor passwordDecryptor;
	private Alerter alerter;
	private ScriptTargetLogDao scriptTargetLogDao;
	private ScriptLog scriptLog;

	public ExecutionResult execute() {
		
		Assert.notNull(script);
		Assert.notNull(target);
		Assert.notNull(passwordDecryptor);
		Assert.notNull(alerter);
		Assert.notNull(scriptTargetLogDao);
		Assert.notNull(scriptLog);

		final Logger logger = LoggerFactory.getLogger(ScriptExecutionTask.class);
		logger.debug("Executing " + script.getName() + " on " + target.getName());

		ScriptTargetLog scriptTargetLog = scriptTargetLogDao.logScriptTargetStart(scriptLog, target);

		ExecutionResult executionResult = new ExecutionResult();
		executionResult.setResultCode(ExecutionResult.RESULT_NOT_FINISHED);
		Connection connection = null;

		// Try connect to the database and execute SQL
		try {
			connection = DriverManager.getConnection(
					target.getUrl(), 
					target.getMonitorUsername(),
					passwordDecryptor.getDecryptedPassword(target));

			connection.setAutoCommit(true);

			final DatabaseQuery databaseTask = DatabaseQueryFactory.getDatabaseTask(script);
			executionResult.setRows(databaseTask.execute(connection, script));
			
			// First assume nothing got triggered, and severity low
			executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_NOT_TRIGGERED);
			executionResult.setSeverity(Severity.LOW);
			final AlertMessage alertMessage = new AlertMessage(script,target,executionResult);
			evaluateExecutionResult(executionResult,alertMessage);
			
			// If triggered - send alert
			if (executionResult.getResultCode() == ExecutionResult.RESULT_FINISHED_TRIGGERED) {
				alertMessage.setSeverity(executionResult.getSeverity());
				raiseAlert(alertMessage,executionResult.getSeverity());
			}

		} catch (Exception e) {

			executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_ERROR);
			executionResult.setResultErrorMsg(e.getMessage());

		} finally {
			JdbcUtil.closeConnectionHandle(connection);
		}
		
		scriptTargetLogDao.logScriptTargetEnd(scriptTargetLog, executionResult);

		return executionResult;
	}
	
	private void evaluateExecutionResult(final ExecutionResult executionResult,final AlertMessage alertMessage) {
		final JEP expressionParser = new JEP();
		
		// Go through all the rows and evaluate expression			
		double expressionValue;
		for (final ExecutionResultRow row : executionResult.getRows()) {
			try {
				
				// Evaluate expression for this row
				expressionValue = evaluateExpression(executionResult,row,script.getExpressionText(),expressionParser);
				
				if (expressionValue > 0.0) {
					
					row.setExpressionResult(ExecutionResultRow.EXPRESSION_RESULT_TRUE);
					executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_TRIGGERED);					
					
					// Calculate and set severity
					row.setExpressionSeverity(calculateSeverity(row));
					
					// Do the same for the execution result
					if (row.getExpressionSeverity().getId() < executionResult.getSeverity().getId()) {
						executionResult.setSeverity(row.getExpressionSeverity());
					}
					
					alertMessage.addRowToMessage(row);										
					
				} else {
					row.setExpressionResult(ExecutionResultRow.EXPRESSION_RESULT_FALSE);
				}
			} catch (ExpressionException e){
				
				// We got error from evaluating expression - update flags 
				// and get out of here
				executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_ERROR);
				executionResult.setResultErrorMsg("Expression evaluation failed for one or more rows");
				
				row.setExpressionResult(ExecutionResultRow.EXPRESSION_RESULT_ERROR);
				row.setExpressionErrorMsg(e.getMessage());
				
				break;
			}					
		}
	}
	
	/**
	 * Build and evaluate expression for execution result row.
	 * Expression is built by replacing %n tags in the expression template
	 * with actual values from the columns in the row
	 * The variable %rc is replaced with number of rows in the resultset
	 * 
	 * @param result execution result
	 * @param row execution result row
	 * @param expressionTemplate expression template from for this script
	 * @param expressionParser JEP expression parser
	 * @return float value, with value > 0.0 meaning true, other values meaning false
	 * @throws ExpressionException
	 */
	private double evaluateExpression(ExecutionResult result,ExecutionResultRow row,String expressionTemplate,JEP expressionParser) throws ExpressionException {
		
		String expressionText;
		double expressionValue;
		
		expressionText = expressionTemplate;
		
		// Replace %rc with number of rows
		expressionText = expressionText.replaceAll("%rc",""+result.getRows().size());
		
		// Replace each %x, where x is column position, with column value
		for (int i=0;i<row.getColumns().size();i++) {
			expressionText = expressionText.replaceAll("%"+i,row.getColumns().get(i));
		}
		expressionParser.parseExpression(expressionText);
		
		if (expressionParser.hasError()) {
			throw new ExpressionException(expressionParser.getErrorInfo());
			
		}
		expressionValue = expressionParser.getValue();
		if (expressionParser.hasError()) {
			throw new ExpressionException(expressionParser.getErrorInfo());
		}
		return expressionValue;
	}
	
	/**
	 * 
	 * Calculate severity for a particular result row
	 * 
	 * @param row
	 * @return calculated severity
	 */
	private Severity calculateSeverity(ExecutionResultRow row) {
		
		String severityColValue;
		
		if (script.getFixedSeverity() != Severity.CALCULATE) {
			return script.getFixedSeverity();
		}
		
		if (row.getColumns().size() > script.getSeverityColumnPosition()) {
			severityColValue = row.getColumns().get(script.getSeverityColumnPosition());
			return script.calculateSeverity(severityColValue);
		} else {
			return Severity.LOW;
		}		
	 
	}
	
	/**
	 * 
	 * Raise alert to the applicable groups
	 * 
	 * @param messageSubject message subject
	 * @param messageHeader  message header
	 * @param messageText message text
	 * @param severity severity
	 */
	private void raiseAlert(AlertMessage alertMessage,
			Severity severity) {		
		ArrayList<String> emailAddresses = new ArrayList<String>();
		
		// Get a list of email addresses and send alert
		for (final Notification notification : script.getScriptNotificationsForSeverity(severity)) {		
			emailAddresses.add(notification.getEmailAddress());
			
		}
		alerter.alert(emailAddresses,alertMessage);		
	
	}

	/**
	 * @return the script
	 */
	public Script getScript() {
		return script;
	}

	/**
	 * @param script the script to set
	 */
	public void setScript(Script script) {
		this.script = script;
	}

	/**
	 * @return the target
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * @return the passwordDecryptor
	 */
	public PasswordDecryptor getPasswordDecryptor() {
		return passwordDecryptor;
	}

	/**
	 * @param passwordDecryptor the passwordDecryptor to set
	 */
	public void setPasswordDecryptor(PasswordDecryptor passwordDecryptor) {
		this.passwordDecryptor = passwordDecryptor;
	}

	/**
	 * @return the alerter
	 */
	public Alerter getAlerter() {
		return alerter;
	}

	/**
	 * @param alerter the alerter to set
	 */
	public void setAlerter(Alerter alerter) {
		this.alerter = alerter;
	}

	/**
	 * @return the scriptTargetLogDao
	 */
	public ScriptTargetLogDao getScriptTargetLogDao() {
		return scriptTargetLogDao;
	}

	/**
	 * @param scriptTargetLogDao the scriptTargetLogDao to set
	 */
	public void setScriptTargetLogDao(ScriptTargetLogDao scriptTargetLogDao) {
		this.scriptTargetLogDao = scriptTargetLogDao;
	}

	/**
	 * @return the scriptLog
	 */
	public ScriptLog getScriptLog() {
		return scriptLog;
	}

	/**
	 * @param scriptLog the scriptLog to set
	 */
	public void setScriptLog(ScriptLog scriptLog) {
		this.scriptLog = scriptLog;
	}

}
