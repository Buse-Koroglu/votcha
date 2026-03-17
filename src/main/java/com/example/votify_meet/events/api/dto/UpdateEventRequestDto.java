package com.example.votify_meet.events.api.dto;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record UpdateEventRequestDto(
        @Size(min = 2, message = "title must be least at 2 characters") String title,
        @Size(min = 2, message = "description must be at least 2 characters") String description,

        @Future(message = "deadline must be in the future")
        Instant deadline,

        @NotNull(message = "option list cannot be null")
        @Size(min = 2, message = "an event must have at least 2 options")
        List<OptionRequestDto> options

) {
}
