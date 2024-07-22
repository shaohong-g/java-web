package com.example.scan;

import com.example.quartz.AutoWiringSpringBeanJobFactory;
import com.example.quartz.QrtzSchedulerFacade;
import org.apache.commons.dbcp2.BasicDataSource;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
//@PropertySource("classpath:application.properties")
//@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class, ActiveMQAutoConfiguration.class})
public class QuartzConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    @PostConstruct
    public void init() {
        System.out.println("Quartz Configuration is initialized and ready!");
    }

    /* Autowired jobs into spring beans to make them spring-aware */
    @Bean("qrtzSpringBeanJobFactory")
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /* Configure Scheduler Factory which @return scheduler factory object
    * DataSource is sourced from parent application context while propertyFileLoc is injected with static value
    * */
    @Bean("qrtzSchedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(
            @Qualifier("dataSource") DataSource qrtzDataSource,
//            @Qualifier("dataSource") BasicDataSource qrtzDataSource,
            @Qualifier("qrtzSpringBeanJobFactory") SpringBeanJobFactory jobFactory,
            @Value("quartz.properties") String propertyFileLoc) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory( jobFactory );
        factory.setDataSource(qrtzDataSource);
        factory.setQuartzProperties( quartzProperties(propertyFileLoc) );
//        factory.setAutoStartup(false);
        factory.setOverwriteExistingJobs(true);
        return factory;
    }

    @Bean("qrtzSchedulerFacade")
    public QrtzSchedulerFacade qrtzSchedulerFacade(@Qualifier("qrtzSchedulerFactoryBean") SchedulerFactoryBean factory) throws SchedulerException, IOException {
        QrtzSchedulerFacade schedulerHandler = new QrtzSchedulerFacade(factory);
        System.out.println("Starting Scheduler threads");
        schedulerHandler.getQrtzScheduler().start();
        return schedulerHandler;
    }

//    @Bean
//    @QuartzDataSource
//    public DataSource quartzDataSource() {
//        return DataSourceBuilder.create().build().;
//    }


    public Properties quartzProperties(String propertyFileLoc) throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(propertyFileLoc));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
