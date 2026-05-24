package com.example.votcha.users.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class UserNotFoundException extends BaseException {
    private static final String code = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(message,code);
    }
}
