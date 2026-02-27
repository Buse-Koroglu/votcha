package com.example.votify_meet.events.api.dto;

import com.example.votify_meet.events.domain.model.EventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EventRequestDto(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "description is required")
        String description,

        @NotNull(message = "deadline is required")
        @Future(message = "deadline must be in the future")
        Instant deadline,

        @NotNull(message = "eventType is required")
        EventType eventType
) {}
