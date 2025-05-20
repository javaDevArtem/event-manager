package com.amir.eventmanager.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EventSender {
    private final static Logger log = LoggerFactory.getLogger(EventSender.class);

    private final KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate;

    public EventSender(KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEven(EventChangeKafkaMessage kafkaMessage) {
        log.info("Sending event: event={}", kafkaMessage);
        CompletableFuture<SendResult<Long, EventChangeKafkaMessage>> result = kafkaTemplate.send(
                "events-topic",
                kafkaMessage.getEventId(),
                kafkaMessage
        );
        result.thenAccept(sendResult -> {
            log.info("Send successful");
        });
    }
}
