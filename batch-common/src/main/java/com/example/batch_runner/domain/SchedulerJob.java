package com.example.batch_runner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
public class SchedulerJob {

    @Id
    private Long id;

    @Column
    private String jobName;

    @Column
    private String jobGroup;

    @Column
    private String cronExpression;

    @Column
    private String useYn;

    @Column
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> jobParamsJson;
}
