package ru.javaboys.defidog.asyncjobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import io.jmix.email.Emailer;

public class JavaBoysEmailSendingJob implements Job {

    @Autowired
    private Emailer emailer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        emailer.processQueuedEmails();
    }

}