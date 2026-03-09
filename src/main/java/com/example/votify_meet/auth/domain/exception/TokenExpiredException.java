package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.options.domain.exception.UnauthorizedException;

public class TokenExpiredException extends UnauthorizedException {
    public TokenExpiredException(String message){
        super(message);
    }
}
