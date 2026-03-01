package com.example.votify_meet.events.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UpdateEventRequestDto(
        @Size(min = 2, message = "title must be least at 2 characters") String title,
        @Size(min = 2, message = "description must be at least 2 characters") String description,

        @Future(message = "deadline must be in the future")
        Instant deadline


) {
}
