package com.untd.database.poseidon;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Object with information about target
 */
public class Target {
	
	private String name,hostname,database_name;
	private int target_id,port_number;
	private String monitor_username,monitor_password,salt,status_code;
	private int server_id;
	private String url;
	
	private boolean errorOccured;
	
	/**
	 * Construct target by querying control database
	 * @param conn established connection to control database
	 * @param targetId target id
	 * @throws SQLException
	 */
	public Target (Connection conn, int targetId) throws SQLException {
		PreparedStatement st;
		ResultSet rs;
		String url_jdbc;
		
		
		// Query target information from control database
		st = conn.prepareStatement(SqlText.getString("Target.getTargetSQL"));
		
		st.setInt(1,targetId);
		rs = st.executeQuery();
		
		if (! rs.next()) {			
			throw new SQLException("Can not find target id:"+targetId);
		}
		
		// Set variables based on column names
		name = rs.getString("name");
		hostname = rs.getString("hostname");
		database_name = rs.getString("database_name");
		monitor_username = rs.getString("monitor_username");
		monitor_password = rs.getString("monitor_password");
		salt = rs.getString("salt");
		status_code = rs.getString("status_code");
		port_number = rs.getInt("port_number");
		target_id = rs.getInt("target_id");
		server_id = rs.getInt("server_id");
		url_jdbc = rs.getString("url_jdbc");
		
		// Try to decrypt the password
		try {						
			MessageDigest md = MessageDigest.getInstance("MD5");				
			byte[] key = md.digest(PoseidonConfiguration.getConfiguration().getString("decryptionSecret").getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			byte[] iv = md.digest(salt.getBytes("UTF-8"));
			
			byte[] decodedBytes = Base64.decodeBase64(monitor_password);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec,new IvParameterSpec(iv));
			byte[] decryptedBytes =
			         cipher.doFinal(decodedBytes);
			monitor_password = new String(decryptedBytes);	
		} catch (Exception e) {
			throw new SQLException("Can not decrypt password for target id:"+targetId);
		}
		
		url = url_jdbc.replaceAll("%h",hostname);
		url = url.replaceAll("%p",""+port_number);
		url = url.replaceAll("%d",database_name);
		
		rs.close();
		st.close();
		
		
	}

	/**
	 * @return Returns the database_name.
	 */
	public String getDatabase_name() {
		return database_name;
	}

	/**
	 * @return Returns the hostname.
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return Returns the monitor_password.
	 */
	public String getMonitor_password() {
		return monitor_password;
	}

	/**
	 * @return Returns the monitor_username.
	 */
	public String getMonitor_username() {
		return monitor_username;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the port_number.
	 */
	public int getPort_number() {
		return port_number;
	}

	/**
	 * @return Returns the status_code.
	 */
	public String getStatus_code() {
		return status_code;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return Returns the target_id.
	 */
	public int getTarget_id() {
		return target_id;
	}
	
	/**
	 * @return Returns whether error has occurred
	 */
	public boolean hasErrorOccured() {
		return errorOccured;
	}

	/**
	 * Set flag whether error has occurred
	 * @param errorOccured - flag
	 */
	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}

	/**
	 * @return the server_id
	 */
	public int getServer_id() {
		return server_id;
	}

}
