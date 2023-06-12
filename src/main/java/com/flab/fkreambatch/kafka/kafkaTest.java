package com.flab.fkreambatch.kafka;

import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class kafkaTest {

    @KafkaListener(topics = "completeDealPrice", groupId = "${kafka.group-id}")
    public void listenTest(@Payload CompleteDealPriceDto completeDealPriceDto, @Header("time")
        LocalDateTime localDateTime) {
        log.info(completeDealPriceDto.toString());
        log.info(localDateTime.toString());
    }
}
