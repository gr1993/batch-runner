package com.example.batch_runner.repository;

import com.example.batch_runner.domain.SchedulerJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchedulerJobRepository extends JpaRepository<SchedulerJob, Long> {
    Optional<SchedulerJob> findByJobName(String jobName);
    List<SchedulerJob> findAllByUseYn(String useYn);
}
