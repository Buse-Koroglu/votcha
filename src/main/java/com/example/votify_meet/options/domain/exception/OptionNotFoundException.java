package com.example.votify_meet.options.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class OptionNotFoundException extends BaseException {
    private static final String code = "OPTION_NOT_FOUND";
    public OptionNotFoundException(String message) {
        super(message,code);
    }
}
