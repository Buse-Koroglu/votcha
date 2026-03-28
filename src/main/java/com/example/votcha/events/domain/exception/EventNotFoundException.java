package com.example.votcha.events.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class EventNotFoundException extends BaseException {
    private static final String code = "EVENT_NOT_FOUND";
    public EventNotFoundException(String message) {
        super(message,code);
    }
}
