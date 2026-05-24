package com.example.votcha.users.api.dto;

import lombok.Builder;

@Builder
public record CreatorResponse(
        String firstName,
        String lastName,
        String email
) {
}
