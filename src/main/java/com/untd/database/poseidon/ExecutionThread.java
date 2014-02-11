package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.ScriptTargetLog;
import com.untd.database.poseidon.data.Target;


/**
 * This is the actual worker thread that executes particular script on the target. 
 * The {@link PoseidonJob} Quartz job fires up execution threads in parallel,
 * one for each target. 
 */
public class ExecutionThread extends Thread {

	private Script script;
	private Target target;
	private volatile boolean running;
	private volatile ExecutionResult executionResult;
	private ScriptTargetLog scriptTargetLog;
	private Executor executor;
	private Logger logger;
	
	
	/**
	 * Create execution thread to run particular script on particular target
	 * @param paramScript script to run
	 * @param paramTarget target to run script on
	 */
	public ExecutionThread(Script paramScript,Target paramTarget) {
		script = paramScript;
		target = paramTarget;
		running = false;
		executionResult = new ExecutionResult();
		executionResult.setResultCode(ExecutionResult.RESULT_NOT_FINISHED);
		executionResult.setTarget(target);
		logger = LoggerFactory.getLogger(ExecutionThread.class);		
	}
	
	/**
	 * Run this thread. The thread will connect to target and run script there. 
	 * 
	 */
	public void run() {
		running = true;
		Connection conn;
		
		conn = null;
		
		logger.debug("Executing "+script.getName()+" on "+target.getName());

		// Try connect to the database and execute SQL
		try {
			conn = DriverManager.getConnection(
					target.getUrl(),
					target.getMonitorUsername(),
					target.getDecryptedPassword());
			
			conn.setAutoCommit(true);
			
			executor = ExecutorFactory.getExecutor(script);
			executor.execute(conn,script,executionResult);
			
			conn.close();

			executionResult.setResultCode(ExecutionResult.RESULT_FINISHED);			
						
		} catch (Exception e) {
			
			try {
				if (conn != null) {
					conn.close();
				}				
			} catch (SQLException e1) {
				// Do nothing - just trying to close connection
			}
			executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_ERROR);
			executionResult.setResultErrorMsg(e.getMessage());
		}
		
		running = false;
	}	
	
	
	/**
	 * Check whether thread is running
	 * @return Returns whether thread is running
	 */
	public boolean isRunning() {
		return running;		
	}

	/**
	 * Return execution result
	 * @return Returns the executionResult.
	 */
	public ExecutionResult getExecutionResult() {
		return executionResult;
	}

	/**
	 * Return script target log id
	 * @return Returns the scriptTargetLogId.
	 */
	public ScriptTargetLog getScriptTargetLog() {
		return scriptTargetLog;
	}

	/**
	 * Set script target log id
	 * @param scriptTargetLogId The scriptTargetLogId to set.
	 */
	public void setScriptTargetLog(ScriptTargetLog scriptTargetLog) {
		this.scriptTargetLog = scriptTargetLog;
	}
	

	/**
	 * Process execution results.
	 * Results are processed using JEP expression parser. The instance to the
	 * parser is passed as a parameter. 
	 * @param expressionParser JEP expression parser
	 */
	public void process(org.nfunk.jep.JEP expressionParser) {
		AlertMessage alertMessage;	
		
		double expressionValue;
		
		// If we got error, then don't process anything
		if (executionResult.getResultCode() == ExecutionResult.RESULT_FINISHED_ERROR) {
			script.setErrorOccured(true);
			return;
		} else {
			alertMessage = new AlertMessage(script,target,executionResult);
		
			// First assume nothing got triggered, and severity low
			executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_NOT_TRIGGERED);
			executionResult.setSeverity(Script.SEVERITY_LOW);
			
			// Go through all the rows and evaluate expression			
			for (final ExecutionResultRow row : executionResult.getRows()) {													
				try {
					
					// Evaluate expression for this row
					expressionValue = evaluateExpression(executionResult,row,script.getExpressionText(),expressionParser);
					
					if (expressionValue > 0.0) {
						
						row.setExpressionResult(ExecutionResultRow.EXPRESSION_RESULT_TRUE);
						executionResult.setResultCode(ExecutionResult.RESULT_FINISHED_TRIGGERED);
						
						script.setTriggered(true);						
						
						// Calculate and set severity
						row.setExpressionSeverity(calculateSeverity(row));
						
						// If severity is higher (lower number) then current set for script - update script
						if (row.getExpressionSeverity() < script.getExecutionSeverity()) {
							script.setExecutionSeverity(row.getExpressionSeverity());
						}
						
						// Do the same for the execution result
						if (row.getExpressionSeverity() < executionResult.getSeverity()) {
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
					script.setErrorOccured(true);
					
					break;
				}															
			}
			
			// If triggered - send alert
			if (executionResult.getResultCode() == ExecutionResult.RESULT_FINISHED_TRIGGERED) {
				alertMessage.setSeverity(executionResult.getSeverity());
				raiseAlert(alertMessage,executionResult.getSeverity());
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
	private double evaluateExpression(ExecutionResult result,ExecutionResultRow row,String expressionTemplate,org.nfunk.jep.JEP expressionParser) throws ExpressionException {
		
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
	private short calculateSeverity(ExecutionResultRow row) {
		
		String severityColValue;
		
		if (script.getFixedSeverity() != Script.SEVERITY_CALCULATE) {
			return script.getFixedSeverity().shortValue();
		}
		
		if (row.getColumns().size() > script.getSeverity_column_position()) {
			severityColValue = row.getColumns().elementAt(script.getSeverity_column_position());
			return script.calculateSeverity(severityColValue);
		} else {
			return Script.SEVERITY_LOW;
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
			short severity) {		
		ArrayList<String> emailAddresses = new ArrayList<String>();
		
		// Get a list of email addresses and send alert
		for (final Notification notification : ControlDataStore.getScriptNotifications(script, severity)) {		
			emailAddresses.add(notification.getEmailAddress());
			
		}
		Alerter.alert(emailAddresses,alertMessage);		
		
	}
}
