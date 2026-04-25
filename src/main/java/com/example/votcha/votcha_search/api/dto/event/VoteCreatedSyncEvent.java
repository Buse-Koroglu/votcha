package com.example.votcha.votcha_search.api.dto.event;

import java.time.Instant;

public record VoteCreatedSyncEvent(
        String id,
        String optionId,
        String optionContent,
        String voterId,
        String voterFullName,
        String eventId,
        String eventTitle,
        Instant createdAt,
        boolean isWinner
) {
}
