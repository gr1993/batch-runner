package com.example.batch_runner.controller;

import com.example.batch_runner.dto.RouteStopResDto;
import com.example.batch_runner.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapApiController {

    private final MapService mapService;

    @GetMapping("stop")
    public ResponseEntity<List<RouteStopResDto>> map() {
        return ResponseEntity.ok(mapService.getRouteStopResList());
    }
}
