package com.example.batch_runner.service;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.dto.RouteInfoDto;
import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.repository.RouteStopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RouteStopInfoRepository routeStopInfoRepository;

    public List<RouteStopResDto> getRouteStopResList() {
        List<RouteStopInfo> routeStopInfoList = routeStopInfoRepository.findAll();
        List<RouteStopResDto> result = new ArrayList<>();
        for (RouteStopInfo routeStopInfo : routeStopInfoList) {
            result.add(RouteStopResDto.fromEntity(routeStopInfo));
        }
        return result;
    }

    public List<RouteInfoDto> getRouteIntoListByNodeId(String nodeId) {
        List<RouteStopInfo> routeStopInfoList = routeStopInfoRepository.findAllByNodeId(nodeId);
        List<RouteInfoDto> result = new ArrayList<>();
        for (RouteStopInfo routeStopInfo : routeStopInfoList) {
            result.add(new RouteInfoDto(routeStopInfo.getId().getRouteId(), routeStopInfo.getRouteName()));
        }
        return result;
    }
}
