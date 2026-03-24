package com.example.votify_meet.votes.api.dto;

import lombok.Builder;

import java.time.Instant;

// todo - UserSummaryDto might be better instead of voterId
@Builder
public record VoteResponseDto (
    String id,
    String optionId,
    String voterId,
    Instant createdAt,
    Instant updatedAt
) {
}
