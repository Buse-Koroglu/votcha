package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.votcha_search.api.dto.UserSyncResponse;
import com.example.votcha.votcha_search.service.UserIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class AdminSyncController implements AdminSyncApi {
    private final UserIndexingService userIndexingService;

    @BusinessAction(action = "BULK_SYNC_TRIGGERED", domain = "ADMIN", logDetails = "'Admin triggered bulk user sync'")
    @Override
    public ResponseEntity<UserSyncResponse> triggerUserSync() {
        return ResponseEntity.ok(userIndexingService.syncAllUsers());
    }
}
