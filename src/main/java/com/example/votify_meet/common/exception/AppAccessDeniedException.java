package com.example.votify_meet.common.exception;

public class AppAccessDeniedException extends RuntimeException {
    public AppAccessDeniedException(String message) {
        super(message);
    }
}
