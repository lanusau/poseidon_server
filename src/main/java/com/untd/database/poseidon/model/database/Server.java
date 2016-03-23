package com.untd.database.poseidon.model.database;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "psd_server", uniqueConstraints = @UniqueConstraint(columnNames = "name") )
public class Server {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "server_id", unique = true, nullable = false)
	private Integer serverId;
	
	@Column(name = "name", unique = true, nullable = false, length = 200)
	private String name;
	
	@Column(name = "location", unique = true, nullable = false, length = 200)
	private String location;
	
	@Column(name = "status_code", unique = true, nullable = false, length = 1)
	private String statusCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_sysdate", nullable = false, length = 19)
	private Date createSysdate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_sysdate", nullable = false, length = 19)
	private Date updateSysdate;

	/**
	 * @return the serverId
	 */
	public Integer getServerId() {
		return serverId;
	}

	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the createSysdate
	 */
	public Date getCreateSysdate() {
		return createSysdate;
	}

	/**
	 * @param createSysdate the createSysdate to set
	 */
	public void setCreateSysdate(Date createSysdate) {
		this.createSysdate = createSysdate;
	}

	/**
	 * @return the updateSysdate
	 */
	public Date getUpdateSysdate() {
		return updateSysdate;
	}

	/**
	 * @param updateSysdate the updateSysdate to set
	 */
	public void setUpdateSysdate(Date updateSysdate) {
		this.updateSysdate = updateSysdate;
	}
	
	
}
