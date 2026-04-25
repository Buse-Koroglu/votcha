package com.example.votcha.votcha_search.api.controller;

import com.example.votcha.common.logging.BusinessAction;
import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.dto.response.EventSyncResponse;
import com.example.votcha.votcha_search.api.dto.response.UserSyncResponse;
import com.example.votcha.votcha_search.api.dto.response.VoteSyncResponse;
import com.example.votcha.votcha_search.service.EventIndexingService;
import com.example.votcha.votcha_search.service.UserIndexingService;
import com.example.votcha.votcha_search.service.VoteIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AdminSyncController implements AdminSyncApi {
    private final UserIndexingService userIndexingService;
    private final EventIndexingService eventIndexingService;
    private final VoteIndexingService voteIndexingService;

    @ElasticSync(
            action = "USER_BULK_SYNC",
            index = "users",
            logDetails = "'Bulk synchronization completed successfully'")
    @BusinessAction(action = "BULK_SYNC_TRIGGERED", domain = "ADMIN", logDetails = "'Admin triggered bulk user sync'")
    @Override
    public UserSyncResponse triggerUserSync() {
        return userIndexingService.syncAllUsers();
    }

    @ElasticSync(
            action = "EVENT_BULK_SYNC",
            index = "events",
            logDetails = "'Bulk synchronization completed successfully'")
    @BusinessAction(action = "BULK_SYNC_TRIGGERED", domain = "ADMIN", logDetails = "'Admin triggered bulk event sync'")
    @Override
    public EventSyncResponse triggerEventSync() {
        return eventIndexingService.syncAllEvents();
    }

    @ElasticSync(
            action = "VOTE_BULK_SYNC",
            index = "votes",
            logDetails = "'Bulk synchronization completed successfully'")
    @BusinessAction(action = "BULK_SYNC_TRIGGERED", domain = "ADMIN", logDetails = "'Admin triggered bulk vote sync'")
    @Override
    public VoteSyncResponse triggerVoteSync() { return voteIndexingService.syncAllVotes(); }
}
