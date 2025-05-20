package com.example.batch_runner.controller;

import com.example.batch_runner.config.batch.CustomJobRegistry;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.dto.SchedulerJobUpdateDto;
import com.example.batch_runner.service.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduledJobController {

    private final SchedulerJobService schedulerJobService;

    /**
     * schedule 작업 리스트 반환 API
     */
    @GetMapping
    public ResponseEntity<List<ScheduleInfoDto>> getScheduleJobList() {
        List<ScheduleInfoDto> scheduleInfoList = schedulerJobService.getScheduleInfoList();
        return ResponseEntity.ok(scheduleInfoList);
    }

    /**
     * schedule 작업 정보 생성 API
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createScheduleInfo(@RequestBody SchedulerJobCreateDto createDto) {
        schedulerJobService.createScheduleInfo(createDto);
        return ResponseEntity.ok().build();
    }

    /**
     * schedule 작업 정보 변경 API
     */
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<Void> updateScheduleInfo(@PathVariable(name = "id") long id,
                                                   @RequestBody SchedulerJobUpdateDto updateDto) {
        schedulerJobService.updateScheduleInfo(id, updateDto);
        return ResponseEntity.ok().build();
    }

    /**
     * schedule 작업 정보 삭제 API
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateScheduleInfo(@PathVariable(name = "id") long id) {
        schedulerJobService.deleteScheduleInfo(id);
        return ResponseEntity.ok().build();
    }
}
