package com.example.votcha.votcha_search.api.listener;

import com.example.votcha.common.logging.ElasticSync;
import com.example.votcha.votcha_search.api.dto.event.EventCreatedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.EventDeletedSyncEvent;
import com.example.votcha.votcha_search.api.dto.event.VoteCountUpdatedSyncEvent;
import com.example.votcha.votcha_search.api.mapper.EventElasticMapper;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.model.OptionDocument;
import com.example.votcha.votcha_search.domain.repository.EventElasticRepository;
import com.example.votcha.votcha_search.service.EventIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventSyncListener {
    private final EventElasticRepository eventElasticRepository;
    private final EventIndexingService eventIndexingService;
    private final EventElasticMapper eventElasticMapper;

    @EventListener
    @Async
    @ElasticSync(
            action = "EVENT_CREATE_SYNC",
            index = "events",
            logDetails = "'Syncing event to ES: ' + #event.title"
    )
    public void handleEventCreated(EventCreatedSyncEvent event){

        List<OptionDocument> optionDocuments = event.options().stream()
                .map(eventElasticMapper::optionSyncToOptionDocument).toList();

        EventDocument document = eventElasticMapper
                .eventCreatedSyncToEventDocument(event, optionDocuments);

        eventElasticRepository.save(document);
    }

    @EventListener
    @Async
    @ElasticSync(
            action = "EVENT_DELETE_SYNC",
            index = "events",
            logDetails = "'Deleting user from ES: ' + #event.id"
    )
    public void handleEventDeleted(EventDeletedSyncEvent event){
        eventElasticRepository.deleteById(event.id());
    }

    @EventListener
    @Async
    @ElasticSync(
            action = "TOTAL_VOTE_COUNT_UPDATE_SYNC",
            index = "events",
            logDetails = "'Updating total vote count of event from ES: ' + #event.eventId"
    )
    public void handleUpdatedTotalVoteCount(VoteCountUpdatedSyncEvent event){
        eventIndexingService.updateEventCurrentVoteCount(event.eventId(),event.currentTotalVoteCount(),event.options());
    }


}
