package com.example.votify_meet.options.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class UnauthorizedException extends BaseException {
    private static final String code = "UNAUTHORIZED";
    public UnauthorizedException(String message){
        super(message,code);
    }
}
