package com.flab.fkreambatch.job.statistics;

import static org.junit.jupiter.api.Assertions.*;

import com.flab.fkreambatch.TestBatchConfig;
import com.flab.fkreambatch.config.FkreamDataSourceConfig;
import com.flab.fkreambatch.dataSource.ExternalDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBatchTest
@SpringBootTest
@ContextConfiguration(classes = {FkreamDataSourceConfig.class, TestBatchConfig.class,
    MakeStatisticsOfDealJobConfig.class, ExternalDataSource.class})
class MakeStatisticsOfDealJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void makeStatisticsOfDealTest() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(
            "makeStatisticsDealStep");
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}