package com.example.batch_runner.dto;

import com.example.batch_runner.domain.RouteStopInfo;
import lombok.Data;

@Data
public class RouteStopResDto {

    private String routeId;
    private Integer nodeSeq;
    private String routeName;
    private String nodeId;
    private String arsId;
    private String nodeName;
    private Double posX;
    private Double posY;

    public static RouteStopResDto fromEntity(RouteStopInfo entity) {
        RouteStopResDto dto = new RouteStopResDto();
        dto.setRouteId(entity.getId().getRouteId());
        dto.setNodeSeq(entity.getId().getNodeSeq());
        dto.setRouteName(entity.getRouteName());
        dto.setNodeId(entity.getNodeId());
        dto.setArsId(entity.getArsId());
        dto.setNodeName(entity.getNodeName());
        dto.setPosX(entity.getPosX());
        dto.setPosY(entity.getPosY());
        return dto;
    }
}
