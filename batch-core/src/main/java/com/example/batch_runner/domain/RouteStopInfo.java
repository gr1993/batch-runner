package com.example.batch_runner.domain;

import lombok.Data;

@Data
public class RouteStopInfo {
    private String routeId;
    private String routeName;
    private Integer nodeSeq;
    private String nodeId;
    private String arsId;
    private String nodeName;
    private Double posX;
    private Double posY;
}
