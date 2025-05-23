package com.example.batch_runner.controller;

import com.example.batch_runner.dto.RouteInfoDto;
import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapApiController {

    private final MapService mapService;

    /**
     * 모든 정류소 정보 조회
     */
    @GetMapping("stop")
    public ResponseEntity<List<RouteStopResDto>> getRouteStopAll() {
        return ResponseEntity.ok(mapService.getRouteStopResList());
    }

    /**
     * 특정 정류소의 모든 노선 리스트 조회
     */
    @GetMapping("stop/{nodeId}/routes")
    public ResponseEntity<List<RouteInfoDto>> getRouteIntoListByNodeId(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(mapService.getRouteIntoListByNodeId(nodeId));
    }

    /**
     * 특정 노선의 모든 정류장 리스트 조회
     */
    @GetMapping("routes/{routeId}/stops")
    public ResponseEntity<List<RouteStopResDto>> getStopListByRouteId(@PathVariable("routeId") String routeId) {
        return ResponseEntity.ok(mapService.getStopListByRouteId(routeId));
    }

}
