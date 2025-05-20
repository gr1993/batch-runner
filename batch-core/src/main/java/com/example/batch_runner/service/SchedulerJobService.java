package com.example.batch_runner.service;

import com.example.batch_runner.domain.SchedulerJob;
import com.example.batch_runner.dto.ScheduleInfoDto;
import com.example.batch_runner.repository.SchedulerJobRepository;
import com.example.batch_runner.scheduler.QuartzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<ScheduleInfoDto> getScheduleInfoList() {
        try {
            List<SchedulerJob> jobList = schedulerJobRepository.findByUseYn("Y");
            List<ScheduleInfoDto> scheduleInfoList = new ArrayList<>();
            for (SchedulerJob job : jobList) {
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
}
