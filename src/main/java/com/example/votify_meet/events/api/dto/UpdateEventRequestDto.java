package com.example.votify_meet.events.api.dto;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record UpdateEventRequestDto(
        @NotBlank(message = "title is required")
        @Size(max = 50, message = "title cannot exceed 50 characters")
        String title,

        @NotBlank(message = "description is required")
        @Size(max = 100, message = "description cannot exceed 100 characters")
        String description,

        @Future(message = "deadline must be in the future")
        Instant deadline,

        @NotNull(message = "option list cannot be null")
        @Size(min = 2, message = "an event must have at least 2 options")
        @Valid
        List<OptionRequestDto> options

) {
}
