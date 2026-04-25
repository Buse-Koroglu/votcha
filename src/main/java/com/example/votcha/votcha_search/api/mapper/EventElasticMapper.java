package com.example.votcha.votcha_search.api.mapper;

import com.example.votcha.events.domain.model.Event;
import com.example.votcha.votcha_search.api.dto.event.EventCreatedSyncEvent;
import com.example.votcha.votcha_search.domain.model.EventDocument;
import com.example.votcha.votcha_search.domain.model.OptionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventElasticMapper {
    private final OptionElasticMapper optionElasticMapper;

    public EventDocument eventToEventDocument(Event event, long totalVotes, List<OptionDocument> optionDocuments) {
        return EventDocument.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .deadline(event.getDeadline())
                .createdAt(event.getCreatedAt())
                .type(event.getType().name())
                .status(event.getStatus().name())
                .creatorName(event.getCreator().getFirstName() + " " + event.getCreator().getLastName())
                .creatorEmail(event.getCreator().getEmail())
                .totalVoteCount(totalVotes)
                .options(optionDocuments)
                .build();
    }
    public EventCreatedSyncEvent eventToEventCreatedSyncEvent(Event event, long totalVotes) {
        return EventCreatedSyncEvent.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .deadline(event.getDeadline())
                .createdAt(event.getCreatedAt())
                .type(event.getType().name())
                .status(event.getStatus().name())
                .creatorName(event.getCreator().getFirstName() + " " + event.getCreator().getLastName())
                .creatorEmail(event.getCreator().getEmail())
                .totalVoteCount(totalVotes)
                .options(
                        event.getOptions().stream()
                                .map(optionElasticMapper::optionToOptionSyncData)
                                .toList()
                ).build();
    }
    public EventDocument eventCreatedSyncToEventDocument(EventCreatedSyncEvent event, List<OptionDocument> optionDocuments) {
        return EventDocument.builder()
                .id(event.id())
                .title(event.title())
                .description(event.description())
                .deadline(event.deadline())
                .createdAt(event.createdAt())
                .type(event.type())
                .status(event.status())
                .creatorName(event.creatorName())
                .creatorEmail(event.creatorEmail())
                .totalVoteCount(event.totalVoteCount() != null ? event.totalVoteCount(): 0L)
                .options(optionDocuments)
                .build();
    }

}
