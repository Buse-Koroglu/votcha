package com.example.votcha.votcha_search.api.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record EventSyncResponse(
        String status,
        String message,
        long totalDbCount,
        long syncedCount,
        Instant timestamp
) {
}
