package com.untd.database.poseidon.util;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.untd.database.poseidon.model.AlertMessage;

/**
 * Class that allows threads to alert (send emails) 
 */
public class Alerter {	
	
	@Autowired
	private JavaMailSender sender;
		
	private String fromEmailAddress;

	/**
	 * 
	 * Send email to specified email addresses
	 * 
	 * @param emailAddresses List of email addresses
	 * @param alertMessage AlertMessage object
	 */
	public synchronized void alert(ArrayList<String> emailAddresses, AlertMessage alertMessage) {

		String emailText = alertMessage.getMessageText();

		Logger logger = LoggerFactory.getLogger(Alerter.class);

		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(fromEmailAddress);
			helper.setTo(emailAddresses.toArray(new String[emailAddresses.size()]));
			helper.setSubject(alertMessage.getMessageSubject());

			switch (alertMessage.getMessageFormat()) {
			case TEXT:
				helper.setText(emailText);
				break;
			case HTML:
				helper.setText(emailText, true);
				break;
			default:
				logger.error("Unknown message format: " + alertMessage.getMessageFormat());
			}

			sender.send(message);
		} catch (MessagingException e) {
			logger.error("Cannot send email message:" + e.getMessage());
		}

	}

	/**
	 * @return the fromEmailAddress
	 */
	public String getFromEmailAddress() {
		return fromEmailAddress;
	}

	/**
	 * @param fromEmailAddress the fromEmailAddress to set
	 */
	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}	
	
}
