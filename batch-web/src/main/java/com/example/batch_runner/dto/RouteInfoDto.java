package com.example.batch_runner.dto;

import lombok.Data;

@Data
public class RouteInfoDto {
    private String routeId;
    private String routeName;
    private Integer nodeSeq;

    public RouteInfoDto() {}
    public RouteInfoDto(String routeId, String routeName, Integer nodeSeq) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.nodeSeq = nodeSeq;
    }
}
