package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/admin/sync")
@Tag(name = "Data Synchronization", description = "Admin APIs for synchronizing data between database and Elasticsearch")
public interface AdminSyncApi {

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<UserSyncResponse> triggerUserSync();
}
