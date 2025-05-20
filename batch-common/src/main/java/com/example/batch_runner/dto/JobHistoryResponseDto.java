package com.example.batch_runner.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobHistoryResponseDto {

    private List<JobHistoryDto> jobHistoryList;
    private long totalElements;
    private int totalPage;
}
