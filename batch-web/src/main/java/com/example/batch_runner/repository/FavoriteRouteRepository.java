package com.example.batch_runner.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRouteRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 즐겨찾기 추가 (native SQL 사용)
     */
    public void insertFavorite(String routeId, String nodeId) {
        em.createNativeQuery("INSERT INTO favorite_route(route_id, node_id) VALUES (?, ?)")
                .setParameter(1, routeId)
                .setParameter(2, nodeId)
                .executeUpdate();
    }

    /**
     * 즐겨찾기 삭제
     */
    public void deleteFavorite(String nodeId) {
        em.createQuery("DELETE FROM FavoriteRoute f WHERE f.nodeId = :nodeId")
                .setParameter("nodeId", nodeId)
                .executeUpdate();
    }

    /**
     * 즐겨찾기 정류장 목록 가져오기
     */
    public List<String> findNodeIdsGroupByNodeId() {
        return em.createQuery("SELECT f.nodeId FROM FavoriteRoute f GROUP BY f.nodeId", String.class)
                .getResultList();
    }
}