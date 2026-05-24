package com.example.votcha.redis.vote.util;

import org.springframework.stereotype.Component;

@Component
public class VoteRedisKeyBuilder {
    public String buildUserKey(String userId, String eventId) {
        return "vote:user:" + userId + ":event:" + eventId;
    }

    public String buildEventKey(String eventId) {
        return "vote:event:" + eventId;
    }

    // Creates a SET that stores voters for each event
    // When an event is deleted, it will help us to delete all votes in the deleted event
    public String buildEventUsersKey(String eventId) {return "vote:event:" + eventId + ":users";}

}
