package com.example.votcha.auth.api.dto;

public record AuthRequestDto(
        String email,
        String password
) {
}
