package com.example.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PreDestroy;

public class QrtzSchedulerFacade {

    private Scheduler qrtzScheduler;

    public QrtzSchedulerFacade(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        this.qrtzScheduler = scheduler;
        initializeScheduler(scheduler);
    }



    private void initializeScheduler(Scheduler scheduler) throws SchedulerException {
        // Method to initialize scheduler if necessary
        // Would initialize jobs and triggers if scheduler is not in Clustered mode
        // In Clustered Mode, jobs will be saved in DataSource and will be retrieved once scheduler is up.
        scheduler.scheduleJob(QrtzJobHelper.getJob1(), QrtzTriggerHelper.getTrigger1());
    }




    @PreDestroy
    public void cleanUp() throws Exception {
        // Shutdown Scheduler when application is down
        this.getQrtzScheduler().shutdown();
    }

    // Getters and Setters
    public Scheduler getQrtzScheduler() {
        return qrtzScheduler;
    }

    public void setQrtzScheduler(Scheduler qrtzScheduler) {
        this.qrtzScheduler = qrtzScheduler;
    }
}
