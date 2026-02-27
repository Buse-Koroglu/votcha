package com.example.votify_meet.users.api.dto;

import lombok.Builder;

@Builder
public record UsersResponseDto (String id,
                                String firstName,
                                String lastName,
                                String email) {}
