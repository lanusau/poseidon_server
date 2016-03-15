package com.untd.database.poseidon.model.database;
// Generated Dec 2, 2015 11:41:41 AM by Hibernate Tools 4.3.1.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * ScriptTarget generated by hbm2java
 */
@Entity
@Table(name = "psd_script_target", uniqueConstraints = @UniqueConstraint(columnNames = {
		"target_id", "script_id" }) )
public class ScriptTarget implements java.io.Serializable {

	private static final long serialVersionUID = 3866818975218200394L;
	
	private Integer scriptTargetId;
	private int targetId;
	private int scriptId;
	private Date createSysdate;
	private Date updateSysdate;

	public ScriptTarget() {
	}

	public ScriptTarget(int targetId, int scriptId, Date createSysdate, Date updateSysdate) {
		this.targetId = targetId;
		this.scriptId = scriptId;
		this.createSysdate = createSysdate;
		this.updateSysdate = updateSysdate;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "script_target_id", unique = true, nullable = false)
	public Integer getScriptTargetId() {
		return this.scriptTargetId;
	}

	public void setScriptTargetId(Integer scriptTargetId) {
		this.scriptTargetId = scriptTargetId;
	}

	@Column(name = "target_id", nullable = false)
	public int getTargetId() {
		return this.targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	@Column(name = "script_id", nullable = false)
	public int getScriptId() {
		return this.scriptId;
	}

	public void setScriptId(int scriptId) {
		this.scriptId = scriptId;
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

}
