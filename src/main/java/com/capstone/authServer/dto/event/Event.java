package com.capstone.authServer.dto.event;

import com.capstone.authServer.model.EventType;

public interface Event<T> {
    EventType getType();
    T getPayload();
    String getEventId();
}
