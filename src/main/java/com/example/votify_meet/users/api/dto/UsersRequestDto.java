package com.example.votify_meet.users.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record UsersRequestDto (
                        @NotBlank(message = "firstname is required") String firstName,
                        @NotBlank(message = "lastname is required") String lastName,
                        @Email String email,
                        @NotBlank(message = "password is required") @Size(min = 4, max = 12, message = "min 4, max 12 characters") String password,
                        @NotBlank(message = "Please confirm your password") @Size(min = 4, max = 12, message = "min 4, max 12 characters")
                        String verifiedPassword) {}
