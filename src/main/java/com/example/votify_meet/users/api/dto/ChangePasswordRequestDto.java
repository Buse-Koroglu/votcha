package com.example.votify_meet.users.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank(message = "password is required") String currentPassword,
        @NotBlank(message = "password is required") @Size(min = 4,max = 12, message = "at least 4, max 12 character") String newPassword
){}
