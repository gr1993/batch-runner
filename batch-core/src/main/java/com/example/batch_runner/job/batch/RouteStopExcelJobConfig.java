package com.example.batch_runner.job.batch;

import com.example.batch_runner.domain.RouteStopInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouteStopExcelJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    // 엑셀 데이터를 읽어서 변경된 부분만 저장 및 수정
    // 변경 빈도가 낮아서(예: 10% 미만 변경) 차등 갱신이 완전 덮어쓰기보다 효율적이라고 판단함
    // 엑셀 데이터를 모두 읽은 후, DB에서 엑셀에 없는 ID를 찾아 DELETE(별도의 Step)
    @Bean
    public Job routeStopExcelJob() {
        return new JobBuilder("routeStopExcelJob", jobRepository)
                .start(routeStopExcelStep())
                .build();
    }

    @Bean
    public Step routeStopExcelStep() {
        return new StepBuilder("routeStopExcelStep", jobRepository)
                .<RouteStopInfo, RouteStopInfo>chunk(1000, transactionManager)
                .reader(routeStopExcelItemReader())
                .processor(routeStopExcelItemProcessor())
                .writer(routeStopExcelItemWriter())
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
            routeStop.setRouteId(row[0]);
            routeStop.setRouteName(row[1]);
            routeStop.setNodeSeq(Integer.valueOf(row[2]));
            routeStop.setNodeId(row[3]);
            routeStop.setArsId(row[4]);
            routeStop.setNodeName(row[5]);
            routeStop.setPosX(Double.valueOf(row[6]));
            routeStop.setPosY(Double.valueOf(row[7]));
            return routeStop;
        };
    }

    @Bean
    public ItemProcessor<RouteStopInfo, RouteStopInfo> routeStopExcelItemProcessor() {
        return item -> {
          return item;
        };
    }

    @Bean
    public ItemWriter<RouteStopInfo> routeStopExcelItemWriter() {
        return chunk -> {
            log.info("[ExcelWriter] {}개 쓰기", chunk.size());
        };
    }
}
