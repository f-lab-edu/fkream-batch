package com.flab.fkreambatch.kafka;

import com.flab.fkreambatch.entity.DealStatisticsEntity;
import com.flab.fkreambatch.repository.DealStatisticsRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;


@Service
@Log4j2
@RequiredArgsConstructor
public class DealStatisticsService {

    private final DealStatisticsRepository dealStatisticsRepository;
    private final KafkaConsumerFactory consumerFactory;

    public void createDealStatistics() {
        KafkaConsumer<Object, Object> kafkaConsumer = consumerFactory.createDealStatisticsConsumer();
        List<CompleteDealPriceDto> completeDealPriceDtos = new ArrayList<>();
        try (kafkaConsumer) {
            while (true) {
                ConsumerRecords<Object, Object> records = kafkaConsumer.poll(
                    Duration.ofMillis(100));
                if (records.isEmpty()) {
                    break;
                }
                for (ConsumerRecord<Object, Object> record : records) {
                    LocalDateTime createdAt = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault());
                    if (LocalDate.now().atStartOfDay().isAfter(createdAt)) {
                        completeDealPriceDtos.add((CompleteDealPriceDto) record.value());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        Map<Long, Double> averagePriceByItemId = completeDealPriceDtos.stream()
            .collect(Collectors.groupingBy(
                CompleteDealPriceDto::getItemId,
                Collectors.averagingInt(CompleteDealPriceDto::getPrice)
            ));
        for (Long itemId : averagePriceByItemId.keySet()) {
            DealStatisticsEntity dealStatisticsEntity =
                DealStatisticsEntity.builder()
                    .itemId(itemId)
                    .averagePrice(averagePriceByItemId.get(itemId).intValue())
                    .date(LocalDate.now().minusDays(1))
                    .build();
            dealStatisticsRepository.save(dealStatisticsEntity);
        }
    }
}
