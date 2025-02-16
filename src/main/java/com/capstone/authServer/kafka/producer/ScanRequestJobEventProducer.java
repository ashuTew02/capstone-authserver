package com.capstone.authServer.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.capstone.authServer.dto.event.ScanRequestJobEvent;
import com.capstone.authServer.exception.KafkaPublishException;
import com.capstone.authServer.model.KafkaTopic; // The new enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ScanRequestJobEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ScanRequestJobEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(ScanRequestJobEvent scanRequestJobEvent) {
        try {
            String json = objectMapper.writeValueAsString(scanRequestJobEvent);
            // Produce to "authserver_jfc"
            kafkaTemplate.send(KafkaTopic.AUTHSERVER_JFC.getTopicName(),
                               scanRequestJobEvent.getEventId(),
                               json);
            System.out.println("1. ScanRequestJobEvent produced at AuthServer, id: " + scanRequestJobEvent.getEventId());
        } catch (JsonProcessingException e) {
            throw new KafkaPublishException("Failed to publish ScanRequestEvent to Kafka.", e);
        }
    }
}
