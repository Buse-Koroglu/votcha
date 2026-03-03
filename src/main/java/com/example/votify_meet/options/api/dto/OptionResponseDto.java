package com.example.votify_meet.options.api.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record OptionResponseDto(
    String id,
    String content,
    Instant createdAt,
    Instant updatedAt
)
{}
