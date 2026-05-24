package com.example.votcha.events.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class EventDeadlinePassedException extends BaseException {
    private static final String code = "EVENT_DEADLINE_PASSED_EXCEPTION";
    public EventDeadlinePassedException(String message) {
        super(message,code);
    }
}
