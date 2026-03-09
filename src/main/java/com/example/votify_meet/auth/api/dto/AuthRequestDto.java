package com.example.votify_meet.auth.api.dto;

public record AuthRequestDto(
        String email,
        String password
) {
}
