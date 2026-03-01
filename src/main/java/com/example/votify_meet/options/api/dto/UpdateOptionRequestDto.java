package com.example.votify_meet.options.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateOptionRequestDto(
        @Size(min = 1,max = 50,message = "at least 1 character")
        String content) {}