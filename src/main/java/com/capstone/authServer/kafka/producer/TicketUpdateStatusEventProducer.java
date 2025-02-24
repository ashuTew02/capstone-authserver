package com.capstone.authServer.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.event.ticket.TicketUpdateStatusEvent;
import com.capstone.authServer.exception.KafkaPublishException;
import com.capstone.authServer.model.KafkaTopic; // The new enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketUpdateStatusEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TicketUpdateStatusEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(TicketUpdateStatusEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            
            kafkaTemplate.send(KafkaTopic.JOBINGESTION_JFC.getTopicName(),
                               event.getEventId(),
                               json);
            // System.out.println("1. StateUpdateJobEvent produced at AuthServer, id: " + StateUpdateEvent.getEventId());
            // System.out.println("JSON:  " + json);
        } catch (JsonProcessingException e) {
            throw new KafkaPublishException("Failed to publish TicketUpdateStatusEvent to Kafka.", e);
        }
    }
}
