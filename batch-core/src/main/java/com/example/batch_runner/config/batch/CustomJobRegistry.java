package com.example.batch_runner.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CustomJobRegistry {
    private final Map<String, Job> jobMap;

    /**
     * @param jobs : Spring Batch Job들이 자동으로 주입
     */
    public CustomJobRegistry(List<Job> jobs) {
        this.jobMap = jobs.stream()
                .collect(Collectors.toMap(Job::getName, Function.identity()));
    }

    public Job getJob(String name) {
        return jobMap.get(name);
    }

    public Set<String> getAllJobNames() {
        return jobMap.keySet();
    }
}