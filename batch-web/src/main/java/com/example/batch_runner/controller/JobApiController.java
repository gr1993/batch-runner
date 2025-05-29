package com.example.batch_runner.controller;

import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobApiController {

    private final JobService jobService;

    /**
     * 작업 등록
     */
    @PostMapping
    public ResponseEntity<Void> createJob(@ModelAttribute SchedulerJobCreateDto createDto) {
        jobService.createScheduleInfo(createDto);
        return ResponseEntity.ok().build();
    }
}
