package com.example.votcha.auth.api.dto;

public record AuthResponseDto(
    String accessToken,
    String refreshToken,
    String message
){}
