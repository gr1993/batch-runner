package com.example.batch_runner.service;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.dto.RouteInfoDto;
import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.external.client.RestApiClient;
import com.example.batch_runner.external.dto.BaiServiceResult;
import com.example.batch_runner.external.dto.BusArrivalInfoDto;
import com.example.batch_runner.repository.FavoriteRouteRepository;
import com.example.batch_runner.repository.RouteStopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {

    private final RouteStopInfoRepository routeStopInfoRepository;
    private final RestApiClient restApiClient;
    private final FavoriteRouteRepository favoriteRouteRepository;

    @Value("${app.serviceKey}")
    private String serviceKey;

    public List<RouteStopResDto> getRouteStopResList() {
        List<RouteStopInfo> routeStopInfoList = routeStopInfoRepository.findAll();

        // 다른 노선에 같은 정류장이 있을 수 있으므로 nodeId로 그룹핑
        return routeStopInfoList.stream()
                .collect(Collectors.groupingBy(RouteStopInfo::getNodeId))
                .values()
                .stream()
                .map(list -> list.get(0))
                .map(RouteStopResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RouteInfoDto> getRouteIntoListByNodeId(String nodeId) {
        List<RouteStopInfo> routeStopInfoList = routeStopInfoRepository.findAllByNodeId(nodeId);

        return routeStopInfoList.stream()
                .map(stop -> new RouteInfoDto(stop.getId().getRouteId(), stop.getRouteName(), stop.getId().getNodeSeq()))
                .collect(Collectors.toList());
    }

    public List<RouteStopResDto> getStopListByRouteId(String routeId) {
        List<RouteStopInfo> routeStopInfoList = routeStopInfoRepository.findAllByIdRouteIdOrderByIdNodeSeqAsc(routeId);

        return routeStopInfoList.stream()
                .map(RouteStopResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BusArrivalInfoDto> getBusArrivalInfoList(String nodeId, String routeId, String nodeSeq) {
        String url = String.format(
            "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute?serviceKey=%s&stId=%s&busRouteId=%s&ord=%s",
            serviceKey, nodeId, routeId, nodeSeq
        );
        BaiServiceResult response = restApiClient.get(url, BaiServiceResult.class);
        return response.getMsgBody().getItemList();
    }

    public List<RouteStopResDto> getFavoriteStopList() {
        List<String> favoriteNodeIdList = favoriteRouteRepository.findNodeIdsGroupByNodeId();

        List<RouteStopInfo> favoriteStopList = routeStopInfoRepository.findAllByNodeIdIn(favoriteNodeIdList);
        return favoriteStopList.stream()
                .collect(Collectors.groupingBy(RouteStopInfo::getNodeId))
                .values()
                .stream()
                .map(list -> list.get(0))
                .map(RouteStopResDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFavoriteStop(String nodeId) {
        List<RouteStopInfo> stopList = routeStopInfoRepository.findAllByNodeId(nodeId);
        for (RouteStopInfo stop : stopList) {
            favoriteRouteRepository.insertFavorite(stop.getId().getRouteId(), stop.getNodeId());
        }
    }

    @Transactional
    public void removeFavoriteStop(String nodeId) {
        favoriteRouteRepository.deleteFavorite(nodeId);
    }
}
