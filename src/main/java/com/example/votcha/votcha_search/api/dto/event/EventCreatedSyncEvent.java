package com.example.votcha.votcha_search.api.dto.event;

import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;

import java.time.Instant;
import java.util.List;

public record EventCreatedSyncEvent(
        String id,
        String title,
        String description,
        Instant deadline,
        Instant createdAt,
        String type,
        String status,
        String creatorName,
        String creatorEmail,
        Long totalVoteCount,
        List<OptionSyncData> options
) {
}
