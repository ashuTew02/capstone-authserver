package com.capstone.authServer.kafka.producer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.event.ScanRequestEvent;

@Service
public class ScanRequestEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.scan-request}")
    private String scanRequestEventTopic;

    public ScanRequestEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(ScanRequestEvent scanRequestEvent) {
        kafkaTemplate.send(scanRequestEventTopic, scanRequestEvent);
    }
}
