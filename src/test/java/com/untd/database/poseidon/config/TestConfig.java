package com.untd.database.poseidon.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import com.untd.database.poseidon.PoseidonApplication;

@Configuration
@ActiveProfiles("test")
@Profile("test")
@SpringApplicationConfiguration(classes = {PoseidonApplication.class})
public class TestConfig{
	
	@Autowired
	private DataSource datasource;

	@Bean
	public TestDataInitialiser testDataInitialiser() {
		TestDataInitialiser bean = new TestDataInitialiser();
		bean.setDataSource(datasource);
		bean.setTestDataResource(new ClassPathResource("testData.xml"));
		return bean;
	}

}
