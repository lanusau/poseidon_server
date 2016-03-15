package com.untd.database.poseidon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.untd.database.poseidon.util.CnameChecker;

@Configuration
@EnableScheduling
@Profile("cnameCheck")
public class CnameCheckConfiguration {

	@Bean
	@ConfigurationProperties
	public CnameChecker cnameChecker() {
		return new CnameChecker();
	}
}
