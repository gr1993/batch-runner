package com.example.batch_runner.controller;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.dto.JobHistoryResponseDto;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.dto.SchedulerJobUpdateDto;
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
     * batch job 이름 리스트 조회
     */
    @GetMapping("/batch/name")
    public ResponseEntity<List<String>> getBatchJobNameList() {
        return ResponseEntity.ok(jobService.getBatchJobNameList());
    }

    /**
     * 작업 정보 리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<ScheduleInfoDto>> getJobList(@RequestParam(value = "jobName", required = false) String jobName) {
        return ResponseEntity.ok(jobService.getScheduleInfoList(jobName));
    }

    /**
     * id로 작업 정보 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SchedulerJob> getJob(@PathVariable(value = "id") long id) {
        return ResponseEntity.ok(jobService.getScheduleJob(id));
    }

    /**
     * 작업 등록
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createJob(@RequestBody SchedulerJobCreateDto createDto) {
        jobService.createScheduleInfo(createDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 작업 수정
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateJob(@PathVariable(value = "id") long id,
                                          @RequestBody SchedulerJobUpdateDto updateDto) {
        jobService.updateScheduleInfo(id, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 실행 이력 조회
     */
    @GetMapping(value = "/history/{jobName}")
    public ResponseEntity<JobHistoryResponseDto> getJobHistory(@PathVariable(value = "jobName") String jobName,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "pageSize") int pageSize) {
        return ResponseEntity.ok(jobService.getJobHistory(jobName, page, pageSize));
    }

    /**
     * 작업 수동 실행
     */
    @PostMapping(value = "/execute/{jobName}")
    public ResponseEntity<Void> executeJob(@PathVariable(value = "jobName") String jobName) {
        jobService.executeJob(jobName);
        return ResponseEntity.ok().build();
    }

    /**
     * 스케줄 중단
     */
    @PostMapping(value = "/pause/{id}")
    public ResponseEntity<Void> pauseSchedule(@PathVariable(value = "id") long id) {
        jobService.pauseSchedule(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 스케줄 재개
     */
    @PostMapping(value = "/resume/{id}")
    public ResponseEntity<Void> resumeSchedule(@PathVariable(value = "id") long id) {
        jobService.resumeSchedule(id);
        return ResponseEntity.ok().build();
    }
}
