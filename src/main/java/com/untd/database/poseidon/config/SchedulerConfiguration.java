package com.untd.database.poseidon.config;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.untd.database.poseidon.quartz.AutowiringSpringBeanJobFactory;
import com.untd.database.poseidon.quartz.PoseidonTriggerListener;
import com.untd.database.poseidon.quartz.ScriptSchedulingTask;

@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulerConfiguration {
	@Autowired
	private ApplicationContext applicationContext;	
	
	@Autowired
	private PoseidonConfiguration poseidonConfiguration;
	
	@Autowired
	private Environment environment;
	
	@Bean
	public TriggerListener poseidonTriggerListener() {
		return new PoseidonTriggerListener();
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws SchedulerException {
		SchedulerFactoryBean bean = new SchedulerFactoryBean();
		
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
	    jobFactory.setApplicationContext(applicationContext);
	    bean.setJobFactory(jobFactory);
	    bean.setGlobalTriggerListeners(poseidonTriggerListener());
	    Properties quartzProperties = new Properties();
	    quartzProperties.setProperty("org.quartz.threadPool.class", environment.getProperty("org.quartz.threadPool.class"));
	    quartzProperties.setProperty("org.quartz.threadPool.threadCount", environment.getProperty("org.quartz.threadPool.threadCount"));
	    quartzProperties.setProperty("org.quartz.jobStore.class", environment.getProperty("org.quartz.jobStore.class"));
	    quartzProperties.setProperty("org.quartz.jobStore.misfireThreshold", environment.getProperty("org.quartz.jobStore.misfireThreshold"));
	    quartzProperties.setProperty("org.quartz.scheduler.instanceName", environment.getProperty("org.quartz.scheduler.instanceName"));	    
	    bean.setQuartzProperties(quartzProperties);
		return bean;
	}
	
	@Bean
	@ConfigurationProperties
	public ScriptSchedulingTask scriptSchedulerTask() {
		return new ScriptSchedulingTask();		
	}
	
	@Bean
	public ExecutorService executorService(){
		return Executors.newFixedThreadPool(poseidonConfiguration.settings().getQueryThreads());
	}
}
