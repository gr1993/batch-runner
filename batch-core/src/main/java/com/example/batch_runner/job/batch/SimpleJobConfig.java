package com.example.batch_runner.job.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job simpleJob() {
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep())
                .build();
    }

    @Bean
    public Step simpleStep() {
        return new StepBuilder("simpleStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("✅ Tasklet 실행되었음!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
