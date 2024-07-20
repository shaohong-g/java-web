package com.example.quartz;

import org.quartz.CronTrigger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QrtzTriggerHelper {


    public static CronTrigger getTrigger1(){
        CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1")
                .withSchedule(cronSchedule("0/10 * * * * ?")
                .withMisfireHandlingInstructionDoNothing())
                .build();
        return trigger;
    }

}
