package com.example.votify_meet.users.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class InvalidPasswordException extends BaseException {
    private static final String code = "INVALID PASSWORD";

    public InvalidPasswordException(String message)
    {
        super(message, code);
    }

}
