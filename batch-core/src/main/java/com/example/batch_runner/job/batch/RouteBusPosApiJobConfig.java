package com.example.batch_runner.job.batch;

import com.example.batch_runner.domain.RouteBusPos;
import com.example.batch_runner.external.dto.BusPosition;
import com.example.batch_runner.external.dto.ServiceResult;
import com.example.batch_runner.repository.RouteBusPosRepository;
import com.example.batch_runner.util.RestApiClient;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouteBusPosApiJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory emf;
    private final RouteBusPosRepository routeBusPosRepository;
    private final RestApiClient restApiClient;

    @Value("${app.serviceKey}")
    private String serviceKey;

    private static final int CHUNK_SIZE = 1;

    //"전체 삭제 후 재삽입" 전략 방식으로 구현 예정
    //데이터량이 작고, 외부 데이터 소스(API)가 전체 상태를 매번 제공하는 경우라 적합하다고 판단
    @Bean
    public Job routeBusPosApiJob() {
        return new JobBuilder("routeBusPosApiJob", jobRepository)
                .start(routeBusPosApiStep())
                .build();
    }

    @Bean
    public Step routeBusPosApiStep() {
        return new StepBuilder("routeBusPosApiStep", jobRepository)
                .<String, List<RouteBusPos>>chunk(CHUNK_SIZE, transactionManager)
                .reader(routeBusPosApiReader())
                .processor(routeBusPosApiProcessor())
                .writer(routeBusPosApiWriter())
                .build();
    }

    /**
     * 노선 하나씩 처리하기 때문에 Cursor 방식이 적합하다.
     */
    @Bean
    public JpaCursorItemReader<String> routeBusPosApiReader() {
        JpaCursorItemReader<String> reader = new JpaCursorItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("SELECT f.routeId FROM FavoriteRoute f GROUP BY f.routeId");
        reader.setName("routeBusPosApiItemReader");
        reader.setSaveState(true);
        return reader;
    }

    /**
     * 하나의 노선에 대해서 버스 위치 정보를 삭제 후 저장할 버스 위치를 외부 API에서 가져옴
     */
    @Bean
    public ItemProcessor<String, List<RouteBusPos>> routeBusPosApiProcessor() {
        return routeId -> {

            // 1. 삭제 작업
            routeBusPosRepository.deleteByRouteId(routeId);

            // 2. API 호출하여 routeId에 해당하는 버스 위치 정보를 가져옴
            String url = String.format(
                "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRtid?serviceKey=%s&busRouteId=%s",
                serviceKey, routeId
            );
            ServiceResult response = restApiClient.get(url, ServiceResult.class);
            List<BusPosition> busPositions = response.getMsgBody().getItemList();
            List<RouteBusPos> result = new ArrayList<>();
            for (BusPosition busPosition : busPositions) {
                RouteBusPos busPosInfo = new RouteBusPos();
                busPosInfo.setRouteId(routeId);
                busPosInfo.setVehId(busPosition.getVehId());
                busPosInfo.setPlainNo(busPosition.getPlainNo());
                busPosInfo.setCongetion(busPosition.getCongetion());
                busPosInfo.setPosX(busPosition.getGpsX());
                busPosInfo.setPosY(busPosition.getGpsY());
                result.add(busPosInfo);
            }
            return result;
        };
    }

    @Bean
    public ItemWriter<List<RouteBusPos>> routeBusPosApiWriter() {
        return items -> {
            for (List<RouteBusPos> routeBusPosList : items) {
                routeBusPosRepository.saveAll(routeBusPosList); // JPA 일괄 저장
            }
        };
    }
}
