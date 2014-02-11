package com.untd.database.poseidon;

/**
 * Stores severity and email address for particular notification
 */
public class Notification {

	private Integer notifyGroupEmailId;
	private short severity;
	private String emailAddress;
	

	/**
	 * Getter method for notifyGroupEmailId
	 * 
	 * @return notifyGroupEmailId
	 */
	public Integer getNotifyGroupEmailId() {
		return notifyGroupEmailId;
	}

	/**
	 * Setter method for notifyGroupEmailId
	 * @param notifyGroupEmailId
	 */
	public void setNotifyGroupEmailId(Integer notifyGroupEmailId) {
		this.notifyGroupEmailId = notifyGroupEmailId;
	}

	/**
	 * Get email address
	 * 
	 * @return email address.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Set email address
	 * 
	 * @param emailAddress The emailAddress to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Get severity
	 * 
	 * @return severity.
	 */
	public short getSeverity() {
		return severity;
	}

	/**
	 * Set severity
	 * 
	 * @param severity The severity to set.
	 */
	public void setSeverity(short severity) {
		this.severity = severity;
	}
	
}
