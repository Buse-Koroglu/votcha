package com.example.votcha.votcha_search.api.listener;

import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.dto.event.VoteCreatedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.VoteDeletedSyncEvent;
import com.example.votcha.votcha_search.api.mapper.VoteElasticMapper;
import com.example.votcha.votcha_search.domain.repository.VoteElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VoteSyncListener {
    private final VoteElasticRepository voteElasticRepository;
    private final VoteElasticMapper voteElasticMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)    @Async
    @ElasticSync(
            action = "VOTE_CREATE_SYNC",
            index = "votes",
            logDetails = "'Syncing vote to ES: ' + #event.id"
    )
    public void handleVoteCreatedEvent(VoteCreatedSyncEvent event){
        voteElasticRepository.save(voteElasticMapper.voteCreatedSyncToVoteDocument(event));
    }

    @EventListener
    @Async
    @ElasticSync(
            action = "VOTE_DELETED_SYNC",
            index = "votes",
            logDetails = "'Deleting vote from ES: ' + #event.id"
    )
    public void handleVoteDeletedEvent(VoteDeletedSyncEvent event){
        voteElasticRepository.deleteById(event.id());
    }


}
