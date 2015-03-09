package com.untd.database.poseidon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Check whether CNAME is pointing to current host
 *
 */
public class CnameChecker {

	private InetAddress localHost;
	private String cname;
	private Logger logger;
	
	public void  setCname(String cname) throws CnameCheckException  {	
		logger = LoggerFactory.getLogger(CnameChecker.class);
		this.cname = cname;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new CnameCheckException(e.getMessage());
		}
		if (!check()) {
			throw new CnameCheckException("CNAME "+cname+" is not pointing to us");
		}
		
	}
	
	/**
	 * Return true if CNAME points to local host, false otherwise
	 */
	public boolean check() {
		InetAddress cnameHost;
		try {
			cnameHost = InetAddress.getByName(cname);
			return localHost.getHostAddress().equalsIgnoreCase(cnameHost.getHostAddress());
		} catch (UnknownHostException e) {
			logger.error("Get error trying to lookup CNAME address: "+e.getMessage());
			return false;
		}
			
	}
	
}
