package ru.javaboys.defidog.asyncjobs.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.javaboys.defidog.asyncjobs.JavaBoysEmailSendingJob;

@Configuration
public class EmailConfiguration {

    @Bean
    JobDetail myCustomEmailSendingJob() {
        return JobBuilder.newJob()
                .ofType(JavaBoysEmailSendingJob.class)
                .storeDurably()
                .withIdentity("emailSending")
                .build();
    }

    @Bean
    Trigger myCustomEmailSendingTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(myCustomEmailSendingJob())
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0,30 * * * * ?"))
                .build();
    }

}
