package com.capstone.authServer.dto.event.job;

import java.util.UUID;

import com.capstone.authServer.dto.event.Event;
import com.capstone.authServer.dto.event.payload.job.RunbookTriggerJobEventPayload;
import com.capstone.authServer.model.EventType;

public class RunbookTriggerJobEvent implements Event<RunbookTriggerJobEventPayload> {
    private RunbookTriggerJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.RUNBOOK_TRIGGER_JOB;

    public RunbookTriggerJobEvent(RunbookTriggerJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public RunbookTriggerJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public RunbookTriggerJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
