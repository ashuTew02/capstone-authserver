package com.capstone.authServer.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.event.ticket.TicketCreateEvent;
import com.capstone.authServer.exception.KafkaPublishException;
import com.capstone.authServer.model.KafkaTopic; // The new enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketCreateEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TicketCreateEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(TicketCreateEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            
            kafkaTemplate.send(KafkaTopic.JOBINGESTION_JFC.getTopicName(),
                               event.getEventId(),
                               json);
        } catch (JsonProcessingException e) {
            throw new KafkaPublishException("Failed to publish TicketCreateEvent to Kafka.", e);
        }
    }
}
