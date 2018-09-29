package com.ivansaprykin.testtasks.bostongene.springuserservice.exceptions;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ApiError {

    private String message;
    private Instant timestamp;

    public ApiError(String message) {
        this.message = message;
        this.timestamp = Instant.now();
    }

    public ApiError(String message, Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
