package com.example.batch_runner.dto;

import com.example.batch_runner.domain.RouteStopInfo;
import lombok.Data;

@Data
public class RouteStopResDto {

    private String nodeId;
    private String arsId;
    private String nodeName;
    private Double lat;
    private Double lng;

    public static RouteStopResDto fromEntity(RouteStopInfo entity) {
        RouteStopResDto dto = new RouteStopResDto();
        dto.setNodeId(entity.getNodeId());
        dto.setArsId(entity.getArsId());
        dto.setNodeName(entity.getNodeName());
        dto.setLat(entity.getPosY());
        dto.setLng(entity.getPosX());
        return dto;
    }
}
