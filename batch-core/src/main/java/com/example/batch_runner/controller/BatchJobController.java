package com.example.batch_runner.controller;

import com.example.batch_runner.dto.JobHistoryResponseDto;
import com.example.batch_runner.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final BatchService batchService;

    /**
     * batch 작업명 리스트 반환 API
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getJobNames() {
        return ResponseEntity.ok(new ArrayList<>(batchService.getJobNames()));
    }

    /**
     * batch 작업 실행 API
     */
    @PostMapping("/execute/{jobName}")
    public ResponseEntity<Void> executeJob(@PathVariable(value = "jobName") String jobName) {
        batchService.executeJob(jobName);
        return ResponseEntity.ok().build();
    }

    /**
     * batch 작업 이력 조회 API
     */
    @GetMapping("/history/{jobName}")
    public ResponseEntity<JobHistoryResponseDto> getJobHistory(@PathVariable(value = "jobName") String jobName,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "pageSize") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(batchService.getJobHistory(jobName, pageable));
    }
}
