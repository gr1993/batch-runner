package com.example.batch_runner.repository;

import com.example.batch_runner.domain.SchedulerJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerJobRepository extends JpaRepository<SchedulerJob, Long> {
    List<SchedulerJob> findByUseYn(String useYn);
}
