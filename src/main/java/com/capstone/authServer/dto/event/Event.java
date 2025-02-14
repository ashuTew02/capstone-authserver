package com.capstone.authServer.dto.event;

import com.capstone.authServer.model.EventType;

public interface Event<T> {
    String getEventId();
    EventType getType();
    T getPayload();
}
