package com.untd.database.poseidon.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;



/**
 * Check whether CNAME is pointing to current host
 *
 */
public class CnameChecker implements InitializingBean{
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private String cname;
	
	private InetAddress localHost;	
	private Logger logger;	
	
	private class ErrorExitCodeGenerator implements ExitCodeGenerator {

		@Override
		public int getExitCode() {
			return 1;
		}
		
	}
	
	/**
	 * Check if CNAME points to local host
	 */
	@Scheduled(fixedDelay=60000)
	public void checkCname() {
		InetAddress cnameHost;
		try {
			cnameHost = InetAddress.getByName(cname);
			if (localHost.getHostAddress().equalsIgnoreCase(cnameHost.getHostAddress())) {
				logger.warn("Exiting because CNAME no longer points to local host");
				SpringApplication.exit(applicationContext, new ErrorExitCodeGenerator());
			}
		} catch (UnknownHostException e) {
			logger.error("Get error trying to lookup CNAME address: "+e.getMessage());
			return;
		}
			
	}

	@Override
	public void afterPropertiesSet() throws UnknownHostException {
		logger = LoggerFactory.getLogger(CnameChecker.class);
		localHost = InetAddress.getLocalHost();
	}

	/**
	 * @return the cname
	 */
	public String getCname() {
		return cname;
	}

	/**
	 * @param cname the cname to set
	 */
	public void setCname(String cname) {
		this.cname = cname;
	}
	
}
