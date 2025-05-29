package com.example.batch_runner.repository;

import com.example.batch_runner.domain.RouteBusPos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteBusPosRepository extends JpaRepository<RouteBusPos, String> {
    List<RouteBusPos> findAllByRouteIdIn(List<String> routeIds);
}
