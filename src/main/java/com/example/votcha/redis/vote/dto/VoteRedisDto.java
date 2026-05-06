package com.example.votcha.redis.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteRedisDto {
    private String voteId;
    private String optionId;
    private String userId;
}
