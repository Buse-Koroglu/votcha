package com.example.votify_meet.users.domain.exception;

public class UserIsAlreadyExistsException extends RuntimeException {
    public UserIsAlreadyExistsException(String message) {
        super(message);
    }
}
