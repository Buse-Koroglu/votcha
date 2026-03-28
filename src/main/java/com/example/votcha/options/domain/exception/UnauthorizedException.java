package com.example.votcha.options.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class UnauthorizedException extends BaseException {
    private static final String code = "UNAUTHORIZED";
    public UnauthorizedException(String message){
        super(message,code);
    }
}
