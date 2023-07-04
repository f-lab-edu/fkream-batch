package com.flab.fkreambatch.kafka;

import com.flab.fkreambatch.elastic.SearchRepository;
import com.flab.fkreambatch.entity.SearchDocument;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class SearchLogService {

    private static final Duration POLLING_DURATION = Duration.ofMillis(100);

    private final SearchRepository searchRepository;
    private final KafkaConsumerFactory consumerFactory;

    public void makeRankingOfRealTimeSearchTerms() {
        try (KafkaConsumer<Object, Object> kafkaConsumer = consumerFactory.createSearchLogConsumer()) {
            Map<String, Integer> termCounts = countTermsFromStream(kafkaConsumer);
            saveSearchDocuments(termCounts);
        } catch (Exception e) {
            log.error("Error during processing search terms", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, Integer> countTermsFromStream(KafkaConsumer<Object, Object> kafkaConsumer) {
        Map<String, Integer> termCounts = new HashMap<>();

        boolean shouldContinue = true;
        while (shouldContinue) {
            ConsumerRecords<Object, Object> records = kafkaConsumer.poll(POLLING_DURATION);
            if (records.isEmpty()) {
                break;
            }
            shouldContinue = accumulateTermCounts(termCounts, records);
        }
        return termCounts;
    }

    private boolean accumulateTermCounts(Map<String, Integer> termCounts, ConsumerRecords<Object, Object> records) {
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        for (ConsumerRecord<Object, Object> record : records) {
            LocalDateTime recordCreatedTime = convertTimestampToLocalDateTime(record.timestamp());

            if (isRecordOutOfTimeRange(currentHour, recordCreatedTime)) {
                updateTermCount(termCounts, (String) record.value());
            } else {
                return false;
            }
        }
        return true;
    }

    private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    private boolean isRecordOutOfTimeRange(LocalDateTime currentHour, LocalDateTime recordCreatedTime) {
        return currentHour.isAfter(recordCreatedTime);
    }

    private void updateTermCount(Map<String, Integer> termCounts, String term) {
        termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
    }

    private void saveSearchDocuments(Map<String, Integer> termCounts) {
        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        termCounts.forEach((term, count) -> saveSearchDocument(term, count, createdAt));
    }

    private void saveSearchDocument(String term, Integer count, LocalDateTime createdAt) {
        SearchDocument searchDocument = SearchDocument.builder()
            .searchWord(term)
            .searchCount(count)
            .createdAt(createdAt)
            .build();

        searchRepository.save(searchDocument);
    }

}
