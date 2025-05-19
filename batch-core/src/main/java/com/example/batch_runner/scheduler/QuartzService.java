package com.example.batch_runner.scheduler;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.job.quartz.BatchLaunchingJob;
import com.example.batch_runner.repository.SchedulerJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class QuartzService {

    @Autowired
    private SchedulerJobRepository jobRepository;

    @Autowired
    private Scheduler scheduler;


    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.clear();

        List<SchedulerJob> jobList = jobRepository.findByUseYn("Y");
        for (SchedulerJob schedulerJob : jobList) {
            try {
                Map<String, Object> jobParams = schedulerJob.getJobParamsJson();
                if (jobParams == null) {
                    jobParams = new HashMap<>();
                }

                addJob(
                    BatchLaunchingJob.class,
                    schedulerJob.getJobName(),
                    schedulerJob.getDescription(),
                    jobParams,
                    schedulerJob.getCronExpression()
                );
            } catch (Exception ex) {
                log.error("Job 등록 실패: " + schedulerJob.getJobName(), ex);
            }
        }

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
            log.info("업데이트 할 Job : {}", jobDetail.getKey());
            scheduler.deleteJob(jobDetail.getKey());
        } else {
            log.info("새로운 Job 추가 : {}", jobDetail.getKey());
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
