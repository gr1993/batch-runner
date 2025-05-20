package com.example.batch_runner.controller;

import com.example.batch_runner.config.batch.CustomJobRegistry;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.service.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final CustomJobRegistry jobRegistry;
    private final SchedulerJobService schedulerJobService;

    /**
     * batch 작업명 리스트 반환 API
     */
    @GetMapping("/batch")
    public ResponseEntity<List<String>> getJobNames() {
        return ResponseEntity.ok(new ArrayList<>(jobRegistry.getAllJobNames()));
    }

    /**
     * schedule 작업 리스트 반환 API
     */
    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleInfoDto>> getScheduleJobList() {
        List<ScheduleInfoDto> scheduleInfoList = schedulerJobService.getScheduleInfoList();
        return ResponseEntity.ok(scheduleInfoList);
    }
}
