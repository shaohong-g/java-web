package com.example.quartz.job;

import com.example.beans.TestBean;
import com.example.util.Helper;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SimpleJob implements InterruptableJob {
    protected AtomicBoolean stopFlag = new AtomicBoolean(false);

    // Variable key in Job Data Map
    public static final String EXCEPTION_STACK_COUNT = "EXCEPTION_STACK_COUNT";
    public static final String LAST_SUCCESS_EXECUTION = "LAST_SUCCESS_EXECUTION";
    public static final String LAST_EXECUTION = "LAST_EXECUTION";

    @Autowired
    TestBean testBean;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        stopFlag.set(true);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        int exception_stack_count = (int) data.getOrDefault(EXCEPTION_STACK_COUNT, 0);
        try {
            data.put(LAST_EXECUTION, Helper.getCurrentDateTime() );

            System.out.println("\n" +
                            "###############################################################" + "\n"  +
                            "SimpleJob keys: " + jobExecutionContext.getJobDetail().getKey() + " executing at " + new Date() + "\n" +
                            "LAST_SUCCESS_EXECUTION: " + data.getOrDefault(LAST_SUCCESS_EXECUTION, "-") + "\n" +
                            "LAST_EXECUTION: " + data.getOrDefault(LAST_EXECUTION, "-") + "\n" +
                            "EXCEPTION_STACK_COUNT: " + exception_stack_count + "\n" +
                            "TEST BEAN (AUTOWIRED): " + testBean + "\n" +
                            "###############################################################"
            );

            data.put(EXCEPTION_STACK_COUNT, exception_stack_count);
            data.put(LAST_SUCCESS_EXECUTION, Helper.getCurrentDateTime() );
        } catch (Exception err) {
            JobExecutionException e2 = new JobExecutionException(err);
            e2.setRefireImmediately(false);

            exception_stack_count ++;
            if (exception_stack_count >= 3) {
                e2.setUnscheduleAllTriggers(true);
            }

            throw e2;
        }
    }


}
