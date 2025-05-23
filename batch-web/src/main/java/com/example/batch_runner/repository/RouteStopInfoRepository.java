package com.example.batch_runner.repository;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.domain.RouteStopInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteStopInfoRepository extends JpaRepository<RouteStopInfo, RouteStopInfoId> {
    List<RouteStopInfo> findAllByNodeId(String nodeId);

    List<RouteStopInfo> findAllByIdRouteIdOrderByIdNodeSeqAsc(String routeId);
}
