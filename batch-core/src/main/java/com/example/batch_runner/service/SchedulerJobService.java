package com.example.batch_runner.service;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.dto.SchedulerJobCreateDto;
import com.example.batch_runner.dto.SchedulerJobUpdateDto;
import com.example.batch_runner.job.quartz.BatchLaunchingJob;
import com.example.batch_runner.repository.SchedulerJobRepository;
import com.example.batch_runner.scheduler.QuartzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerJobService {

    private final QuartzService quartzService;
    private final Scheduler scheduler;
    private final SchedulerJobRepository schedulerJobRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * DB에 등록한 스케줄 정보와 Quartz의 Job 정보를 결합하여 스케줄 정보를 반환
     */
    public List<ScheduleInfoDto> getScheduleInfoList(String jobName) {
        try {
            List<SchedulerJob> jobList = schedulerJobRepository.findAllByUseYn("Y");
            List<ScheduleInfoDto> scheduleInfoList = new ArrayList<>();
            for (SchedulerJob job : jobList) {
                // jobName 검색어가 있으면 매칭된 job만 처리
                if (StringUtils.hasText(jobName) && !job.getJobName().contains(jobName)) {
                    continue;
                }

                ScheduleInfoDto scheduleInfoDto = new ScheduleInfoDto();
                scheduleInfoDto.setId(job.getId());
                scheduleInfoDto.setJobName(job.getJobName());
                scheduleInfoDto.setCronExpression(job.getCronExpression());
                scheduleInfoDto.setUseYn(job.getUseYn());

                Trigger trigger = quartzService.getTrigger(job.getJobName());
                Trigger.TriggerState state = scheduler.getTriggerState(trigger.getKey());
                Date previousFireTime = trigger.getPreviousFireTime();
                Date nextFireTime = trigger.getNextFireTime();
                boolean isRunning = quartzService.isRunning(job.getJobName());

                scheduleInfoDto.setStatus(isRunning ? "RUNNING" : state.name());
                if (previousFireTime != null) {
                    scheduleInfoDto.setPreviousFireTime(formatter.format(previousFireTime));
                }
                if (nextFireTime != null) {
                    scheduleInfoDto.setNextFireTime(formatter.format(nextFireTime));
                }
                scheduleInfoList.add(scheduleInfoDto);
            }
            return scheduleInfoList;
        } catch (SchedulerException ex) {
            throw new RuntimeException("schedulerJob SchedulerException", ex);
        }
    }

    /**
     * 스케줄 JOB 정보 등록
     */
    @Transactional
    public void createScheduleInfo(SchedulerJobCreateDto createDto) {
        Optional<SchedulerJob> find = schedulerJobRepository.findByJobName(createDto.getJobName());
        if (find.isPresent()) {
            throw new IllegalArgumentException("already exist job");
        }

        SchedulerJob schedulerJob = new SchedulerJob();
        schedulerJob.setJobName(createDto.getJobName());
        schedulerJob.setJobGroup(QuartzService.JOB_GROUP);
        schedulerJob.setCronExpression(createDto.getCronExpression());
        schedulerJob.setDescription(createDto.getDescription());
        schedulerJob.setUseYn("Y");
        schedulerJobRepository.save(schedulerJob);

        // 스케줄러에 job을 등록
        upsertJobInQuartz(schedulerJob);
    }
    
    /**
     * 스케줄 JOB 정보 변경
     */
    @Transactional
    public void updateScheduleInfo(long id, SchedulerJobUpdateDto updateDto) {
        SchedulerJob schedulerJob = getSchedulerJobById(id);
        schedulerJob.setCronExpression(updateDto.getCronExpression());
        schedulerJob.setDescription(updateDto.getDescription());

        // 스케줄러에 Job을 업데이트
        upsertJobInQuartz(schedulerJob);
    }

    /**
     * 스케줄 JOB 정보 삭제
     */
    @Transactional
    public void deleteScheduleInfo(long id) {
        SchedulerJob schedulerJob = getSchedulerJobById(id);
        // 스케줄러에서 Job을 제거
        quartzService.deleteJob(schedulerJob.getJobName());

        schedulerJobRepository.delete(schedulerJob);
    }

    /**
     * 스케줄러의 Job 스케줄 중단 (이미 실행중인 Job은 계속 진행됨)
     */
    public void pauseSchedule(long id) {
        SchedulerJob schedulerJob = getSchedulerJobById(id);
        quartzService.pauseJob(schedulerJob.getJobName());
    }

    /**
     * 스케줄러의 Job 스케줄 재개
     */
    public void resumeSchedule(long id) {
        SchedulerJob schedulerJob = getSchedulerJobById(id);
        quartzService.resumeJob(schedulerJob.getJobName());
    }

    private void upsertJobInQuartz(SchedulerJob schedulerJob) {
        try {
            quartzService.addJob(
                    BatchLaunchingJob.class,
                    schedulerJob.getJobName(),
                    schedulerJob.getDescription(),
                    schedulerJob.getJobParamsJson(),
                    schedulerJob.getCronExpression()
            );
        } catch (SchedulerException ex) {
            throw new RuntimeException("Job Info Update Failed!", ex);
        }
    }

    private SchedulerJob getSchedulerJobById(long id) {
        Optional<SchedulerJob> find = schedulerJobRepository.findById(id);
        if (find.isEmpty()) {
            throw new IllegalArgumentException("not exist job");
        }
        return find.get();
    }
}
