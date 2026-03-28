package com.example.votcha.users.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class InvalidPasswordException extends BaseException {
    private static final String code = "INVALID PASSWORD";

    public InvalidPasswordException(String message)
    {
        super(message, code);
    }

}
