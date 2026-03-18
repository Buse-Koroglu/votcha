package com.example.votify_meet.auth.api.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        String message
) {
}
