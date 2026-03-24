package com.example.votify_meet.events.api.dto;

import com.example.votify_meet.events.domain.model.EventType;
import com.example.votify_meet.events.domain.model.Status;
import com.example.votify_meet.options.api.dto.OptionDetailResponseDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record EventDetailResponseDto(
        String id,
        String title,
        String description,
        Status status,
        EventType type,
        Instant createdAt,
        Instant updatedAt,
        Instant deadline,
        String creatorId,
        List<OptionDetailResponseDto> options
) {}
