package com.untd.database.poseidon;

/**
 * Stores severity and email address for particular notification
 */
public class Notification {

	private short severity;
	private String emailAddress;
	
	/**
	 * @param paramSeverity
	 * @param paramEmailAddress
	 */
	public Notification(short paramSeverity,String paramEmailAddress) {
		severity = paramSeverity;
		emailAddress = paramEmailAddress;
	}

	/**
	 * @return Returns the emailAddress.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress The emailAddress to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return Returns the severity.
	 */
	public short getSeverity() {
		return severity;
	}

	/**
	 * @param severity The severity to set.
	 */
	public void setSeverity(short severity) {
		this.severity = severity;
	}
	
}
