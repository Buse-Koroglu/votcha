package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
import com.example.votcha.votcha_search.service.UserIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AdminSyncController implements AdminSyncApi {
    private final UserIndexingService userIndexingService;

    @ElasticSync(
            action = "USER_BULK_SYNC",
            index = "users",
            logDetails = "'Bulk synchronization completed successfully'")
    @BusinessAction(action = "BULK_SYNC_TRIGGERED", domain = "ADMIN", logDetails = "'Admin triggered bulk user sync'")
    @Override
    public ResponseEntity<UserSyncResponse> triggerUserSync() {
        return ResponseEntity.ok(userIndexingService.syncAllUsers());
    }
}
