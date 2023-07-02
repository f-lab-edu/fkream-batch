package com.flab.fkreambatch.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flab.fkreambatch.elastic.SearchRepository;
import com.flab.fkreambatch.entity.SearchDocument;
import com.flab.fkreambatch.entity.SearchLog;
import com.flab.fkreambatch.repository.SearchLogRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchLogServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private KafkaConsumerFactory consumerFactory;

    @InjectMocks
    private SearchLogService searchLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        searchLogService = new SearchLogService(searchRepository, consumerFactory);
    }

    @Test
    void makeRankingOfRealTimeSearchTerms_shouldSaveSearchDocuments() {
        // Arrange
        KafkaConsumer<Object, Object> kafkaConsumer = mock(KafkaConsumer.class);
        when(consumerFactory.createDealStatisticsConsumer()).thenReturn(kafkaConsumer);

        ConsumerRecords<Object, Object> records = mock(ConsumerRecords.class);
        when(kafkaConsumer.poll(any())).thenReturn(records);
        when(records.isEmpty()).thenReturn(false);

        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime createdAt = currentHour.minusMinutes(30);

        ConsumerRecord<Object, Object> record = mock(ConsumerRecord.class);
        when(record.timestamp()).thenReturn(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli());
        when(record.value()).thenReturn("term1");

        when(records.iterator()).thenReturn(Collections.singletonList(record).iterator());

        searchLogService.makeRankingOfRealTimeSearchTerms();

        verify(searchRepository, times(1)).save(any(SearchDocument.class));
    }
}