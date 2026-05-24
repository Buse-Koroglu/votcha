package com.example.votcha.auth.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class TokenNotFoundException extends BaseException {
    private static final String code = "TOKEN_NOT_FOUND";

    public TokenNotFoundException(String message){
        super(message,code);
    }
}
