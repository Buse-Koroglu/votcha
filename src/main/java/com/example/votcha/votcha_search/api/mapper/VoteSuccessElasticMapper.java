package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.votcha_search.api.dto.response.UserVoteSuccessSyncResponse;
import org.springframework.stereotype.Component;

@Component
public class VoteSuccessElasticMapper{
    public UserVoteSuccessSyncResponse buildResponse(String voterId, long total, long success) {
        double percentage = 0.0;

        if (total > 0) {
            percentage = ((double) success / total) * 100;
            percentage = Math.round(percentage * 100.0) / 100.0;
        }
    return UserVoteSuccessSyncResponse.builder()
            .voterId(voterId)
            .totalVotes(total)
            .successCount(success)
            .winnerPercentage(percentage)
            .build();
}}
