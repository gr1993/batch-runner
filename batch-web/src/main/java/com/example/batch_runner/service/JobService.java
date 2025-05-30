package com.example.batch_runner.service;

import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.external.client.RestApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final RestApiClient restApiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.batch.core.url}")
    private String batchCoreUrl;


    public List<String> getBatchJobNameList() {
        try {
            String json = restApiClient.get(batchCoreUrl + "/api/batch/jobs", String.class);
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void createScheduleInfo(SchedulerJobCreateDto createDto) {
        restApiClient.post(batchCoreUrl + "/api/schedule", createDto);
    }
}
