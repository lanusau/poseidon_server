package com.untd.database.poseidon.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.untd.database.poseidon.quartz.AutowiringSpringBeanJobFactory;
import com.untd.database.poseidon.quartz.PoseidonTriggerListener;
import com.untd.database.poseidon.quartz.ScriptSchedulingTask;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {
	@Autowired
	private ApplicationContext applicationContext;	
	
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
		return bean;
	}
	
	@Bean
	@ConfigurationProperties
	public ScriptSchedulingTask scriptSchedulerTask() {
		return new ScriptSchedulingTask();		
	}
	
	@Bean
	public ExecutorService executorService(){
		return Executors.newFixedThreadPool(10);
	}
}
