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

    private final SearchRepository searchRepository;
    private final KafkaConsumerFactory consumerFactory;

    public void makeRankingOfRealTimeSearchTerms() {
        KafkaConsumer<Object, Object> kafkaConsumer = consumerFactory.createDealStatisticsConsumer();
        Map<String, Integer> countDataByTerms = new HashMap<>();

        boolean isContinue = true;
        try (kafkaConsumer) {
            while (isContinue) {
                ConsumerRecords<Object, Object> records = kafkaConsumer.poll(
                    Duration.ofMillis(100));
                if (records.isEmpty()) {
                    break;
                }
                isContinue = makeCountDataByTerm(countDataByTerms, isContinue, records);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        saveSearchDocument(countDataByTerms);
    }

    private void saveSearchDocument(Map<String, Integer> countDataByTerms) {
        for (Entry<String, Integer> entry : countDataByTerms.entrySet()) {
            SearchDocument searchDocument = SearchDocument.builder()
                .searchWord(entry.getKey())
                .searchCount(entry.getValue())
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)).build();
            searchRepository.save(searchDocument);
        }
    }

    private boolean makeCountDataByTerm(Map<String, Integer> countDataByTerms, boolean isContinue,
        ConsumerRecords<Object, Object> records) {
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        for (ConsumerRecord<Object, Object> record : records) {
            LocalDateTime createdTimeOfRecord = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault());
            if (currentHour.isAfter(createdTimeOfRecord)) {
                 String term = (String)record.value();
                if (countDataByTerms.containsKey(term)) {
                    countDataByTerms.put(term, countDataByTerms.get(term)+1);
                } else {
                    countDataByTerms.put(term, 1);
                }
            }
            else {
                isContinue = false;
                break;
            }
        }
        return isContinue;
    }
}
