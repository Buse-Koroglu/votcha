package com.example.votcha.redis.vote.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteRedisLuaExecutor {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<String> voteScript;

    public String executeVoteScript(List<String> keys, String... args) {
        return redisTemplate.execute(voteScript, keys, args);
    }
}