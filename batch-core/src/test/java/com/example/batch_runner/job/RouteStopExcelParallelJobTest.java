package com.example.batch_runner.job;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RouteStopExcelParallelJobTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job routeStopExcelParallelJob;

    @Test
    public void executeRouteStopExcelParallelJob() throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(routeStopExcelParallelJob, jobParameters);
    }
}
