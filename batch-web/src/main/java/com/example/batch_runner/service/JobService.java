package com.example.batch_runner.service;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.dto.SchedulerJobUpdateDto;
import com.example.batch_runner.external.client.RestApiClient;
import com.example.batch_runner.repository.SchedulerJobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.batch.core.url}")
    private String batchCoreUrl;


    public List<String> getBatchJobNameList() {
        return fetchAndParse(batchCoreUrl + "/api/batch/jobs", new TypeReference<List<String>>() {});
    }

    public List<ScheduleInfoDto> getScheduleInfoList(String jobName) {
        String uri = "/api/schedule";
        if (StringUtils.hasText(jobName)) {
            uri += "?jobName=" + jobName;
        }
        return fetchAndParse(batchCoreUrl + uri, new TypeReference<List<ScheduleInfoDto>>() {});
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

    private <T> T fetchAndParse(String url, TypeReference<T> typeRef) {
        try {
            String json = restApiClient.get(url, String.class);
            return mapper.readValue(json, typeRef);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("JSON 파싱 실패: " + url, ex);
        }
    }
}
