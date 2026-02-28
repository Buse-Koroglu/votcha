package com.example.votify_meet.options.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OptionRequestDto(
    @NotBlank(message = "content is required")
    @Size(min = 1,max = 200,message = "at least 1 character")
    String content
    )
{}
