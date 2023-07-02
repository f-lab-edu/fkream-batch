package com.flab.fkreambatch.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.flab.fkreambatch.entity.DealStatisticsEntity;
import com.flab.fkreambatch.repository.DealStatisticsRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class DealStatisticsServiceTest {

    @Mock
    private DealStatisticsRepository dealStatisticsRepository;

    @Mock
    private KafkaConsumerFactory consumerFactory;

    @Mock
    private KafkaConsumer<Object, Object> kafkaConsumer;

    @Captor
    private ArgumentCaptor<DealStatisticsEntity> dealStatisticsEntityCaptor;

    @InjectMocks
    private DealStatisticsService dealStatisticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createDealStatistics() {
        List<CompleteDealPriceDto> completeDealPriceDtos = new ArrayList<>();
        completeDealPriceDtos.add(new CompleteDealPriceDto(1L, 10));
        completeDealPriceDtos.add(new CompleteDealPriceDto(1L, 20));
        completeDealPriceDtos.add(new CompleteDealPriceDto(2L, 15));

        ConsumerRecord<Object, Object> record1 = new ConsumerRecord<>("topic", 0, 0L, "key",
            completeDealPriceDtos.get(0));
        ConsumerRecord<Object, Object> record2 = new ConsumerRecord<>("topic", 0, 0L, "key",
            completeDealPriceDtos.get(1));
        ConsumerRecord<Object, Object> record3 = new ConsumerRecord<>("topic", 0, 0L, "key",
            completeDealPriceDtos.get(2));

        ConsumerRecords<Object, Object> records = new ConsumerRecords<>(Map.of(
            new TopicPartition("topic", 0), List.of(record1, record2, record3)
        ));

        when(consumerFactory.createDealStatisticsConsumer()).thenReturn(kafkaConsumer);
        when(kafkaConsumer.poll(Duration.ofMillis(100))).thenReturn(records);

        //when
        dealStatisticsService.createDealStatistics();

        //then
        verify(kafkaConsumer, times(2)).poll(Duration.ofMillis(100));
        verify(dealStatisticsRepository, times(2)).save(dealStatisticsEntityCaptor.capture());

        List<DealStatisticsEntity> capturedDealStatistics = dealStatisticsEntityCaptor.getAllValues();
        assertEquals(2, capturedDealStatistics.size());

        DealStatisticsEntity dealStatisticsEntity1 = capturedDealStatistics.get(0);
        assertEquals(1L, dealStatisticsEntity1.getItemId());
        assertEquals(15, dealStatisticsEntity1.getAveragePrice());
        assertEquals(LocalDate.now().minusDays(1), dealStatisticsEntity1.getDate());

        DealStatisticsEntity dealStatisticsEntity2 = capturedDealStatistics.get(1);
        assertEquals(2L, dealStatisticsEntity2.getItemId());
        assertEquals(15, dealStatisticsEntity2.getAveragePrice());
        assertEquals(LocalDate.now().minusDays(1), dealStatisticsEntity2.getDate());
    }
}