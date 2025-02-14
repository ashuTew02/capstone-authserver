package com.capstone.authServer.dto.event;

import java.util.UUID;

import com.capstone.authServer.dto.event.payload.ScanRequestJobEventPayload;
import com.capstone.authServer.model.EventType;

public final class ScanRequestJobEvent implements Event<ScanRequestJobEventPayload> {
    private ScanRequestJobEventPayload payload;
    private String eventId;

    public ScanRequestJobEvent(ScanRequestJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    
    public ScanRequestJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }


    @Override
    public EventType getType() {
        return EventType.SCAN_REQUEST_JOB;
    }

    @Override
    public ScanRequestJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
