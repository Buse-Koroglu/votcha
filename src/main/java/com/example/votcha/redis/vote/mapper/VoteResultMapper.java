package com.example.votcha.redis.vote.mapper;

import com.example.votcha.redis.vote.dto.VoteRedisResultType;
import org.springframework.stereotype.Component;

@Component
public class VoteResultMapper {

    public VoteRedisResultType stringToVoteResultType(String result) {
        return switch (result) {
            case "created" -> VoteRedisResultType.CREATED;
            case "updated" -> VoteRedisResultType.UPDATED;
            case "deleted" -> VoteRedisResultType.DELETED;
            case "not_changed" -> VoteRedisResultType.NOT_CHANGED;
            default -> throw new IllegalStateException("Unknown result: " + result);
        };
    }
}
