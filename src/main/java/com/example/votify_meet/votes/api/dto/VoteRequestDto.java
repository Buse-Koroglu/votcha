package com.example.votify_meet.votes.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VoteRequestDto (
    @NotBlank(message = "option id can not be empty or blank")
    String optionId
) {
}
