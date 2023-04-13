package com.example.task2.utils;

import java.time.LocalDateTime;

public class ErrorResponseBody {

    private final String timestamp;

    private final String error;

    private final String path;

    public ErrorResponseBody(String errorMessage, String path) {
        this.error = errorMessage;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
