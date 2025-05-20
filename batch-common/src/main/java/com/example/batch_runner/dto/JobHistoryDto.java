package com.example.batch_runner.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobHistoryDto {

    private Long jobExecutionId;
    private String jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String exitCode;
    private String exitMessage;
}
