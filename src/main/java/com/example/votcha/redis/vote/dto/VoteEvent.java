package com.example.votcha.redis.vote.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteEvent {
    private String userId;
    private String eventId;
    private String oldOptionId;
    private String newOptionId;
    private String action;
    private long timestamp;
}