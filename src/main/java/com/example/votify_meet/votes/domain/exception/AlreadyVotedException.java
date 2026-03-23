package com.example.votify_meet.votes.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class AlreadyVotedException extends BaseException {
    private static final String code = "ALREADY_VOTED";

    public AlreadyVotedException(String message) {
        super(message,code);
    }
}
