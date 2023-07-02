package com.flab.fkreambatch.kafka;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerFactory {

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapServers;
    @Value("${kafka.group-id}")
    private String groupId;


    public KafkaConsumer<Object, Object> createDealStatisticsConsumer() {
        Properties props = createDealStatisticsConsumerProperty();
        KafkaConsumer<Object, Object> kafkaConsumer = new KafkaConsumer<>(props);
        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(KafkaTopic.DEAL);
        assignTopicPartitions(kafkaConsumer, partitionInfos, KafkaTopic.DEAL);

        Map<TopicPartition, OffsetAndTimestamp> offsets = getOffsetsForPrevious(
            kafkaConsumer, partitionInfos, KafkaTopic.DEAL, getPreviousDayStartMillis());

        setConsumerOffsetsByTimestamp(kafkaConsumer, offsets);
        return kafkaConsumer;
    }

    public KafkaConsumer<Object, Object> createSearchLogConsumer() {
        Properties props = createDealStatisticsConsumerProperty();
        KafkaConsumer<Object, Object> kafkaConsumer = new KafkaConsumer<>(props);
        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(KafkaTopic.SEARCH_LOG);
        assignTopicPartitions(kafkaConsumer, partitionInfos, KafkaTopic.SEARCH_LOG);

        Map<TopicPartition, OffsetAndTimestamp> offsets = getOffsetsForPrevious(
            kafkaConsumer, partitionInfos, KafkaTopic.SEARCH_LOG, getPreviousHoursStartMillis(1));

        setConsumerOffsetsByTimestamp(kafkaConsumer, offsets);
        return kafkaConsumer;
    }

    private void setConsumerOffsetsByTimestamp(KafkaConsumer<Object, Object> kafkaConsumer,
        Map<TopicPartition, OffsetAndTimestamp> offsets) {
        for (Entry<TopicPartition, OffsetAndTimestamp> entry : offsets.entrySet()) {
            TopicPartition topicPartition = entry.getKey();
            OffsetAndTimestamp offsetAndTimestamp = entry.getValue();

            if (offsetAndTimestamp != null) {
                kafkaConsumer.seek(topicPartition, offsetAndTimestamp.offset());
            }
        }
    }

    private Map<TopicPartition, OffsetAndTimestamp> getOffsetsForPrevious(
        KafkaConsumer<Object, Object> kafkaConsumer, List<PartitionInfo> partitionInfos,
        String topic, long timestamp) {
        HashMap<TopicPartition, Long> timestamps = new HashMap<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            timestamps.put(new TopicPartition(topic, partitionInfo.partition()), timestamp);
        }
        Map<TopicPartition, OffsetAndTimestamp> offsets = kafkaConsumer.offsetsForTimes(
            timestamps);
        return offsets;
    }

    private long getPreviousDayStartMillis() {
        return LocalDate
            .now()
            .atStartOfDay()
            .minusDays(1)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    }

    private long getPreviousHoursStartMillis(int Hours) {
        return LocalDateTime
            .now()
            .truncatedTo(ChronoUnit.HOURS)
            .minusHours(Hours)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    }

    private void assignTopicPartitions(KafkaConsumer<Object, Object> kafkaConsumer,
        List<PartitionInfo> partitionInfos, String topic) {
        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            topicPartitions.add(new TopicPartition(topic, partitionInfo.partition()));
        }
        kafkaConsumer.assign(topicPartitions);
    }


    private Properties createDealStatisticsConsumerProperty() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }
}
