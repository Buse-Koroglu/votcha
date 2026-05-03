package com.example.votcha.redis.vote.service;

import com.example.votcha.redis.vote.dto.VoteRedisResultType;
import com.example.votcha.redis.vote.executor.VoteRedisLuaExecutor;
import com.example.votcha.redis.vote.mapper.VoteResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteRedisService {

    private final StringRedisTemplate redisTemplate;
    private final VoteRedisLuaExecutor executor;
    private final VoteResultMapper mapper;

    private String buildUserKey(String userId, String eventId) {
        return "vote:user:" + userId + ":event:" + eventId;
    }

    private String buildEventKey(String eventId) {
        return "vote:event:" + eventId;
    }

    public VoteRedisResultType createVote(String userId, String eventId, String optionId) {

        String userKey = buildUserKey(userId, eventId);

        Boolean exists = redisTemplate.hasKey(userKey);
        if (Boolean.TRUE.equals(exists)) {
            return VoteRedisResultType.NOT_CHANGED;
        }

        String result = executor.executeVoteScript(List.of(userKey,buildEventKey(eventId)),optionId);
        return mapper.stringToVoteResultType(result);
    }


    public VoteRedisResultType updateVote(String userId, String eventId, String optionId) {
        String result = executor.executeVoteScript(List.of(buildUserKey(userId,eventId),buildEventKey(eventId)),optionId);
        return mapper.stringToVoteResultType(result);
    }


    public VoteRedisResultType deleteVote(String userId, String eventId) {
        String result = executor.executeVoteScript(List.of(buildUserKey(userId, eventId), buildEventKey(eventId)),"null");
        return mapper.stringToVoteResultType(result);
    }


    public String getUserVote(String userId, String eventId) {
        return redisTemplate.opsForValue()
                .get("vote:user:" + userId + ":event:" + eventId);
    }

    public long getTotalVoteCount(String eventId) {
        return redisTemplate.opsForHash()
                .values("vote:event:" + eventId)
                .stream()
                .mapToLong(v -> Long.parseLong(v.toString()))
                .sum();
    }

}

