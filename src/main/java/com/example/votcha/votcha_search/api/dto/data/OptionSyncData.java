package com.example.votcha.votcha_search.api.dto.data;

public record OptionSyncData(
        String id,
        String content,
        Integer voteCount
) {
}
