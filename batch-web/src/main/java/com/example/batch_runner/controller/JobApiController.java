package com.example.batch_runner.controller;

import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobApiController {

    private final JobService jobService;

    /**
     * batch job 이름 리스트 가져오기
     */
    @GetMapping("/batch/name")
    public ResponseEntity<List<String>> getBatchJobNameList() {
        return ResponseEntity.ok(jobService.getBatchJobNameList());
    }

    /**
     * 작업 등록
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createJob(@RequestBody SchedulerJobCreateDto createDto) {
        jobService.createScheduleInfo(createDto);
        return ResponseEntity.ok().build();
    }
}
