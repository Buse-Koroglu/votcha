package com.example.votify_meet.votes.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VoteRequestDto (
    @NotNull(message = "option id can not be null")
    String optionId
) {
}
