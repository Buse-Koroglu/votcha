package com.example.votcha.users.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class UserIsAlreadyExistsException extends BaseException {
    private static final String code = "USER_ALREADY_EXISTS";

    public UserIsAlreadyExistsException(String message) {
        super(message,code);
    }
}
