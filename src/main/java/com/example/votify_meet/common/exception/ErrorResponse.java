package com.example.votify_meet.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;   // code
    private String message;
    private String path;
}
