package com.capstone.authServer.dto.event;

import java.util.UUID;

import com.capstone.authServer.dto.event.payload.AckJobEventPayload;
import com.capstone.authServer.model.EventType;

public class AckStateUpdateJobEvent implements Event<AckJobEventPayload>{
    private AckJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.ACK_SCAN_PARSE_JOB;


    public AckStateUpdateJobEvent(AckJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public AckStateUpdateJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public AckJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
