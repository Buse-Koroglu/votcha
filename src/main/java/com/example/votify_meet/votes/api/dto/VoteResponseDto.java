package com.example.votify_meet.votes.api.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record VoteResponseDto (
    String id,
    String optionId,
    Instant createdAt
) {
}
