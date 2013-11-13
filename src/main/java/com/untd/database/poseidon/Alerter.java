package com.untd.database.poseidon;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static class that allows threads to alert (send emails) in synchronized fashion
 */
public class Alerter {
	
	static private Session mailSession;
	static private String fromEmailAddress;
	static private Logger logger;
	
	/**
	 * 
	 * Initialize alerter.
	 * 
	 * @param prop Configuration object, expected to contain property for "smtpHostName"
	 */
	public static void init(Configuration prop) {
		
		// Initialize logger	
		logger = LoggerFactory.getLogger(Alerter.class);
		
		String smtpHostName;
		
		if (prop.getString("smtpHostName") == null) {
			logger.warn("smtpHostName parameter not found, using default server: smtp");
		}
		
		smtpHostName = prop.getString("smtpHostName");
		Properties sessionProps = new Properties();
		sessionProps.put("mail.smtp.host",smtpHostName );
		mailSession = Session.getInstance(sessionProps, null);
		if (prop.getString("fromEmailAddress") == null) {
			logger.warn("fromEmailAddress parameter not found, using default: dba_team@localhost");
		}
		fromEmailAddress = prop.getString("fromEmailAddress", "dba_team@localhost");
	}

	/**
	 * 
	 * Send email to specified email addresses
	 * 
	 * @param emailAddresses List of email addresses
	 * @param alertMessage AlertMessage object
	 */
	synchronized public static void alert(ArrayList<String> emailAddresses,AlertMessage alertMessage) {
				
		String emailSubject = alertMessage.getMessageSubject();
		String emailText = alertMessage.getMessageText();
		
		// Make sure init() was called
		if (mailSession == null) {
			logger.error("Alerter called without being initialized first");
			return;
		}		
		
		// Try sending message
		try {			
			Message msg = new MimeMessage(mailSession);
			
			msg.setFrom(new InternetAddress(fromEmailAddress));
			InternetAddress[] addresses = new InternetAddress[emailAddresses.size()];
			
			// Loop through all addresses and add it to the list
			int index=0;
			for (final String emailAddress : emailAddresses ) {
				logger.info("Sending alert: "+emailSubject+" to "+emailAddress);
				addresses[index] = new InternetAddress(emailAddress);
				index++;
			}
			
			msg.setRecipients(Message.RecipientType.TO, addresses);
			msg.setSubject(emailSubject);
			msg.setSentDate(new Date());
			// Send either text of HTML
			switch (alertMessage.getMessageFormat()) {
				case Script.MESSAGE_FORMAT_TEXT:
					msg.setText(emailText);
					break;
				case Script.MESSAGE_FORMAT_HTML:
					msg.setContent(emailText, "text/html");
					break;
				default: 	
					logger.error("Unknown message format: "+alertMessage.getMessageFormat());
			}			
	    
			Transport.send(msg);
			
		} catch (MessagingException e) {
			logger.error("Can not send email message: "+e.getMessage());
		}
	
		
	}	
	
}
