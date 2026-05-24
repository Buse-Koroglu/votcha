package com.example.votcha.events.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UpdateEventRequestDto(
        @NotBlank(message = "title is required")
        @Size(max = 50, message = "title cannot exceed 50 characters")
        String title,

        @NotBlank(message = "description is required")
        @Size(max = 100, message = "description cannot exceed 100 characters")
        String description,

        @Future(message = "deadline must be in the future")
        Instant deadline

) {
}
