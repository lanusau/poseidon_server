package com.untd.database.poseidon.model;

/**
 * Bean to hold various application settings from the configuration file
 *
 */
public class Settings {
	private int serverId; // Server ID
	private String decryptionSecret; // Secret used to decrypt target database password
	private int maxThreadRunTimeSec; // Maximum time script execution thread is allowed to run
	private int queryThreads; //size of the thread pool that will execute scripts on each target.
	
	/**
	 * @return the serverId
	 */
	public int getServerId() {
		return serverId;
	}
	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	/**
	 * @return the decryptionSecret
	 */
	public String getDecryptionSecret() {
		return decryptionSecret;
	}
	/**
	 * @param decryptionSecret the decryptionSecret to set
	 */
	public void setDecryptionSecret(String decryptionSecret) {
		this.decryptionSecret = decryptionSecret;
	}
	/**
	 * @return the maxThreadRunTimeSec
	 */
	public int getMaxThreadRunTimeSec() {
		return maxThreadRunTimeSec;
	}
	/**
	 * @param maxThreadRunTimeSec the maxThreadRunTimeSec to set
	 */
	public void setMaxThreadRunTimeSec(int maxThreadRunTimeSec) {
		this.maxThreadRunTimeSec = maxThreadRunTimeSec;
	}
	
	public int getQueryThreads() {
		return queryThreads;
	}
	public void setQueryThreads(int queryThreads) {
		this.queryThreads = queryThreads;
	}
}
