package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.options.domain.exception.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {
    private static final String code = "TOKEN_EXPIRED";
    public TokenExpiredException(String message){
        super(message);
    }
}
