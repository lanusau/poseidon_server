package com.untd.database.poseidon.model;

/**
 * Stores severity and email address for particular notification
 */
public class Notification {

	private Severity severity;
	private String emailAddress;
	
	public Notification(Severity severity, String emailAddress) {	
		this.severity = severity;
		this.emailAddress = emailAddress;
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
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Set severity
	 * 
	 * @param severity The severity to set.
	 */
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	
}
