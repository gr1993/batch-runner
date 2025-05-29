package com.example.batch_runner.controller;

import com.example.batch_runner.dto.RouteInfoDto;
import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.external.dto.BusArrivalInfoDto;
import com.example.batch_runner.service.BusPosService;
import com.example.batch_runner.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapApiController {

    private final MapService mapService;
    private final BusPosService busPosService;

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

    /**
     * 특정 정류장의 도착 시간 정보 조회
     */
    @GetMapping("stop/arrival")
    public ResponseEntity<List<BusArrivalInfoDto>> getBusArrivalInfoList(@RequestParam("nodeId") String nodeId,
                                                                         @RequestParam("routeId") String routeId,
                                                                         @RequestParam("nodeSeq") String nodeSeq) {
        return ResponseEntity.ok(mapService.getBusArrivalInfoList(nodeId, routeId, nodeSeq));
    }

    /**
     * 즐겨찾기 정류장 목록 조회
     */
    @GetMapping("stop/favorites")
    public ResponseEntity<List<RouteStopResDto>> getFavoriteStopList() {
        return ResponseEntity.ok(mapService.getFavoriteStopList());
    }

    /**
     * 정류소 즐겨찾기 추가
     */
    @PostMapping("stop/favorite/{nodeId}")
    public ResponseEntity<Void> addFavoriteStop(@PathVariable("nodeId") String nodeId) {
        mapService.addFavoriteStop(nodeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 정류소 즐겨찾기 삭제
     */
    @DeleteMapping("stop/favorite/{nodeId}")
    public ResponseEntity<Void> removeFavoriteStop(@PathVariable("nodeId") String nodeId) {
        mapService.removeFavoriteStop(nodeId);
        return ResponseEntity.ok().build();
    }

    /**
     * 즐겨찾기에 등록된 노선의 버스 위치 조회 (SSE)
     */
    @GetMapping(value = "bus/pos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getBusPosListByFavoriteRouteId() {
        return ResponseEntity.ok(busPosService.registerSse());
    }
}
