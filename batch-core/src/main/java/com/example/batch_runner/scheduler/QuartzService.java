package com.example.batch_runner.scheduler;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.job.quartz.BatchLaunchingJob;
import com.example.batch_runner.repository.SchedulerJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzService {

    private final SchedulerJobRepository jobRepository;
    private final Scheduler scheduler;

    private static final String JOB_GROUP = "jobGroup";
    private static final String TRIGGER_GROUP = "triggerGroup";
    private static final String TRIGGER_POST_FIX = "Trigger";

    /**
     * 어플리케이션이 시작 시 DB에서 스케줄 작업 정보를 가져와 설정
     * @throws SchedulerException
     */
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

    /**
     * 스케줄러에 작업을 등록 (Merge 방식)
     */
    public void addJob(Class<? extends Job> jobClass
            , String jobName
            , String jobDescription
            , Map<String, Object> jobDataMap
            , String cronExpression) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(jobClass, jobName, jobDescription, jobDataMap);
        Trigger trigger = buildCronTrigger(cronExpression, jobName + TRIGGER_POST_FIX);

        if (scheduler.checkExists(jobDetail.getKey())) {
            log.info("업데이트 할 Job : {}", jobDetail.getKey());
            scheduler.deleteJob(jobDetail.getKey());
        } else {
            log.info("새로운 Job 추가 : {}", jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 스케줄러에 등록된 작업의 Trigger를 반환
     */
    public Trigger getTrigger(String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, JOB_GROUP);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            if (triggers.isEmpty()) {
                throw new RuntimeException("Trigger not found for job: " + jobKey);
            }
            return triggers.get(0);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to retrieve trigger for job: " + jobName, e);
        }
    }

    /**
     * 스케줄 Job이 실행중인지 검사
     */
    public boolean isRunning(String jobName) {
        try {
            List<JobExecutionContext> currentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();

            return currentlyExecutingJobs.stream()
                    .anyMatch(context -> context.getJobDetail().getKey().equals(new JobKey(jobName, JOB_GROUP)));
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to retrieve job status: " + jobName, e);
        }
    }

    private JobDetail buildJobDetail(Class<? extends Job> job
            , String name
            , String desc
            , Map<String, Object> paramsMap) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(paramsMap);

        return JobBuilder.newJob(job)
                .withIdentity(name, JOB_GROUP)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronTrigger(String cronExp, String triggerName) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerName, TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}
