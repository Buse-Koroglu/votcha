package com.example.votify_meet.users.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUsersRequestDto(@Size(min = 2,max = 50, message = "at least 2 character") String firstName,
                                    @Size(min = 2,max = 50, message = "at least 2 character") String lastName,
                                    @Email String email,
                                    @NotBlank(message = "password is required") @Size(min = 4,max = 12, message = "at least 4, max 12 character") String password) {}
