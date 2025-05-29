package com.example.batch_runner.service;

import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.external.client.RestApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final RestApiClient restApiClient;

    @Value("${app.batch.core.url}")
    private String batchCoreUrl;

    public void createScheduleInfo(SchedulerJobCreateDto createDto) {
        restApiClient.post(batchCoreUrl + "/api/schedule", createDto);
    }
}
