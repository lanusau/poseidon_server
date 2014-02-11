package com.untd.database.poseidon.data;

public class ScriptTargetRowLog {
	

	private Integer scriptTargetRowLogId;
	private Integer scriptTargetLogId;
	private Integer rowNumber;
	private Short expressionResult;
	private String expressionErrorMessage;
	private Short severity;
	
	public Integer getScriptTargetRowLogId() {
		return scriptTargetRowLogId;
	}
	public void setScriptTargetRowLogId(Integer scriptTargetRowLogId) {
		this.scriptTargetRowLogId = scriptTargetRowLogId;
	}	
	public Integer getScriptTargetLogId() {
		return scriptTargetLogId;
	}
	public void setScriptTargetLogId(Integer scriptTargetLogId) {
		this.scriptTargetLogId = scriptTargetLogId;
	}
	public Integer getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}
	public Short getExpressionResult() {
		return expressionResult;
	}
	public void setExpressionResult(Short expressionResult) {
		this.expressionResult = expressionResult;
	}
	public String getExpressionErrorMessage() {
		return expressionErrorMessage;
	}
	public void setExpressionErrorMessage(String expressionErrorMessage) {
		this.expressionErrorMessage = expressionErrorMessage;
	}
	public Short getSeverity() {
		return severity;
	}
	public void setSeverity(Short severity) {
		this.severity = severity;
	}


}
