package com.example.batch_runner.service;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.dto.JobHistoryResponseDto;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.dto.SchedulerJobUpdateDto;
import com.example.batch_runner.repository.SchedulerJobRepository;
import com.example.batch_runner.util.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final SchedulerJobRepository schedulerJobRepository;
    private final RestApiClient restApiClient;

    @Value("${app.batch.core.url}")
    private String batchCoreUrl;


    public List<String> getBatchJobNameList() {
        return restApiClient.fetchList(batchCoreUrl + "/api/batch/jobs", String.class);
    }

    public List<ScheduleInfoDto> getScheduleInfoList(String jobName) {
        String uri = "/api/schedule";
        if (StringUtils.hasText(jobName)) {
            uri += "?jobName=" + jobName;
        }
        return restApiClient.fetchList(batchCoreUrl + uri, ScheduleInfoDto.class);
    }

    public SchedulerJob getScheduleJob(long id) {
        return schedulerJobRepository.findById(id).orElse(null);
    }

    public void createScheduleInfo(SchedulerJobCreateDto createDto) {
        restApiClient.post(batchCoreUrl + "/api/schedule", createDto);
    }

    public void updateScheduleInfo(long id, SchedulerJobUpdateDto updateDto) {
        restApiClient.put(batchCoreUrl + "/api/schedule/" + id, updateDto);
    }

    public JobHistoryResponseDto getJobHistory(String jobName, int page, int pageSize) {
        String url = batchCoreUrl + "/api/batch/history/" + jobName + "?page=" + page + "&pageSize=" + pageSize;
        return restApiClient.get(url, JobHistoryResponseDto.class);
    }

    public void executeJob(String jobName) {
        restApiClient.post(batchCoreUrl + "/api/batch/execute/" + jobName, null);
    }

    public void pauseSchedule(long id) {
        restApiClient.post(batchCoreUrl + "/api/schedule/pause/" + id, null);
    }

    public void resumeSchedule(long id) {
        restApiClient.post(batchCoreUrl + "/api/schedule/resume/" + id, null);
    }

}
