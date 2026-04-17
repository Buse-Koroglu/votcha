package com.example.votcha.users.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class AdminActionException extends BaseException {
    private static final String code = "INVALID ADMIN_ACTION_FORBIDDEN";

    public AdminActionException(String message)
    {
        super(message, code);
    }
}
