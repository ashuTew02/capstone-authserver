package com.capstone.authServer.dto.event;

import java.util.UUID;

import com.capstone.authServer.dto.event.payload.AckJobEventPayload;
import com.capstone.authServer.model.EventType;

public class AckScanParseJobEvent implements Event<AckJobEventPayload>{
    private AckJobEventPayload payload;
    private String eventId;

    public AckScanParseJobEvent(AckJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public AckScanParseJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return EventType.ACK_SCAN_PARSE_JOB;
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
