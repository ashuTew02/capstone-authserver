package com.capstone.authServer.exception;

public class KafkaPublishException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public KafkaPublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaPublishException(String message) {
        super(message);
    }
}
