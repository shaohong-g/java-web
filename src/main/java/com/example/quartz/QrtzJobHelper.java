package com.example.quartz;

import com.example.quartz.job.SimpleJob;
import com.example.util.Helper;
import org.quartz.JobDetail;

import static org.quartz.JobBuilder.newJob;

public class QrtzJobHelper {
    public static JobDetail getJob1(){
        JobDetail job = newJob(SimpleJob.class).withIdentity("job1", "group1")
                .usingJobData(SimpleJob.LAST_EXECUTION, Helper.getCurrentDateTime() )
                .requestRecovery(true)
                .storeDurably(true)
                .build();
        return job;
    }
}
