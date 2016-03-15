package com.untd.database.poseidon.config;

import org.nfunk.jep.JEP;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.untd.database.poseidon.util.Alerter;
import com.untd.database.poseidon.util.PasswordDecryptor;

@Configuration
public class PoseidonConfiguration {
	
	@Bean
	@ConfigurationProperties
	public PasswordDecryptor passwordDecryptor() {
		return new PasswordDecryptor();
	}
	
	@Bean
	public JEP expressionParser() {
		JEP brean = new org.nfunk.jep.JEP();
		brean.addStandardFunctions();
		brean.addStandardConstants();	
		return brean;
	}
	
	@Bean
	@ConfigurationProperties(prefix="mail")
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
	
	@Bean
	@ConfigurationProperties(prefix="mail")
	public Alerter alerter() {
		return new Alerter();
	}
}
