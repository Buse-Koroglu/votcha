package com.example.votcha.redis.vote.service;

import com.example.votcha.redis.vote.dto.VoteRedisDto;
import com.example.votcha.redis.vote.dto.VoteRedisResultType;
import com.example.votcha.redis.vote.executor.VoteRedisLuaExecutor;
import com.example.votcha.redis.vote.mapper.VoteResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public VoteRedisResultType createVote(String userId, String eventId, String optionId, String voteId) {

        String result = executor.executeVoteScript(
                List.of(buildUserKey(userId, eventId), buildEventKey(eventId)),
                voteId,
                optionId,
                userId,
                "false"
        );

        return mapper.stringToVoteResultType(result);
    }


    public VoteRedisResultType updateVote(String userId, String eventId, String optionId, String voteId) {

        String result = executor.executeVoteScript(
                List.of(buildUserKey(userId, eventId), buildEventKey(eventId)),
                voteId,
                optionId,
                userId,
                "false"
        );

        return mapper.stringToVoteResultType(result);
    }


    public VoteRedisResultType deleteVote(String userId, String eventId) {

        String result = executor.executeVoteScript(
                List.of(buildUserKey(userId, eventId), buildEventKey(eventId)),
                "", "", "", "true"
        );

        return mapper.stringToVoteResultType(result);
    }


    public VoteRedisDto getUserVote(String userId, String eventId) {

        Map<Object, Object> map = redisTemplate.opsForHash()
                .entries(buildUserKey(userId, eventId));

        if (map == null || map.isEmpty()) return null;

        return new VoteRedisDto(
                Objects.toString(map.get("voteId"), null),
                Objects.toString(map.get("optionId"), null),
                Objects.toString(map.get("userId"), null)
        );
    }

    public long getTotalVoteCount(String eventId) {
        return redisTemplate.opsForHash()
                .values("vote:event:" + eventId)
                .stream()
                .mapToLong(v -> Long.parseLong(v.toString()))
                .sum();
    }

}

