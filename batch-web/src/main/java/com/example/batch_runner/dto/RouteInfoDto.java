package com.example.batch_runner.dto;

import lombok.Data;

@Data
public class RouteInfoDto {
    private String routeId;
    private String routeName;

    public RouteInfoDto() {}
    public RouteInfoDto(String routeId, String routeName) {
        this.routeId = routeId;
        this.routeName = routeName;
    }
}
