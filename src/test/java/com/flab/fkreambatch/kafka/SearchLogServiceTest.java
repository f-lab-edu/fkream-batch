package com.flab.fkreambatch.kafka;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flab.fkreambatch.entity.SearchLog;
import com.flab.fkreambatch.repository.SearchLogRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    private SearchLogRepository searchLogRepository;

    @InjectMocks
    private SearchLogService searchLogService;

    @Captor
    private ArgumentCaptor<SearchLog> searchLogCaptor;

    public SearchLogServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListenWithHeaders() throws JsonProcessingException {
        String content = "search log content";
        long timestamp = System.currentTimeMillis();
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key",
            content);

        searchLogService.listenWithHeaders(record);

        Mockito.verify(searchLogRepository).insert(searchLogCaptor.capture());
        SearchLog capturedSearchLog = searchLogCaptor.getValue();
        assertEquals(content, capturedSearchLog.getContent());

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime expectedCreatedAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        assertEquals(expectedCreatedAt, capturedSearchLog.getCreatedAt());
    }
}