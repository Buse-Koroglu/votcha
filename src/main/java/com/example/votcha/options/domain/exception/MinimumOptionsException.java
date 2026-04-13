package com.example.votcha.options.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class MinimumOptionsException extends BaseException {
    private static final String code = "INVALID_OPTION_COUNT";
    public MinimumOptionsException(String message) {
        super(message,code);
    }
}
