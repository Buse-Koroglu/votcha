package com.example.votcha.auth.domain.exception;

import com.example.votcha.options.domain.exception.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {
    private static final String code = "TOKEN_EXPIRED";
    public TokenExpiredException(String message){
        super(message);
    }
}
