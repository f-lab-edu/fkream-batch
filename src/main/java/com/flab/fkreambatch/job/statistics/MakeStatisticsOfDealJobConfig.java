package com.flab.fkreambatch.job.statistics;


import com.flab.fkreambatch.Dto.DealDto;
import com.flab.fkreambatch.entity.DealStatisticsEntity;
import com.flab.fkreambatch.repository.DealStatisticsRepository;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
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
public class MakeStatisticsOfDealJobConfig {

    private final static int CHUNK_SIZE = 5;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DealStatisticsRepository dealStatisticsRepository;
    private final DataSource dataSource;

    public MakeStatisticsOfDealJobConfig(JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory, DealStatisticsRepository dealStatisticsRepository,
        @Qualifier("fkreamDataSource") DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dealStatisticsRepository = dealStatisticsRepository;
        this.dataSource = dataSource;
    }

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
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution,
                    ChunkContext chunkContext) throws Exception {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    List<Integer> itemIds = jdbcTemplate.query("select id from item order by id",
                        new SingleColumnRowMapper<>());

                    LocalDate date = LocalDate.now().minusDays(1);

                    for (int i = 0; i<itemIds.size(); i++) {
                        Integer itemId = itemIds.get(i);
                        if (itemId == 1) {
                            log.info("itemId");
                        }
                        List<DealDto> dealDtos = jdbcTemplate.query(
                            "select * from deal where item_id = ? and status = 'COMPLETION' and deal_type = 'PURCHASE' and trading_day = ? ",
                            new BeanPropertyRowMapper<>(DealDto.class), itemId, date);

                        if (dealDtos.size() == 0) {
                            continue;
                        }

                        int average = (int)dealDtos.stream().mapToInt(dealDto -> dealDto.getPrice())
                            .average().getAsDouble();


                        DealStatisticsEntity dealStatisticsEntity = DealStatisticsEntity.builder().itemId(Long.valueOf(itemId))
                            .date(date).averagePrice(average).build();

                        dealStatisticsRepository.save(dealStatisticsEntity);
                    }
                    return RepeatStatus.FINISHED;
                }
            })
            .build();
    }
}
