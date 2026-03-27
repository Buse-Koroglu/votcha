package com.example.votify_meet.auth.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

//public class TokenRevokedException extends AppAccessDeniedException {
//    private static final String code = "TOKEN_REVOKED";
//    public TokenRevokedException(String message) {
//        super(message);
//    }
//}

/// todo - should be tested \\\
public class TokenRevokedException extends BaseException {
    private static final String code = "TOKEN_REVOKED";
    public TokenRevokedException(String message) {
        super(message, code);
    }
}

