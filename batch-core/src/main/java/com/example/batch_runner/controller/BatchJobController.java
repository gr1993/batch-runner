package com.example.batch_runner.controller;

import com.example.batch_runner.config.batch.CustomJobRegistry;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final CustomJobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    /**
     * batch 작업명 리스트 반환 API
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getJobNames() {
        return ResponseEntity.ok(new ArrayList<>(jobRegistry.getAllJobNames()));
    }

    /**
     * batch 작업 실행 API
     */
    @PostMapping("/execute/{jobName}")
    public ResponseEntity<Void> executeJob(@PathVariable(value = "jobName") String jobName) {
        Job batchJob = jobRegistry.getJob(jobName);

        try {
            JobParametersBuilder builder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis());

            jobLauncher.run(batchJob, builder.toJobParameters());
        } catch (Exception e) {
            throw new RuntimeException("Failed to run Spring Batch job", e);
        }

        return ResponseEntity.ok().build();
    }
}
