package com.example.votcha.votcha_search.api.dto.response;

import lombok.Builder;

@Builder
public record UserVoteSuccessSyncResponse(
        String voterId,
        long totalVotes,
        long successCount,
        double winnerPercentage
) {
}
