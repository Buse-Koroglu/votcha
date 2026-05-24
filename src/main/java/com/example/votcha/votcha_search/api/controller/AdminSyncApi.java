package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.votcha_search.api.dto.response.EventSyncResponse;
import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
import com.example.votcha.votcha_search.api.dto.response.VoteSyncResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@RequestMapping("/api/admin/sync")
@Tag(name = "Data Synchronization", description = "Admin APIs for synchronizing data between database and Elasticsearch")
public interface AdminSyncApi {

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    UserSyncResponse triggerUserSync();

    @GetMapping("/events")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    EventSyncResponse triggerEventSync();

    @GetMapping("/votes")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    VoteSyncResponse triggerVoteSync();
}
