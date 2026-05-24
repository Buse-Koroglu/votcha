package com.example.votcha.events.api.dto;

import com.example.votcha.events.domain.model.EventType;
import com.example.votcha.events.domain.model.Status;
import com.example.votcha.options.api.dto.OptionResponseDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record EventResponseDto(
        String id,
        String title,
        String description,
        Status status,
        EventType type,
        Instant createdAt,
        Instant updatedAt,
        Instant deadline,
        String creatorId,
        List<OptionResponseDto> options
){}
