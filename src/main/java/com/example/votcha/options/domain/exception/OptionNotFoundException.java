package com.example.votcha.options.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class OptionNotFoundException extends BaseException {
    private static final String code = "OPTION_NOT_FOUND";
    public OptionNotFoundException(String message) {
        super(message,code);
    }
}
