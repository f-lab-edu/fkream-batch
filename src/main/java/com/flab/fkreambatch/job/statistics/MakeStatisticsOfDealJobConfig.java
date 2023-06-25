package com.flab.fkreambatch.job.statistics;


import com.flab.fkreambatch.kafka.DealStatisticsService;
import com.flab.fkreambatch.repository.DealStatisticsRepository;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MakeStatisticsOfDealJobConfig {

    private final static int CHUNK_SIZE = 5;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DealStatisticsService dealStatisticsService;

    @Bean
    public Job makeStatisticsOfDealJob() throws Exception {
        return this.jobBuilderFactory.get("makeStatisticsOfDealJob")
            .start(makeStatisticsDealStep())
            .build();
    }

    @Bean
    public Step makeStatisticsDealStep() throws Exception {
        return this.stepBuilderFactory.get("makeStatisticsDealStep")
            .allowStartIfComplete(true)
            .tasklet((contribution, chunkContext) -> {
                dealStatisticsService.createDealStatistics();
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
