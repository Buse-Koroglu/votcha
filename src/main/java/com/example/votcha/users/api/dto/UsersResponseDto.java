package com.example.votcha.users.api.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UsersResponseDto (String id,
                                String firstName,
                                String lastName,
                                String email,
                                Instant createdAt,
                                Instant updatedAt
) {}
