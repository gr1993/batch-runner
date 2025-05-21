package com.example.batch_runner.repository;

import com.example.batch_runner.domain.RouteBusPos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteBusPosRepository extends JpaRepository<RouteBusPos, String> {
    void deleteByRouteId(String routeId);
}
