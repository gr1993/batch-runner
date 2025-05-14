package com.example.batch_runner.scheduler;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class QuartzService {

    @Autowired
    Scheduler scheduler;

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();

        //addJob(SimpleJob.class, "SimpleJob", "단순 실행 Job 입니다.", new HashMap<>(), "0/10 * * * * ?");

        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    public void addJob(Class<? extends Job> jobClass
            , String jobName
            , String jobDescription
            , Map<String, Object> jobDataMap
            , String cronExpression) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(jobClass, jobName, jobDescription, jobDataMap);
        Trigger trigger = buildCronTrigger(cronExpression, jobName + "Trigger");

        if (scheduler.checkExists(jobDetail.getKey())) {
            System.out.println("업데이트 할 Job : " + jobDetail.getKey());
            scheduler.deleteJob(jobDetail.getKey());
        } else {
            System.out.println("새로운 Job 추가 : " + jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildJobDetail(Class<? extends Job> job
            , String name
            , String desc
            , Map<String, Object> paramsMap) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(paramsMap);

        return JobBuilder.newJob(job)
                .withIdentity(name)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronTrigger(String cronExp, String triggerName) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "triggerGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}
