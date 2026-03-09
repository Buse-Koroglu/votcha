package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.common.exception.AccessDeniedException;

public class TokenRevokedException extends AccessDeniedException {
    public TokenRevokedException(String message) {
        super(message);
    }
}
