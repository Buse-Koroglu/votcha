package com.example.votcha.votes.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VoteRequestDto (
    @NotBlank(message = "option id can not be empty or blank")
    String optionId
) {
}
