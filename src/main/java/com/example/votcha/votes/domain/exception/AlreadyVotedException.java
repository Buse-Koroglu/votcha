package com.example.votcha.votes.domain.exception;

import com.example.votcha.common.exception.BaseException;

public class AlreadyVotedException extends BaseException {
    private static final String code = "ALREADY_VOTED";

    public AlreadyVotedException(String message) {
        super(message,code);
    }
}
