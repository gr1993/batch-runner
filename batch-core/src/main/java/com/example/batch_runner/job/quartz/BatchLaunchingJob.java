package com.example.batch_runner.job.quartz;

import com.example.batch_runner.config.batch.CustomJobRegistry;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchLaunchingJob implements Job {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private CustomJobRegistry jobRegistry;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = context.getJobDetail().getKey().getName();
            org.springframework.batch.core.Job batchJob = jobRegistry.getJob(jobName);

            JobParametersBuilder builder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis());

            // Quartz로 넘겨받은 Job 파라미터를 Batch로 전달
            JobDataMap dataMap = context.getMergedJobDataMap();
            dataMap.forEach((k, v) -> {
                if (v instanceof String) builder.addString(k, (String) v);
                else if (v instanceof Long) builder.addLong(k, (Long) v);
                else if (v instanceof Integer) builder.addLong(k, ((Integer) v).longValue());
                else if (v instanceof Double) builder.addDouble(k, (Double) v);
            });

            jobLauncher.run(batchJob, builder.toJobParameters());

        } catch (Exception e) {
            throw new JobExecutionException("Failed to run Spring Batch job", e);
        }
    }
}
