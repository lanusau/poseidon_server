package com.untd.database.poseidon.data;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.untd.database.poseidon.PoseidonConfiguration;

public class Target {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.target_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Integer targetId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.target_type_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Integer targetTypeId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.server_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Integer serverId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.hostname
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String hostname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.database_name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String databaseName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.port_number
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Integer portNumber;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.monitor_username
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String monitorUsername;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.salt
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String salt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.monitor_password
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String monitorPassword;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.status_code
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private String statusCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.inactive_until
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Date inactiveUntil;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.create_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Date createSysdate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_target.update_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    private Date updateSysdate;
    
    private String urlJdbc;
    

    /** 
     * getter method of urlJdbc
     */
    public String getUrlJdbc() {
		return urlJdbc;
	}

    
	/**
	 * setter method of urlJdbc
	 */
	public void setUrlJdbc(String urlJdbc) {
		this.urlJdbc = urlJdbc;
	}
	
	/**
	 * Return connection URL
	 * @return
	 */
	public String getUrl() {
		String url = urlJdbc;
		url = url.replaceAll("%h",hostname);
		url = url.replaceAll("%p",""+portNumber);
		url = url.replaceAll("%d",databaseName);
		return url;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.target_id
     *
     * @return the value of psd_target.target_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Integer getTargetId() {
        return targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.target_id
     *
     * @param targetId the value for psd_target.target_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.target_type_id
     *
     * @return the value of psd_target.target_type_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Integer getTargetTypeId() {
        return targetTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.target_type_id
     *
     * @param targetTypeId the value for psd_target.target_type_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setTargetTypeId(Integer targetTypeId) {
        this.targetTypeId = targetTypeId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.server_id
     *
     * @return the value of psd_target.server_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Integer getServerId() {
        return serverId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.server_id
     *
     * @param serverId the value for psd_target.server_id
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.name
     *
     * @return the value of psd_target.name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.name
     *
     * @param name the value for psd_target.name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.hostname
     *
     * @return the value of psd_target.hostname
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.hostname
     *
     * @param hostname the value for psd_target.hostname
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.database_name
     *
     * @return the value of psd_target.database_name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.database_name
     *
     * @param databaseName the value for psd_target.database_name
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.port_number
     *
     * @return the value of psd_target.port_number
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Integer getPortNumber() {
        return portNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.port_number
     *
     * @param portNumber the value for psd_target.port_number
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.monitor_username
     *
     * @return the value of psd_target.monitor_username
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getMonitorUsername() {
        return monitorUsername;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.monitor_username
     *
     * @param monitorUsername the value for psd_target.monitor_username
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setMonitorUsername(String monitorUsername) {
        this.monitorUsername = monitorUsername;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.salt
     *
     * @return the value of psd_target.salt
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getSalt() {
        return salt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.salt
     *
     * @param salt the value for psd_target.salt
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.monitor_password
     *
     * @return the value of psd_target.monitor_password
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getMonitorPassword() {
        return monitorPassword;
    }
    
    /**
     * Get decrypted password
     * @return decrypted password
     * @throws DigestException
     */
    public String getDecryptedPassword() throws DigestException {
		String decryptedPassword = "";
		
		// Try to decrypt the password
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] key = md.digest(PoseidonConfiguration.getConfiguration()
					.getString("decryptionSecret").getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			byte[] iv = md.digest(salt.getBytes("UTF-8"));

			byte[] decodedBytes = Base64.decodeBase64(monitorPassword);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			decryptedPassword = new String(decryptedBytes);
		} catch (Exception e) {
			throw new DigestException("Can not decrypt password for target id:"
					+ targetId);
		}
		return decryptedPassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.monitor_password
     *
     * @param monitorPassword the value for psd_target.monitor_password
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setMonitorPassword(String monitorPassword) {
        this.monitorPassword = monitorPassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.status_code
     *
     * @return the value of psd_target.status_code
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.status_code
     *
     * @param statusCode the value for psd_target.status_code
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.inactive_until
     *
     * @return the value of psd_target.inactive_until
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Date getInactiveUntil() {
        return inactiveUntil;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.inactive_until
     *
     * @param inactiveUntil the value for psd_target.inactive_until
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setInactiveUntil(Date inactiveUntil) {
        this.inactiveUntil = inactiveUntil;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.create_sysdate
     *
     * @return the value of psd_target.create_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Date getCreateSysdate() {
        return createSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.create_sysdate
     *
     * @param createSysdate the value for psd_target.create_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setCreateSysdate(Date createSysdate) {
        this.createSysdate = createSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_target.update_sysdate
     *
     * @return the value of psd_target.update_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public Date getUpdateSysdate() {
        return updateSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_target.update_sysdate
     *
     * @param updateSysdate the value for psd_target.update_sysdate
     *
     * @mbggenerated Thu Feb 06 14:16:38 GMT-08:00 2014
     */
    public void setUpdateSysdate(Date updateSysdate) {
        this.updateSysdate = updateSysdate;
    }
}