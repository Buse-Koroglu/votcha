package com.example.votify_meet.events.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class EventDeadlinePassedException extends BaseException {
    private static final String code = "EVENT_DEADLINE_PASSED_EXCEPTION";
    public EventDeadlinePassedException(String message) {
        super(message,code);
    }
}
