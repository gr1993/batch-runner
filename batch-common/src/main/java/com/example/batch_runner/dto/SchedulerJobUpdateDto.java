package com.example.batch_runner.dto;

import lombok.Data;

@Data
public class SchedulerJobUpdateDto {
    private String cronExpression;
    private String description;
}
