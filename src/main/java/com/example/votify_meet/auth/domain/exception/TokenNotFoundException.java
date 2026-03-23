package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class TokenNotFoundException extends BaseException {
    private static final String code = "TOKEN_NOT_FOUND";

    public TokenNotFoundException(String message){
        super(message,code);
    }
}
