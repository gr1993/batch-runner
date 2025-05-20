package com.example.batch_runner.dto;

import lombok.Data;

@Data
public class ScheduleInfoDto {

    private Long id;

    private String jobName;

    private String status;

    private String cronExpression;

    private String previousFireTime;

    private String nextFireTime;

    private String useYn;
}
