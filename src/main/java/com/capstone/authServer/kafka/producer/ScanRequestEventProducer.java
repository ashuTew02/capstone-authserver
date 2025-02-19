package com.capstone.authServer.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.event.ScanRequestEvent;
import com.capstone.authServer.exception.KafkaPublishException;
import com.capstone.authServer.model.KafkaTopic; // The new enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ScanRequestEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ScanRequestEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(ScanRequestEvent scanRequestEvent) {
        try {
            String json = objectMapper.writeValueAsString(scanRequestEvent);
            kafkaTemplate.send(KafkaTopic.JOBINGESTION_JFC.getTopicName(),
                               scanRequestEvent.getEventId(),
                               json);
            System.out.println("1. ScanRequestEvent produced at AuthServer, id: " + scanRequestEvent.getEventId());
        } catch (JsonProcessingException e) {
            throw new KafkaPublishException("Failed to publish ScanRequestEvent to Kafka.", e);
        }
    }
}
