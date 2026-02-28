package com.example.votify_meet.options.api.dto;

import lombok.Builder;

@Builder
public record OptionResponseDto(
    String id,
    String content
)
{}
