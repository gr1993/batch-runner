package com.example.batch_runner.job.batch;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.domain.RouteStopInfoId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouteStopExcelJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory emf;

    private static final int CHUNK_SIZE = 5000;

    // 엑셀 데이터를 읽어서 변경된 부분만 저장 및 수정
    // 변경 빈도가 낮아서(예: 10% 미만 변경) 차등 갱신이 완전 덮어쓰기보다 효율적이라고 판단함
    // 엑셀 데이터를 모두 읽은 후, DB에서 엑셀에 없는 ID를 찾아 DELETE(별도의 Step)
    @Bean
    public Job routeStopExcelJob(Set<RouteStopInfoId> validRouteStopKeys) {
        return new JobBuilder("routeStopExcelJob", jobRepository)
                .start(routeStopExcelStep(validRouteStopKeys))
                .next(deleteMissingRouteStopsStep(validRouteStopKeys))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        validRouteStopKeys.clear();
                    }
                })
                .build();
    }

    @Bean
    public Step routeStopExcelStep(Set<RouteStopInfoId> validRouteStopKeys) {
        return new StepBuilder("routeStopExcelStep", jobRepository)
                .<RouteStopInfo, RouteStopInfo>chunk(CHUNK_SIZE, transactionManager)
                .reader(routeStopExcelItemReader())
                .processor(routeStopExcelItemProcessor(validRouteStopKeys))
                .writer(routeStopJpaItemWriter())
                .build();
    }

    @Bean
    public ItemReader<RouteStopInfo> routeStopExcelItemReader() {
        PoiItemReader<RouteStopInfo> reader = new PoiItemReader<>();
        reader.setResource(new ClassPathResource("/data/nodeData.xlsx"));
        reader.setRowMapper(excelRowMapper());
        reader.setLinesToSkip(1); // 첫 줄(헤더) 스킵
        return reader;
    }

    private RowMapper<RouteStopInfo> excelRowMapper() {
        return (rs) -> {
            String[] row = rs.getCurrentRow();
            RouteStopInfo routeStop = new RouteStopInfo();
            routeStop.setId(new RouteStopInfoId(row[0], Integer.valueOf(row[2])));
            routeStop.setRouteName(row[1]);
            routeStop.setNodeId(row[3]);
            routeStop.setArsId(row[4]);
            routeStop.setNodeName(row[5]);
            routeStop.setPosX(Double.valueOf(row[6]));
            routeStop.setPosY(Double.valueOf(row[7]));
            return routeStop;
        };
    }

    @Bean
    public ItemProcessor<RouteStopInfo, RouteStopInfo> routeStopExcelItemProcessor(Set<RouteStopInfoId> validRouteStopKeys) {
        return item -> {
            validRouteStopKeys.add(item.getId());
            return item;
        };
    }

    /**
     * JpaItemWriter는 기본 Merge로 동작하여 변경 시 자동 업데이트가 된다.
     */
    @Bean
    public ItemWriter<RouteStopInfo> routeStopJpaItemWriter() {
        JpaItemWriter<RouteStopInfo> delegate = new JpaItemWriter<>();
        delegate.setEntityManagerFactory(emf);

        return items -> {
            log.info("[routeStopExcelStep:writer] : Write 작업 진행 사이즈 {}", items.size());
            delegate.write(items);
        };
    }

    /**
     * @JobScope: Job 실행 동안만 생성되고 끝나면 자동으로 파기됨
     * 엑셀 데이터에 있는 Row의 키를 임시저장하기 위한 용도
     */
    @Bean
    public Set<RouteStopInfoId> validRouteStopKeys() {
        return ConcurrentHashMap.newKeySet();
    }

    /**
     * 엑셀에 없는 항목 삭제 Step
     */
    @Bean
    public Step deleteMissingRouteStopsStep(Set<RouteStopInfoId> validRouteStopKeys) {
        return new StepBuilder("deleteMissingRouteStopsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("[deleteMissingRouteStopsStep] : 엑셀 ROW 수 {} 건", validRouteStopKeys.size());

                    EntityManager em = emf.createEntityManager();
                    em.getTransaction().begin();

                    List<RouteStopInfo> allFromDb = em.createQuery("SELECT r FROM RouteStopInfo r", RouteStopInfo.class).getResultList();

                    for (RouteStopInfo item : allFromDb) {
                        if (!validRouteStopKeys.contains(item.getId())) {
                            em.remove(em.contains(item) ? item : em.merge(item));
                        }
                    }

                    em.getTransaction().commit();
                    em.close();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
