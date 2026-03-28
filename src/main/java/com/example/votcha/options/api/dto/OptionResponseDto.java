package com.example.votcha.options.api.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record OptionResponseDto(
    String id,
    String content,
    Instant createdAt,
    Instant updatedAt,
    Integer voteCount
)
{}
