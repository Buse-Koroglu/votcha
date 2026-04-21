package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.dto.UserSyncResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/api/admin/sync")
@Tag(name = "Admin", description = "Admin Synchronization Management APIs")
public interface AdminSyncApi {

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<UserSyncResponse> triggerUserSync();
}
