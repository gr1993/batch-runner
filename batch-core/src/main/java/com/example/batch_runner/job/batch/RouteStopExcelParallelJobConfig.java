package com.example.batch_runner.job.batch;

import com.example.batch_runner.domain.RouteStopInfo;
import com.example.batch_runner.domain.RouteStopInfoId;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RouteStopExcelParallelJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 5000;


    @Bean
    public Job routeStopExcelParallelJob(@Qualifier("deleteMissingRouteStopsStep") Step deleteMissingRouteStopsStep, Set<RouteStopInfoId> validRouteStopKeys) {
        return new JobBuilder("routeStopExcelParallelJob", jobRepository)
                .start(routeStopExcelParallelStep(validRouteStopKeys))
                .next(deleteMissingRouteStopsStep) // 오래 안걸리는 Step은 재사용
                .build();
    }

    @Bean
    public Step routeStopExcelParallelStep(Set<RouteStopInfoId> validRouteStopKeys) {
        return new StepBuilder("routeStopExcelParallelStep", jobRepository)
                .<RouteStopInfo, RouteStopInfo>chunk(CHUNK_SIZE, transactionManager)
                .reader(excelParallelItemReader())
                .processor(excelParallelItemProcessor(validRouteStopKeys))
                .writer(excelParallelItemWriter())
                .build();
    }

    @Bean
    public ItemReader<RouteStopInfo> excelParallelItemReader() {
        PoiItemReader<RouteStopInfo> reader = new PoiItemReader<>();
        reader.setResource(new ClassPathResource("/data/nodeData.xlsx"));
        reader.setRowMapper(excelRowMapper());
        reader.setLinesToSkip(1);
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
    public ItemProcessor<RouteStopInfo, RouteStopInfo> excelParallelItemProcessor(Set<RouteStopInfoId> validRouteStopKeys) {
        return item -> {
            validRouteStopKeys.add(item.getId());
            return item;
        };
    }

    @Bean
    public ItemWriter<RouteStopInfo> excelParallelItemWriter() {
        JdbcBatchItemWriter<RouteStopInfo> delegate = new JdbcBatchItemWriter<>();
        delegate.setDataSource(dataSource);

        String sql = "INSERT INTO route_stop_info"
                + "(route_id, route_name, node_seq, node_id, ars_id, node_name, pos_x, pos_y)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                + "ON CONFLICT (route_id, node_seq) DO UPDATE SET "
                + " route_name = EXCLUDED.route_name, "
                + " node_id = EXCLUDED.node_id, "
                + " ars_id = EXCLUDED.ars_id, "
                + " node_name = EXCLUDED.node_name, "
                + " pos_x = EXCLUDED.pos_x, "
                + " pos_y = EXCLUDED.pos_y";

        delegate.setSql(sql);
        delegate.setItemPreparedStatementSetter((item, ps) -> {
            ps.setString(1, item.getId().getRouteId());
            ps.setString(2, item.getRouteName());
            ps.setInt(3, item.getId().getNodeSeq());
            ps.setString(4, item.getNodeId());
            ps.setString(5, item.getArsId());
            ps.setString(6, item.getNodeName());
            ps.setDouble(7, item.getPosX());
            ps.setDouble(8, item.getPosY());
        });

        return items -> {
            log.info("[routeStopExcelStep:writer] : Write 작업 진행 사이즈 {}", items.size());
            delegate.write(items);
        };
    }
}
