package com.example.votify_meet.events.api.dto;

import com.example.votify_meet.events.domain.model.EventType;
import com.example.votify_meet.events.domain.model.Status;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EventResponseDto(
        String id,
        String title,
        String description,
        Status status,
        EventType type,
        Instant createdAt,
        Instant deadline,
        String creatorId
){}
