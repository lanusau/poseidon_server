package com.untd.database.poseidon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.untd.database.poseidon.model.Settings;
import com.untd.database.poseidon.util.Alerter;
import com.untd.database.poseidon.util.PasswordDecryptor;

@Configuration
public class PoseidonConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix="poseidon")
	public Settings settings() {
		return new Settings();
	}
	
	@Bean	
	public PasswordDecryptor passwordDecryptor() {
		return new PasswordDecryptor();
	}
	
	@Bean
	@ConfigurationProperties(prefix="mailer")
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
	
	@Bean
	@ConfigurationProperties(prefix="alerter")
	public Alerter alerter() {
		return new Alerter();
	}
}
