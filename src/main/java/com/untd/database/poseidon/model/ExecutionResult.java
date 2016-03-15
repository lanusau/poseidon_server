package com.untd.database.poseidon.model;

import java.util.List;


/**
 * Class to store various states of script execution result.
 * After each execution, script can have several types of results,
 * like finished with error, triggered etc. This class also stores 
 * list of {@link ExecutionResultRow} objects which represent 
 * execution results for each row that script returned on the target
 *
 */
public class ExecutionResult {
	
	// Constants for execution result codes
	/**
	 * Script is still running
	 */
	public static short RESULT_NOT_FINISHED = 0;
	/**
	 * Script finished with error
	 */
	public static short RESULT_FINISHED_ERROR = 1;
	/**
	 * Script finished executing, but expression is not evaluated yet
	 */
	public static short RESULT_FINISHED = 2;
	/**
	 * Script finished and it was triggered
	 */
	public static short RESULT_FINISHED_TRIGGERED = 3;
	/**
	 * Script finished and it was not triggered
	 */
	public static short RESULT_FINISHED_NOT_TRIGGERED = 4;
	/**
	 * Script missfired
	 */
	public static short RESULT_MISSFIRED = 5;
	/**
	 * Script timed out
	 */
	public static short RESULT_TIMEDOUT = 6;
	
	private List<ExecutionResultRow> rows;
	private short resultCode;
	private Severity severity;
	private String resultErrorMsg;
	
	/**
	 * Return result code
	 * @return Returns the resultCode.
	 */
	public short getResultCode() {
		return resultCode;
	}
	/**
	 * Set result code
	 * @param resultCode The resultCode to set.
	 */
	public void setResultCode(short resultCode) {
		this.resultCode = resultCode;
	}
	
	/**
	 * Add new execution result row
	 * @param row execution result row
	 */
	public void add(ExecutionResultRow row) {
		rows.add(row);
	}
	/**
	 * Get result error message
	 * @return Returns the resultErrorMsg.
	 */
	public String getResultErrorMsg() {
		return resultErrorMsg;
	}
	/**
	 * Set result error message
	 * @param resultErrorMsg The resultErrorMsg to set.
	 */
	public void setResultErrorMsg(String resultErrorMsg) {
		this.resultErrorMsg = resultErrorMsg;
	}

	/**
	 * Get severity
	 * @return severity value
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Set severity
	 * @param severity severity value
	 * @see Script for severity definitions
	 */
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	/**
	 * @return the rows
	 */
	public List<ExecutionResultRow> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<ExecutionResultRow> rows) {
		this.rows = rows;
	}

}
