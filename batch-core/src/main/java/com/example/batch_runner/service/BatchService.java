package com.example.batch_runner.service;

import com.example.batch_runner.config.batch.CustomJobRegistry;
import com.example.batch_runner.dto.JobHistoryDto;
import com.example.batch_runner.dto.JobHistoryResponseDto;
import com.example.batch_runner.repository.BatchJobExecutionCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final CustomJobRegistry jobRegistry;
    private final JobLauncher jobLauncher;
    private final BatchJobExecutionCustomRepository batchJobExecutionCustomRepository;

    public Set<String> getJobNames() {
        return jobRegistry.getAllJobNames();
    }

    public void executeJob(String jobName) {
        Job batchJob = jobRegistry.getJob(jobName);

        new Thread(() -> {
            try {
                JobParametersBuilder builder = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis());

                jobLauncher.run(batchJob, builder.toJobParameters());
            } catch (Exception e) {
                throw new RuntimeException("Failed to run Spring Batch job", e);
            }
        }).start();
    }

    public JobHistoryResponseDto getJobHistory(String jobName, Pageable pageable) {
        Page<Map<String, Object>> executionsPage = batchJobExecutionCustomRepository.findExecutions(jobName, pageable);
        List<JobHistoryDto> jobHistoryList = new ArrayList<>();
        for (Map<String, Object> execution : executionsPage.getContent()) {
            JobHistoryDto dto = new JobHistoryDto();
            dto.setJobExecutionId(((Number) execution.get("JOB_EXECUTION_ID")).longValue());
            dto.setJobName((String) execution.get("JOB_NAME"));
            dto.setStartTime(toLocalDateTime(execution.get("START_TIME")));
            dto.setEndTime(toLocalDateTime(execution.get("END_TIME")));
            dto.setStatus((String) execution.get("STATUS"));
            dto.setExitCode((String) execution.get("EXIT_CODE"));
            dto.setExitMessage((String) execution.get("EXIT_MESSAGE"));
            jobHistoryList.add(dto);
        }

        JobHistoryResponseDto jobHistoryResponseDto = new JobHistoryResponseDto();
        jobHistoryResponseDto.setJobHistoryList(jobHistoryList);
        jobHistoryResponseDto.setTotalElements(executionsPage.getTotalElements());
        jobHistoryResponseDto.setTotalPage(executionsPage.getTotalPages());
        return jobHistoryResponseDto;
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        } else if (value instanceof Date) {
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }
}
