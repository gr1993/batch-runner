package com.example.batch_runner.service;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.dto.RouteInfoDto;
import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.external.client.RestApiClient;
import com.example.batch_runner.external.dto.BaiServiceResult;
import com.example.batch_runner.external.dto.BusArrivalInfoDto;
import com.example.batch_runner.repository.RouteStopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RouteStopInfoRepository routeStopInfoRepository;
    private final RestApiClient restApiClient;

    @Value("${app.serviceKey}")
    private String serviceKey;

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

    public List<RouteStopResDto> getStopListByRouteId(String routeId) {
        List<RouteStopInfo> routeStopInfoList =routeStopInfoRepository.findAllByIdRouteIdOrderByIdNodeSeqAsc(routeId);
        List<RouteStopResDto> result = new ArrayList<>();
        for (RouteStopInfo routeStopInfo : routeStopInfoList) {
            result.add(RouteStopResDto.fromEntity(routeStopInfo));
        }
        return result;
    }

    public List<BusArrivalInfoDto> getBusArrivalInfoList(String nodeId, String routeId, String nodeSeq) {
        String url = String.format(
            "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute?serviceKey=%s&stId=%s&busRouteId=%s&ord=%s",
            serviceKey, nodeId, routeId, nodeSeq
        );
        BaiServiceResult response = restApiClient.get(url, BaiServiceResult.class);
        return response.getMsgBody().getItemList();
    }
}
