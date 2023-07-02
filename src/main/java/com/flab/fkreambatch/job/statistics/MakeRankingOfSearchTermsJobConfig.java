package com.flab.fkreambatch.job.statistics;

import com.flab.fkreambatch.kafka.SearchLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MakeRankingOfSearchTermsJobConfig {

    private final static int CHUNK_SIZE = 5;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SearchLogService searchLogService;

    @Bean
    public Job makeRankingOfSearchTermsJob() throws Exception {
        return this.jobBuilderFactory.get("makeStatisticsOfDealJob")
            .start(makeRankingOfSearchTermsStep())
            .build();
    }

    @Bean
    public Step makeRankingOfSearchTermsStep() throws Exception {
        return this.stepBuilderFactory.get("makeStatisticsDealStep")
            .allowStartIfComplete(true)
            .tasklet((contribution, chunkContext) -> {
                searchLogService.makeRankingOfRealTimeSearchTerms();
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
