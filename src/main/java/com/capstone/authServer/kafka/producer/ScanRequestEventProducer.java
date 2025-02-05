package com.capstone.authServer.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.exception.KafkaPublishException;

@Service
public class ScanRequestEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.scan-request}")
    private String scanRequestEventTopic;

    public ScanRequestEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(ScanRequestEvent scanRequestEvent) {
        try {
            kafkaTemplate.send(scanRequestEventTopic, scanRequestEvent);
        } catch (Exception e) {
            // Throw custom exception to be handled globally
            throw new KafkaPublishException("Failed to publish ScanRequestEvent to Kafka.", e);
        }
    }
}
