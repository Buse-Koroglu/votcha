package com.example.votify_meet.auth.api.dto;

public record LoginResponseDto (
    String accessToken,
    String refreshToken,
    String message
){}
