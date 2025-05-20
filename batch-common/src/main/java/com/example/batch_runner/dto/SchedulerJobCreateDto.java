package com.example.batch_runner.dto;

import lombok.Data;

@Data
public class SchedulerJobCreateDto {
    private String jobName;
    private String cronExpression;
    private String description;
}
