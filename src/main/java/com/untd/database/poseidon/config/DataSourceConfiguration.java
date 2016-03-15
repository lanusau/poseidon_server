package com.untd.database.poseidon.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.untd.database.poseidon.dao.ScriptDao;
import com.untd.database.poseidon.dao.ScriptLogDao;
import com.untd.database.poseidon.dao.ScriptTargetLogDao;
import com.untd.database.poseidon.dao.TargetDao;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
public class DataSourceConfiguration {
	
	@Value("${serverId}")
	private int serverId;
	
	@Bean
	@ConfigurationProperties(prefix="datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPackagesToScan("com.untd.database.poseidon.model.database");
		Properties p = new Properties();
		p.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		p.setProperty("hibernate.show_sql", "true");
		p.setProperty("hibernate.format_sql", "true");
		bean.setHibernateProperties(p);
		return bean;
	}
	
	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager bean = new HibernateTransactionManager();
		bean.setSessionFactory(sessionFactory().getObject());
		return bean;
	}
	
	@Bean
	public ScriptDao scriptDao() {
		ScriptDao bean = new ScriptDao();
		bean.setSessionFactory(sessionFactory().getObject());
		return bean;
	}
	
	@Bean
	public ScriptLogDao scriptLogDao() {
		ScriptLogDao bean = new ScriptLogDao();
		bean.setSessionFactory(sessionFactory().getObject());
		bean.setServerId(serverId);
		return bean;
	}
	
	@Bean
	public ScriptTargetLogDao scriptTargetLogDao() {
		ScriptTargetLogDao bean = new ScriptTargetLogDao();
		bean.setSessionFactory(sessionFactory().getObject());
		return bean;
	}
	
	@Bean
	public TargetDao targetDao() {
		TargetDao bean = new TargetDao();
		bean.setSessionFactory(sessionFactory().getObject());
		return bean;
	}	
}
