package com.untd.database.poseidon.model.database;
// Generated Dec 2, 2015 11:41:41 AM by Hibernate Tools 4.3.1.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.untd.database.poseidon.model.Severity;

/**
 * ScriptTargetRowLog generated by hbm2java
 */
@Entity
@Table(name = "psd_script_target_row_log")
public class ScriptTargetRowLog implements java.io.Serializable {

	private static final long serialVersionUID = 5359103353763278421L;
	
	private Integer scriptTargetRowLogId;
	private ScriptTargetLog scriptTargetLog;
	private int rowNumber;
	private int expressionResult;
	private String expressionErrorMessage;
	private Severity severity;
	private Date createSysdate;
	private Date updateSysdate;
	private Set<ScriptTargetColLog> scriptTargetColLogs = new HashSet<ScriptTargetColLog>();

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "script_target_row_log_id", unique = true, nullable = false)
	public Integer getScriptTargetRowLogId() {
		return this.scriptTargetRowLogId;
	}

	public void setScriptTargetRowLogId(Integer scriptTargetRowLogId) {
		this.scriptTargetRowLogId = scriptTargetRowLogId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "script_target_log_id", nullable = false)
	public ScriptTargetLog getScriptTargetLog() {
		return scriptTargetLog;
	}

	public void setScriptTargetLog(ScriptTargetLog scriptTargetLog) {
		this.scriptTargetLog = scriptTargetLog;
	}

	@Column(name = "row_number", nullable = false)
	public int getRowNumber() {
		return this.rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	@Column(name = "expression_result", nullable = false)
	public int getExpressionResult() {
		return this.expressionResult;
	}

	public void setExpressionResult(int expressionResult) {
		this.expressionResult = expressionResult;
	}

	@Column(name = "expression_error_message", length = 65535)
	public String getExpressionErrorMessage() {
		return this.expressionErrorMessage;
	}

	public void setExpressionErrorMessage(String expressionErrorMessage) {
		this.expressionErrorMessage = expressionErrorMessage;
	}

	@Column(name = "severity", nullable = false)
	@Type(type = "com.untd.database.poseidon.model.database.types.SeverityUserType")
	public Severity getSeverity() {
		return this.severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_sysdate", nullable = false, length = 19)
	public Date getCreateSysdate() {
		return this.createSysdate;
	}

	public void setCreateSysdate(Date createSysdate) {
		this.createSysdate = createSysdate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_sysdate", nullable = false, length = 19)
	public Date getUpdateSysdate() {
		return this.updateSysdate;
	}

	public void setUpdateSysdate(Date updateSysdate) {
		this.updateSysdate = updateSysdate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scriptTargetRowLog",cascade={CascadeType.ALL})
	public Set<ScriptTargetColLog> getScriptTargetColLogs() {
		return scriptTargetColLogs;
	}

	public void setScriptTargetColLogs(Set<ScriptTargetColLog> scriptTargetColLogs) {
		this.scriptTargetColLogs = scriptTargetColLogs;
	}

}
