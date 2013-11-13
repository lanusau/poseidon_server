package com.untd.database.poseidon;

/**
 * Stores severity and email address for particular notification
 */
public class Notification {

	private short severity;
	private String emailAddress;
	
	/**
	 * @param paramSeverity severity level
	 * @param paramEmailAddress email address
	 */
	public Notification(short paramSeverity,String paramEmailAddress) {
		severity = paramSeverity;
		emailAddress = paramEmailAddress;
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
