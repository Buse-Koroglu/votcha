package com.example.votify_meet.common.exception;

public class AppAccessDeniedException extends BaseException {
    private static final String code = "ACCESS_DENIED";
    public AppAccessDeniedException(String message) {
        super(message,code);
    }
}
