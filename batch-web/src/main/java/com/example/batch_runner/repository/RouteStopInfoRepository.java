package com.example.batch_runner.repository;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.domain.RouteStopInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStopInfoRepository extends JpaRepository<RouteStopInfo, RouteStopInfoId> {

}
