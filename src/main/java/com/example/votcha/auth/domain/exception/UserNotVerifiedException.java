package com.example.votcha.auth.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class UserNotVerifiedException extends BaseException {
    private static final String code = "USER_NOT_VERIFIED";
    public UserNotVerifiedException(String message) {
        super(message,code);
    }
}
