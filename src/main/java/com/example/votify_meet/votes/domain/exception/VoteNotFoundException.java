package com.example.votify_meet.votes.domain.exception;

import com.example.votify_meet.common.exception.BaseException;

public class VoteNotFoundException extends BaseException {
    private static final String code = "VOTE_NOT_FOUND";

    public VoteNotFoundException(String message) {
        super(message,code);
    }
}
