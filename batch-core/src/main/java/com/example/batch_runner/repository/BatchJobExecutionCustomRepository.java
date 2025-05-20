package com.example.batch_runner.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BatchJobExecutionCustomRepository {

    private final EntityManager em;

    public Page<Map<String, Object>> findExecutions(String jobName, Pageable pageable) {
        String sql = """
            SELECT 
                e.JOB_EXECUTION_ID,
                i.JOB_NAME,
                e.START_TIME,
                e.END_TIME,
                e.STATUS,
                e.EXIT_CODE
            FROM 
                BATCH_JOB_EXECUTION e
            JOIN 
                BATCH_JOB_INSTANCE i ON e.JOB_INSTANCE_ID = i.JOB_INSTANCE_ID
            WHERE 
                i.JOB_NAME = :jobName
            ORDER BY 
                e.START_TIME DESC
            """;

        Query query = em.createNativeQuery(sql);
        query.setParameter("jobName", jobName);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("JOB_EXECUTION_ID", row[0]);
            map.put("JOB_NAME", row[1]);
            map.put("START_TIME", row[2]);
            map.put("END_TIME", row[3]);
            map.put("STATUS", row[4]);
            map.put("EXIT_CODE", row[5]);
            result.add(map);
        }

        // Count 쿼리
        String countSql = """
            SELECT COUNT(*)
            FROM BATCH_JOB_EXECUTION e
            JOIN BATCH_JOB_INSTANCE i ON e.JOB_INSTANCE_ID = i.JOB_INSTANCE_ID
            WHERE i.JOB_NAME = :jobName
            """;

        long total = ((Number) em.createNativeQuery(countSql)
                .setParameter("jobName", jobName)
                .getSingleResult()).longValue();

        return new PageImpl<>(result, pageable, total);
    }
}
