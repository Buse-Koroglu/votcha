package com.example.votcha.common.event;

import com.example.votcha.users.domain.model.Role;

import java.time.Instant;

public record UserCreatedSyncEvent (
        String id,
        String fullName,
        String email,
        Role role,
        Instant createdAt
){}
