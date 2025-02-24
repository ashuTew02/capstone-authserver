package com.capstone.authServer.dto.event.ticket.job;
import java.util.UUID;

import com.capstone.authServer.dto.event.Event;
import com.capstone.authServer.dto.event.payload.job.TicketCreateJobEventPayload;
import com.capstone.authServer.model.EventType;

public final class TicketCreateJobEvent implements Event<TicketCreateJobEventPayload> {
    private TicketCreateJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.TICKET_CREATE_JOB;


    public TicketCreateJobEvent(TicketCreateJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public TicketCreateJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public TicketCreateJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
