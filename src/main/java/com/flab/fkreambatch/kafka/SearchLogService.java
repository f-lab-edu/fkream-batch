package com.flab.fkreambatch.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flab.fkreambatch.entity.SearchLog;
import com.flab.fkreambatch.repository.SearchLogRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    @KafkaListener(topics = "${kafka.topic.search-log}"
        , groupId = "${kafka.group-id}")
    public void listenWithHeaders(ConsumerRecord<String, String> record)
        throws JsonProcessingException {
        String content = record.value();
        LocalDateTime createdAt = ChangeTimestampToLocalDateTime(record.timestamp());
        SearchLog searchLog = new SearchLog(content, createdAt);
        searchLogRepository.insert(searchLog);
    }

    private LocalDateTime ChangeTimestampToLocalDateTime(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
