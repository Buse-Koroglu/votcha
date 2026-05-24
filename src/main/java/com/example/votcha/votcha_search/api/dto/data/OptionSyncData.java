package com.example.votcha.votcha_search.api.dto.data;

import lombok.Builder;

@Builder
public record OptionSyncData(
        String id,
        String content,
        Integer voteCount,
        boolean isWinner
) {
}
