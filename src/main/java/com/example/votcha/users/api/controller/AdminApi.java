package com.example.votcha.users.api.controller;

import com.example.votcha.users.api.dto.EmailRequest;
import com.example.votcha.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/admin/users")
@Tag(name = "Admin", description = "Admin Management APIs")
public interface AdminApi {
    @PatchMapping("/promote")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    ResponseEntity<Void> promoteToAdmin(@AuthenticationPrincipal Users user, @Valid @RequestBody EmailRequest request);

    @PatchMapping("/demote")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    ResponseEntity<Void> demoteToUser(@AuthenticationPrincipal Users user, @Valid @RequestBody EmailRequest request);
}
