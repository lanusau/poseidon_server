package com.untd.database.poseidon.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains rows returned by SQL statements, together with 
 * expression results
 *
 */

public class ExecutionResultRow {
	/**
	 * Script expression evaluated to TRUE for this row
	 */
	public static short EXPRESSION_RESULT_TRUE = 1;
	/**
	 * Script expression evaluated to FALSE for this row
	 */
	public static short EXPRESSION_RESULT_FALSE = 0;
	/**
	 * Script expression got error
	 */
	public static short EXPRESSION_RESULT_ERROR = 2;
	
	private List<String> columns;
	private short expressionResult = EXPRESSION_RESULT_FALSE;
	private Severity expressionSeverity = Severity.LOW;
	private String expressionErrorMsg;
	
	/**
	 * Build execution result row object from JDBC {@link ResultSet}
	 * @param  - rs {@link ResultSet}
	 * @throws SQLException
	 */
	public ExecutionResultRow(ResultSet rs) throws SQLException {			
		
		// Get all the columns as strings
		columns = new ArrayList<String>();
		for (int i=1; i<= rs.getMetaData().getColumnCount();i++) {
			if (rs.getString(i) != null ) {
				columns.add(rs.getString(i));	
			} else {
				columns.add("");
			}
			
		}
	}

	/**
	 * Returns error that was received evaluating script expression for this row
	 * @return Returns the expressionErrorMsg.
	 */
	public String getExpressionErrorMsg() {
		return expressionErrorMsg;
	}

	/**
	 * Sets the error was received evaluating script expression for this row
	 * @param expressionErrorMsg The expressionErrorMsg to set.
	 */
	public void setExpressionErrorMsg(String expressionErrorMsg) {
		this.expressionErrorMsg = expressionErrorMsg;
	}

	/**
	 * Returns expression result
	 * @return Returns the expressionResult.
	 */
	public short getExpressionResult() {
		return expressionResult;
	}

	/**
	 * Sets expression result
	 * @param expressionResult The expressionResult to set.
	 */
	public void setExpressionResult(short expressionResult) {
		this.expressionResult = expressionResult;
	}

	/**
	 * Return all column values as strings
	 * @return Returns the column values
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * Set column values
	 * @param columns The list of strings to set columns to
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * Get expression severity
	 * @return Returns the expressionSeverity.
	 */
	public Severity getExpressionSeverity() {
		return expressionSeverity;
	}

	/**
	 * Set expression severity
	 * @param expressionSeverity The expressionSeverity to set.
	 */
	public void setExpressionSeverity(Severity expressionSeverity) {
		this.expressionSeverity = expressionSeverity;
	}
}
