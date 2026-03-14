package com.example.votify_meet.auth.api.dto;

public record AuthResponseDto(
    String accessToken,
    String refreshToken,
    String message
){}
