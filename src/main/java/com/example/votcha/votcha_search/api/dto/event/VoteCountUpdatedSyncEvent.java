package com.example.votcha.votcha_search.api.dto.event;

import com.example.votcha.votcha_search.api.dto.data.OptionSyncData;

import java.util.List;

public record VoteCountUpdatedSyncEvent(
        String eventId,
        Long currentTotalVoteCount,
        List<OptionSyncData> options
) {
}
