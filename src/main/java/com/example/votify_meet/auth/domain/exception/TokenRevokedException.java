package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.common.exception.AppAccessDeniedException;

public class TokenRevokedException extends AppAccessDeniedException {
    public TokenRevokedException(String message) {
        super(message);
    }
}
