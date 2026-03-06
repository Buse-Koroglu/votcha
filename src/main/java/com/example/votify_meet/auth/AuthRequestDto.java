package com.example.votify_meet.auth;

public record AuthRequestDto(
        String email,
        String password
) {
}
